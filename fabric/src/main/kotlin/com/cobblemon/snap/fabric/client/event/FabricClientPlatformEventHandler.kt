/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.fabric.client.event

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.snap.common.CobblemonSnap
import com.cobblemon.snap.common.api.event.LeftClickEvent
import com.cobblemon.snap.common.api.event.PlatformEvents
import com.mojang.authlib.minecraft.client.MinecraftClient
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.InteractionHand

object FabricClientPlatformEventHandler {
    fun registerListeners() {
        ClientPreAttackCallback.EVENT.register { client, player, tickCount ->
            if (tickCount != 0) {
                CobblemonSnap.LOGGER.error("Click")
                PlatformEvents.LEFT_CLICK_EVENT.postThen(
                    LeftClickEvent(player, player.getItemInHand(InteractionHand.MAIN_HAND)),
                    { return@register true },
                    { return@register false }
                )
            }
            return@register false
        }
    }
}