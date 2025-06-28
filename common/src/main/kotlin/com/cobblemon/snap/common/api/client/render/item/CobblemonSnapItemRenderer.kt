/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.common.api.client.render.item

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

interface CobblemonSnapItemRenderer {
    fun render(
        entity: LivingEntity?,
        itemStack: ItemStack,
        displayContext: ItemDisplayContext,
        leftHand: Boolean,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        level: Level?,
        combinedLight: Int,
        combinedOverlay: Int,
        seed: Int
    )
}