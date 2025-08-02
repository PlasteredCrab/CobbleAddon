package com.cobblemon.eclipse.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import net.impactdev.impactor.api.Impactor
import net.impactdev.impactor.api.economy.EconomyService
import net.impactdev.impactor.api.economy.accounts.Account
import net.impactdev.impactor.api.economy.currency.Currency
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.network.chat.Component
import java.math.BigDecimal
import java.util.*

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
                                        .executes { ctx ->
                                            val economy = Impactor.instance().services()
                                                .provide(EconomyService::class.java)
                                            val profiles = GameProfileArgument.getGameProfiles(ctx, "target")
                                            val amount = BigDecimal.valueOf(DoubleArgumentType.getDouble(ctx, "amount"))
                                            val currencyName = StringArgumentType.getString(ctx, "currency")

                                            // Convert currencies to Iterable
                                            val currencies = economy.currencies() as Iterable<Currency>
                                            val currency = currencies.firstOrNull {
                                                it.key().equals(currencyName)
                                            }

                                            if (currency == null) {
                                                ctx.source.sendFailure(
                                                    Component.literal("Unknown currency: $currencyName")
                                                )
                                                return@executes 0
                                            }

                                            profiles.forEach { profile ->
                                                // Async account retrieval
                                                economy.account(currency, profile.id)
                                                    .thenAccept { account ->
                                                        val finalAccount = account ?: createAccount(profile.id, currency)
                                                        finalAccount.deposit(amount)
                                                    }
                                            }

                                            ctx.source.sendSuccess(
                                                { Component.literal("Gave ${amount.toPlainString()} $currencyName to ${profiles.joinToString { it.name }}") },
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
     * Creates an account for the player with the specified currency if it does not exist.
     */
    private fun createAccount(owner: UUID, currency: Currency): Account {
        return Account.builder()
            .owner(owner)
            .currency(currency)
            .balance(BigDecimal.ZERO)
            .build()
    }
}