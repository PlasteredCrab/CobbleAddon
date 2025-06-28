package com.cobblemon.snap.common.mixin.client;

import com.cobblemon.mod.common.client.pokedex.PokedexScannerRenderer;
import com.cobblemon.snap.common.client.CobblemonSnapClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL40;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokedexScannerRenderer.class)
public class PokedexScannerRendererMixin {
    @Inject(
        method = "onRenderOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
        at = @At(value = "HEAD")
    )
    public void cobblemonsnap$copyFramebuffer(
        GuiGraphics graphics,
        DeltaTracker tickCounter,
        CallbackInfo ci
    ) {
        GlStateManager._glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, Minecraft.getInstance().getMainRenderTarget().frameBufferId);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, CobblemonSnapClient.pictureBuffer.frameBufferId);
        GL40.glBlitFramebuffer(
            0,
            0,
            CobblemonSnapClient.pictureBuffer.width,
            CobblemonSnapClient.pictureBuffer.height,
            0,
            0,
            CobblemonSnapClient.pictureBuffer.width,
            CobblemonSnapClient.pictureBuffer.height,
            GL30C.GL_COLOR_BUFFER_BIT | GL30C.GL_DEPTH_BUFFER_BIT | GL30C.GL_STENCIL_BUFFER_BIT,
            GL30C.GL_NEAREST
        );
        GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, Minecraft.getInstance().getMainRenderTarget().frameBufferId);
    }
}
