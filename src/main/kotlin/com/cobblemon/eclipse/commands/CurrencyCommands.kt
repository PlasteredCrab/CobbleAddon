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
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.network.chat.Component
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.CompletableFuture

object CurrencyCommands {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal<CommandSourceStack>("givecurrency")
                .requires { it.hasPermission(2) }
                .then(
                    argument<CommandSourceStack, GameProfileArgument.Result>("target", GameProfileArgument.gameProfile())
                        .then(
                            argument<CommandSourceStack, Double>("amount", DoubleArgumentType.doubleArg(0.0))
                                .then(
                                    argument<CommandSourceStack, String>("currency", StringArgumentType.string())
                                        .suggests { ctx, builder -> suggestCurrencies(builder) }
                                        .executes { ctx ->
                                            val economy = Impactor.instance().services()
                                                .provide(EconomyService::class.java)
                                            val profiles = GameProfileArgument.getGameProfiles(ctx, "target")
                                            val amount = BigDecimal.valueOf(DoubleArgumentType.getDouble(ctx, "amount"))
                                            val inputName = StringArgumentType.getString(ctx, "currency")

                                            val currencyProvider = economy.currencies()
                                            val currencies: Set<Currency> = currencyProvider.registered()

                                            val currency = currencies.firstOrNull {
                                                val fullKey = it.key().asString() // e.g., "impactor:event_points"
                                                fullKey.equals(inputName, ignoreCase = true)
                                                        || fullKey.substringAfter(":").equals(inputName, ignoreCase = true)
                                            }

                                            if (currency == null) {
                                                ctx.source.sendFailure(Component.literal("Unknown currency: $inputName"))
                                                return@executes 0
                                            }

                                            profiles.forEach { profile ->
                                                economy.account(currency, profile.id).thenAccept { account ->
                                                    val finalAccount = account ?: createAccount(profile.id, currency)
                                                    finalAccount.deposit(amount)
                                                }
                                            }

                                            ctx.source.sendSuccess(
                                                { Component.literal("Gave ${amount.toPlainString()} ${currency.key()} to ${profiles.joinToString { it.name }}") },
                                                false
                                            )

                                            1
                                        }
                                )
                        )
                )
        )
    }

    /**
     * Suggests currency keys without requiring namespace
     */
    private fun suggestCurrencies(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val economy = Impactor.instance().services().provide(EconomyService::class.java)
        val currencies = economy.currencies().registered()

        currencies.forEach {
            val fullKey = it.key().asString() // Convert Key -> "namespace:value"
            val shortKey = fullKey.substringAfter(":")

            // Suggest short key
            if (shortKey.startsWith(builder.remaining, ignoreCase = true)) {
                builder.suggest(shortKey)
            }
            // Suggest full key
            if (fullKey.startsWith(builder.remaining, ignoreCase = true)) {
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
