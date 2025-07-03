package com.cobblemon.eclipse.events

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import net.impactdev.impactor.api.Impactor
import net.impactdev.impactor.api.economy.EconomyService
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import java.math.BigDecimal

/**
 * Listens for Cobblemon battles finishing and rewards the winners with coins.
 */
object BattleRewardListener {

    private val REWARD_AMOUNT: BigDecimal = BigDecimal(100.00)

    fun onInitialize() {
        CobblemonEvents.BATTLE_VICTORY.subscribe { event: BattleVictoryEvent ->
            val economy = Impactor.instance().services().provide(EconomyService::class.java)

            val playerMap = event.battle.players.associateBy { it.uuid }

            // Exit early if any loser is a player so those stinkers don't cheese the system
            if (event.losers.any { actor -> actor.getPlayerUUIDs().any { uuid -> playerMap.containsKey(uuid) } }) {
                return@subscribe
            }

            event.winners.forEach { actor ->
                actor.getPlayerUUIDs().forEach { uuid ->
                    economy.account(uuid).thenAccept { account ->
                        account.deposit(REWARD_AMOUNT)

                        playerMap[uuid]?.sendSystemMessage(
                            Component.literal("You earned ${REWARD_AMOUNT.toPlainString()} coins for winning a battle!")
                        )
                    }
                }
            }
        }
    }
}