/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.common.event.handler

import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.item.PokedexItem
import com.cobblemon.snap.common.CobblemonSnap.LOGGER
import com.cobblemon.snap.common.client.CobblemonSnapClient
import com.cobblemon.snap.common.api.event.LeftClickEvent
import com.cobblemon.snap.common.api.pictures.PictureData
import com.cobblemon.snap.common.api.pictures.PictureEncoding
import com.mojang.authlib.minecraft.client.MinecraftClient
import com.mojang.blaze3d.pipeline.RenderTarget
import net.minecraft.client.Minecraft
import net.minecraft.client.Screenshot
import net.minecraft.world.level.material.MapColor
import org.lwjgl.system.MemoryUtil
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.util.UUID
import javax.imageio.ImageIO

object PokedexScanHandler {
    var isTakingPicture = false

    fun onLeftClick(event: LeftClickEvent) {
        if (event.heldItem?.item is PokedexItem) {
            val isScanning = CobblemonClient.pokedexUsageContext.scanningGuiOpen
            if (isScanning) {

                val image = Screenshot.takeScreenshot(CobblemonSnapClient.pictureBuffer)
                val imageData = image.asByteArray()
                val imageDataBuffer = MemoryUtil.memAlloc(imageData.size)
                imageDataBuffer.put(imageData)
                imageDataBuffer.rewind()

                // Decode byte array to BufferedImage
                val bufferedImage = ImageIO.read(imageData.inputStream())

                // Save cropped image for debugging
                ImageIO.write(bufferedImage, "png", File("buffered_image.png"))
                LOGGER.info("Saved buffered image to buffered_image.png")

                // Crop the image to the desired ratio based on a hardcoded value
                val ratioType = 1 // Example hardcoded value: 1=1:1, 2=1:2, 3=2:1
                val croppedImage = cropImage(bufferedImage, ratioType)

                // Save cropped image for debugging
                ImageIO.write(croppedImage, "png", File("cropped_image.png"))
                LOGGER.info("Saved cropped image to cropped_image.png")

                /*// Optionally, convert the image to map colors
                val mapColors = MapColor.MATERIAL_COLORS.filterNotNull().toTypedArray()
                val convertedImage = convertToMapColors(croppedImage, mapColors)

                // Save for debugging
                ImageIO.write(convertedImage, "png", File("converted_image.png"))
                LOGGER.info("Saved converted image to converted_image.png")*/


                val pictureData = PictureData(
                        MemoryUtil.memAlloc(croppedImage.width * croppedImage.height * 4)
                                .put(convertedImageToBytes(croppedImage)).rewind(),
                        PictureEncoding.NATIVEIMAGE,
                        UUID.randomUUID()
                )
                CobblemonSnapClient.pictureDataStore.insertPicture(pictureData)
                event.cancel()
            }
        }

    }

    fun cropImage(image: BufferedImage, ratioType: Int): BufferedImage {
        val width = image.width.toDouble()
        val height = image.height.toDouble()

        // Define desired aspect ratio and target dimensions
        val (desiredAspectRatio, targetWidth, targetHeight) = when (ratioType) {
            1 -> Triple(1.0 / 1.0, 128, 128) // 1:1 ratio (128x128)
            2 -> Triple(1.0 / 2.0, 128, 256) // 1:2 ratio (128x256)
            3 -> Triple(2.0 / 1.0, 256, 128) // 2:1 ratio (256x128)
            else -> Triple(width / height, width.toInt(), height.toInt())
        }

        val imageAspectRatio = width / height

        // Calculate crop dimensions
        val (cropWidth, cropHeight) = if (imageAspectRatio >= desiredAspectRatio) {
            // Image is wider than desired aspect ratio
            val cropHeight = height
            val cropWidth = cropHeight * desiredAspectRatio
            Pair(cropWidth, cropHeight)
        } else {
            // Image is taller than desired aspect ratio
            val cropWidth = width
            val cropHeight = cropWidth / desiredAspectRatio
            Pair(cropWidth, cropHeight)
        }

        // Calculate the top-left corner (x, y) for cropping
        val x = ((width - cropWidth) / 2.0).toInt().coerceAtLeast(0)
        val y = ((height - cropHeight) / 2.0).toInt().coerceAtLeast(0)

        // Ensure crop dimensions are within image bounds
        val cropWidthInt = cropWidth.toInt().coerceAtLeast(1)
        val cropHeightInt = cropHeight.toInt().coerceAtLeast(1)
        val maxCropWidth = (image.width - x).coerceAtMost(cropWidthInt)
        val maxCropHeight = (image.height - y).coerceAtMost(cropHeightInt)

        // Crop the image
        val croppedImage = image.getSubimage(x, y, maxCropWidth, maxCropHeight)

        // Resize to target dimensions
        return resizeImage(croppedImage, targetWidth, targetHeight)
    }

    // Helper function to resize a BufferedImage
    fun resizeImage(image: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
        val resizedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = resizedImage.createGraphics()
        graphics.drawImage(image, 0, 0, targetWidth, targetHeight, null)
        graphics.dispose()
        return resizedImage
    }

    fun convertToMapColors(image: BufferedImage, mapColors: Array<MapColor>): BufferedImage {
        val convertedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)

        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val color = Color(image.getRGB(x, y), true)
                val nearestColorIndex = nearestColor(mapColors, color)
                convertedImage.setRGB(x, y, mapColors[nearestColorIndex].col)
            }
        }
        return convertedImage
    }

    fun nearestColor(mapColors: Array<MapColor>, color: Color): Int {
        var closestColorIndex = 0
        var minDistance = Double.MAX_VALUE

        for (i in mapColors.indices) {
            val mapColor = Color(mapColors[i].col)
            val distance = colorDistance(color.red, color.green, color.blue, mapColor.red, mapColor.green, mapColor.blue)

            if (distance < minDistance) {
                minDistance = distance
                closestColorIndex = i
            }
        }
        return closestColorIndex
    }

    fun colorDistance(r1: Int, g1: Int, b1: Int, r2: Int, g2: Int, b2: Int): Double {
        val dr = (r1 - r2).toDouble()
        val dg = (g1 - g2).toDouble()
        val db = (b1 - b2).toDouble()
        return Math.sqrt(dr * dr + dg * dg + db * db)
    }

    fun convertedImageToBytes(image: BufferedImage): ByteArray {
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "png", baos)
        return baos.toByteArray()
    }
}