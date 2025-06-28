/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.common.client.render

import com.cobblemon.snap.common.CobblemonSnap
import com.cobblemon.snap.common.api.client.render.item.CobblemonSnapItemRenderer
import com.cobblemon.snap.common.client.CobblemonSnapClient
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.platform.TextureUtil
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.joml.Quaternionf
import org.joml.Vector3f
//import org.lwjgl.opengl.GREMEDYStringMarker

class PictureItemRenderer : CobblemonSnapItemRenderer {
    var texId: Int? = null
    override fun render(
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
    ) {
        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        val image = CobblemonSnapClient.pictureDataStore.getAnyPicture()
        poseStack.pushPose()
        image?.let { imageBuf ->
            val nativeImage = NativeImage.read(imageBuf.data!!)
            val maxLen = Math.max(nativeImage.width, nativeImage.height)
            val maxX = (nativeImage.width.toFloat() / maxLen.toFloat()) * 0.75F
            val maxY = (nativeImage.height.toFloat() / maxLen.toFloat()) * 0.75F

            val rotationQuaternion = Quaternionf().rotateY(Math.toRadians(180.0).toFloat())

            if (displayContext == ItemDisplayContext.FIXED) {
                poseStack.rotateAround(rotationQuaternion, 0.5F, 0.5F, 0F)
                poseStack.translate(0.4F, -0.6F, 0F)
                poseStack.scale(1.6F,1.6F,-1.6F)
            }
            else
                poseStack.translate(-0.5F, 0.1F, 0F)
            val pose = poseStack.last()
            buffer.addVertex(pose, Vector3f(maxX, 0f, 0f))
            buffer.setUv(1f, 1f)
            buffer.addVertex(pose, Vector3f(maxX, maxY, 0f))
            buffer.setUv(1f, 0f)
            buffer.addVertex(pose, Vector3f(0F, maxY, 0f))
            buffer.setUv(0f, 0f)
            buffer.addVertex(pose, Vector3f(0F, 0f, 0f))
            buffer.setUv(0f, 1f)

            //Eventually, we will need a different texId for each size of the textures
            if (texId == null) {
                texId = TextureUtil.generateTextureId()
                TextureUtil.prepareImage(texId!!, 0, nativeImage.width, nativeImage.height)
                CobblemonSnap.LOGGER.info(texId)
                nativeImage.upload(0,0,0, false)
            }
            RenderSystem.disableCull()
            RenderType.cutout().setupRenderState()
            RenderSystem.setShader(GameRenderer::getPositionTexShader)
            RenderSystem.setShaderTexture(0, texId!!)
            //GREMEDYStringMarker.glStringMarkerGREMEDY("TEST")
            val builtBuffer = buffer.buildOrThrow()
            BufferUploader.drawWithShader(builtBuffer)
            builtBuffer.close()
            nativeImage.close()
            RenderType.cutout().clearRenderState()
            imageBuf.data.rewind()

        }
        poseStack.popPose()
    }

}