/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.common.storage

import com.cobblemon.snap.common.api.pictures.PictureData
import java.util.UUID

class PictureDataStore {
    private val pictureMap = mutableMapOf<UUID, PictureData>()

    fun insertPicture(picture: PictureData) {
        pictureMap[picture.id] = picture
    }

    fun getPicture(id: UUID) = pictureMap[id]

    fun getAnyPicture() = pictureMap.values.firstOrNull()
}