package com.cobblemon.eclipse.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.impactdev.impactor.api.Impactor
import net.impactdev.impactor.api.economy.EconomyService
import net.impactdev.impactor.api.economy.accounts.Account
import net.impactdev.impactor.api.economy.currency.Currency
import net.kyori.adventure.key.Key
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.network.chat.Component
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.CompletableFuture

object CurrencyCommands {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        // /givecurrency <target> <amount> <currency>
        dispatcher.register(
            literal<CommandSourceStack>("givecurrency")
                .requires { it.hasPermission(2) }
                .then(
                    argument<CommandSourceStack, GameProfileArgument.Result>("target", GameProfileArgument.gameProfile())
                        .then(
                            argument<CommandSourceStack, Double>("amount", DoubleArgumentType.doubleArg(0.0))
                                .then(
                                    argument<CommandSourceStack, String>("currency", StringArgumentType.string())
                                        .suggests { _, builder -> suggestCurrencies(builder) }
                                        .executes { ctx ->
                                            val economy = Impactor.instance().services().provide(EconomyService::class.java)
                                            val profiles = GameProfileArgument.getGameProfiles(ctx, "target")
                                            val amount = BigDecimal.valueOf(DoubleArgumentType.getDouble(ctx, "amount"))
                                            val input = StringArgumentType.getString(ctx, "currency")

                                            val currency = resolveCurrency(economy, input)
                                            if (currency == null) {
                                                ctx.source.sendFailure(Component.literal("Unknown currency: $input"))
                                                return@executes 0
                                            }

                                            profiles.forEach { profile ->
                                                // Impactor API is async
                                                economy.account(currency, profile.id).thenAccept { account ->
                                                    val acc = account ?: createAccount(profile.id, currency)
                                                    acc.deposit(amount)
                                                }
                                            }

                                            ctx.source.sendSuccess(
                                                { Component.literal("Gave ${amount.toPlainString()} ${currency.key().asString()} to ${profiles.joinToString { it.name }}") },
                                                false
                                            )
                                            1
                                        }
                                )
                        )
                )
        )

        // /listcurrencies (debug helper)
        dispatcher.register(
            literal<CommandSourceStack>("listcurrencies")
                .requires { it.hasPermission(2) }
                .executes { ctx ->
                    val economy = Impactor.instance().services().provide(EconomyService::class.java)
                    val list = economy.currencies().registered().map { it.key().asString() }
                    ctx.source.sendSuccess(
                        { Component.literal("Registered currencies: $list") },
                        false
                    )
                    1
                }
        )
    }

    /** Resolve either "impactor:event_points" or shorthand "event_points". */
    private fun resolveCurrency(economy: EconomyService, input: String): Currency? {
        val currencies = economy.currencies().registered()
        val lower = input.lowercase(Locale.ROOT)

        // Try exact full key match (case-insensitive)
        currencies.firstOrNull { it.key().asString().equals(lower, ignoreCase = true) }?.let { return it }

        // Try shorthand match against the path part
        return currencies.firstOrNull {
            val full = it.key().asString()              // e.g. "impactor:event_points"
            val short = full.substringAfter(':')        // e.g. "event_points"
            short.equals(lower, ignoreCase = true)
        }
    }

    /** Tab suggestions: show short names first, then full names. */
    private fun suggestCurrencies(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val economy = Impactor.instance().services().provide(EconomyService::class.java)
        val currencies = economy.currencies().registered()
        val remaining = builder.remaining.lowercase(Locale.ROOT)

        // Short suggestions: "currency", "event_points", "cosmetic_points"
        currencies.forEach {
            val shortKey = it.key().asString().substringAfter(':')
            if (shortKey.startsWith(remaining, ignoreCase = true)) {
                builder.suggest(shortKey)
            }
        }
        // Full suggestions: "impactor:currency", etc.
        currencies.forEach {
            val fullKey = it.key().asString()
            if (fullKey.startsWith(remaining, ignoreCase = true)) {
                builder.suggest(fullKey)
            }
        }
        return builder.buildFuture()
    }

    private fun createAccount(owner: UUID, currency: Currency): Account {
        return Account.builder()
            .owner(owner)
            .currency(currency)
            .balance(BigDecimal.ZERO)
            .build()
    }
}