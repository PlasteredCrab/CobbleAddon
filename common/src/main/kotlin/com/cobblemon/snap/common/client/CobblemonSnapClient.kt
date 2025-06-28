/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.common.client

import com.cobblemon.snap.common.api.client.CobblemonSnapClientImpl
import com.cobblemon.snap.common.storage.PictureDataStore
import com.mojang.blaze3d.pipeline.MainTarget
import com.mojang.blaze3d.pipeline.RenderTarget

object CobblemonSnapClient {
    lateinit var implementation: CobblemonSnapClientImpl

    lateinit var pictureBuffer: RenderTarget
    val pictureDataStore = PictureDataStore()

    fun initialize() {

    }

    fun initPictureBuffer() {
        pictureBuffer = MainTarget(854, 480)
    }
}
