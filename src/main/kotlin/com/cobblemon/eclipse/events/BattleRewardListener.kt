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

/**
 * Rewards players with Impactor currency for winning wild Pokémon battles.
 */
object BattleRewardListener {

    private val REWARD_AMOUNT: BigDecimal = BigDecimal(100.00)
    private const val REWARD_CURRENCY_KEY = "event_points" // change as needed

    fun onInitialize() {
        CobblemonEvents.BATTLE_VICTORY.subscribe { event: BattleVictoryEvent ->
            val economy = Impactor.instance().services().provide(EconomyService::class.java)
            val playerMap = event.battle.players.associateBy { it.uuid }

            // ✅ Only reward if all losers are wild Pokémon (no UUIDs)
            val allWild = event.losers.all { actor -> actor.getPlayerUUIDs().none() }
            if (!allWild) return@subscribe

            // ✅ Correct currency lookup
            val currencyProvider = economy.currencies()
            val currencies: Set<Currency> = currencyProvider.registered()
            val currency = currencies.firstOrNull {
                it.key().equals(REWARD_CURRENCY_KEY)
            } ?: return@subscribe

            // ✅ Reward each winner
            event.winners.forEach { actor ->
                actor.getPlayerUUIDs().forEach { uuid ->
                    economy.account(currency, uuid).thenAccept { account ->
                        val finalAccount = account ?: createAccount(uuid, currency)
                        finalAccount.deposit(REWARD_AMOUNT)

                        // Notify the player if online
                        playerMap[uuid]?.sendSystemMessage(
                            Component.literal("You earned ${REWARD_AMOUNT.toPlainString()} ${currency.key()} for winning a battle!")
                        )
                    }
                }
            }
        }
    }

    /** Creates an account for the player if missing. */
    private fun createAccount(owner: UUID, currency: Currency): Account {
        return Account.builder()
            .owner(owner)
            .currency(currency)
            .balance(BigDecimal.ZERO)
            .build()
    }
}