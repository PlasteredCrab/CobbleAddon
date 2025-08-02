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
 * Listens for Cobblemon battles finishing and rewards the winners with ImpactOR currency.
 */
object BattleRewardListener {

    private val REWARD_AMOUNT: BigDecimal = BigDecimal(100.00)

    // Change this to the key of the currency you want to give
    private const val REWARD_CURRENCY_KEY = "event_points"

    fun onInitialize() {
        CobblemonEvents.BATTLE_VICTORY.subscribe { event: BattleVictoryEvent ->
            val economy = Impactor.instance().services().provide(EconomyService::class.java)

            val playerMap = event.battle.players.associateBy { it.uuid }

            // Skip if any loser is a player (prevents cheesing by fighting each other)
            if (event.losers.any { actor -> actor.getPlayerUUIDs().any { uuid -> playerMap.containsKey(uuid) } }) {
                return@subscribe
            }

            // Find the currency in Impactor
            val currencies = economy.currencies() as Iterable<Currency>
            val currency = currencies.firstOrNull { it.key().equals(REWARD_CURRENCY_KEY) }
                ?: return@subscribe  // Exit if currency not found

            // Reward each winner
            event.winners.forEach { actor ->
                actor.getPlayerUUIDs().forEach { uuid ->
                    economy.account(currency, uuid).thenAccept { account ->
                        val finalAccount = account ?: createAccount(uuid, currency)
                        finalAccount.deposit(REWARD_AMOUNT)

                        // Notify the player if they're online
                        playerMap[uuid]?.sendSystemMessage(
                            Component.literal("You earned ${REWARD_AMOUNT.toPlainString()} ${currency.key()} for winning a battle!")
                        )
                    }
                }
            }
        }
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