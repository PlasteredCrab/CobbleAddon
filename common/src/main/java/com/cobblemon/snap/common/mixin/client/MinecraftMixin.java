package com.cobblemon.snap.common.mixin.client;

import com.cobblemon.snap.common.client.CobblemonSnapClient;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow @Final private Window window;

    @Shadow @Final public static boolean ON_OSX;

    @Inject(
        method = "<init>(Lnet/minecraft/client/main/GameConfig;)V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/MainTarget;<init>(II)V")
    )
    private void cobblemonsnap$initCustomFramebuffer(
        GameConfig gameConfig,
        CallbackInfo ci
    ) {
        CobblemonSnapClient.INSTANCE.initPictureBuffer();
    }

    @Inject(
        method = "resizeDisplay()V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;resize(IIZ)V")
    )
    private void cobblemonsnap$resizeDisplay(CallbackInfo ci) {
        CobblemonSnapClient.pictureBuffer.resize(this.window.getWidth(), this.window.getHeight(), ON_OSX);
    }
}
