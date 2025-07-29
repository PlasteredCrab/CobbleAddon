package com.cobblemon.eclipse.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import net.impactdev.impactor.api.Impactor
import net.impactdev.impactor.api.economy.EconomyService
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.network.chat.Component
import java.math.BigDecimal

object CurrencyCommands {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {

        literal<CommandSourceStack>("givecurrency")
            .requires { it.hasPermission(2) }
            .then(
                argument<CommandSourceStack, GameProfileArgument.Result>(
                    "target", GameProfileArgument.gameProfile()
                ).then(
                    argument<CommandSourceStack, Double>(
                        "amount", DoubleArgumentType.doubleArg(0.0)
                    ).executes { ctx ->
                        val economy = Impactor.instance().services()
                            .provide(EconomyService::class.java)
                        val profiles = GameProfileArgument.getGameProfiles(ctx, "target")
                        val amount = BigDecimal.valueOf(DoubleArgumentType.getDouble(ctx, "amount"))

                        profiles.forEach { profile ->
                            economy.account(profile.id).thenAccept { account ->
                                account.deposit(amount)
                            }
                        }

                        ctx.source.sendSuccess(
                            { Component.literal("Gave ${amount.toPlainString()} coins to ${profiles.joinToString { it.name }}") },
                            false
                        )

                        1
                    }
                )
            )
        dispatcher.register(
            literal<CommandSourceStack>("givecurrency")
                .requires { it.hasPermission(2) }
                .then(
                    argument<CommandSourceStack, GameProfileArgument.Result>("target", GameProfileArgument.gameProfile())
                        .then(
                            argument<CommandSourceStack, Double>("amount", DoubleArgumentType.doubleArg(0.0))
                                .executes { ctx ->
                                    val economy = Impactor.instance().services().provide(EconomyService::class.java)
                                    val profiles = GameProfileArgument.getGameProfiles(ctx, "target")
                                    val amount = BigDecimal.valueOf(DoubleArgumentType.getDouble(ctx, "amount"))

                                    profiles.forEach { profile ->
                                        economy.account(profile.id).thenAccept { account ->
                                            account.deposit(amount)
                                        }
                                    }

                                    ctx.source.sendSuccess(
                                        { Component.literal("Gave ${amount.toPlainString()} coins to ${profiles.joinToString { it.name }}") },
                                        false
                                    )

                                    1
                                }
                        )
                )
        )
    }
}
