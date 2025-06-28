/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.common.mixin.client;

import com.cobblemon.snap.common.api.client.render.item.CobblemonSnapItemRenderer;
import com.cobblemon.snap.common.client.render.CobblemonSnapItemRenderOverrides;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(
        at = @At("HEAD"),
        method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V",
        cancellable = true
    )
    public void cobblemonsnap$renderItemOverrides(
        @Nullable LivingEntity entity,
        ItemStack itemStack,
        ItemDisplayContext displayContext,
        boolean leftHand,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        @Nullable Level level,
        int combinedLight,
        int combinedOverlay,
        int seed,
        CallbackInfo ci
    ) {
        if (CobblemonSnapItemRenderOverrides.INSTANCE.hasOverride(itemStack)) {
            CobblemonSnapItemRenderer renderer = CobblemonSnapItemRenderOverrides.INSTANCE.getOverride(itemStack);
            renderer.render(entity, itemStack, displayContext, leftHand, poseStack, bufferSource, level, combinedLight, combinedOverlay, seed);
            ci.cancel();
        }
    }
}
