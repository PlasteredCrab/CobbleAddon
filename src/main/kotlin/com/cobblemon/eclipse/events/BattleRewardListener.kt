package com.cobblemon.eclipse.events

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import net.impactdev.impactor.api.Impactor
import net.impactdev.impactor.api.economy.EconomyService
import net.impactdev.impactor.api.economy.accounts.Account
import net.impactdev.impactor.api.economy.currency.Currency
import net.minecraft.network.chat.Component
import java.math.BigDecimal
import java.util.*

/** Rewards players with Impactor currency for winning wild Pokémon battles. */
object BattleRewardListener {

    private val REWARD_AMOUNT: BigDecimal = BigDecimal(100)
    // You can set just "event_points" or the full "impactor:event_points".
    private const val REWARD_CURRENCY_KEY: String = "event_points"

    fun onInitialize() {
        CobblemonEvents.BATTLE_VICTORY.subscribe { event: BattleVictoryEvent ->
            val economy = Impactor.instance().services().provide(EconomyService::class.java)
            val playerMap = event.battle.players.associateBy { it.uuid }

            // Only reward if all losers are wild (no player UUIDs)
            val allWild = event.losers.all { actor -> actor.getPlayerUUIDs().none() }
            if (!allWild) return@subscribe

            val currency = resolveCurrency(economy, REWARD_CURRENCY_KEY) ?: return@subscribe

            event.winners.forEach { actor ->
                actor.getPlayerUUIDs().forEach { uuid ->
                    economy.account(currency, uuid).thenAccept { account ->
                        val acc = account ?: createAccount(uuid, currency)
                        acc.deposit(REWARD_AMOUNT)

                        playerMap[uuid]?.sendSystemMessage(
                            Component.literal("You earned ${REWARD_AMOUNT.toPlainString()} ${currency.key().asString()} for winning a battle!")
                        )
                    }
                }
            }
        }
    }

    private fun resolveCurrency(economy: EconomyService, input: String): Currency? {
        val currencies = economy.currencies().registered()
        val lower = input.lowercase(Locale.ROOT)

        currencies.firstOrNull { it.key().asString().equals(lower, ignoreCase = true) }?.let { return it }

        return currencies.firstOrNull {
            val full = it.key().asString()
            val short = full.substringAfter(':')
            short.equals(lower, ignoreCase = true)
        }
    }

    private fun createAccount(owner: UUID, currency: Currency): Account {
        return Account.builder()
            .owner(owner)
            .currency(currency)
            .balance(BigDecimal.ZERO)
            .build()
    }
}