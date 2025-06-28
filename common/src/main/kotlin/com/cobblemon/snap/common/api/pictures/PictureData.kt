/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.common.api.pictures

import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.writeEnumConstant
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ExtraCodecs
import java.nio.ByteBuffer
import java.util.Optional
import java.util.UUID

class PictureData(
    val data: ByteBuffer?,
    val type: PictureEncoding,
    var id: UUID
) {


    companion object {
        val CODEC: MapCodec<PictureData> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.BYTE_BUFFER.optionalFieldOf("data").forGetter { Optional.ofNullable(it.data) },
                Codec.STRING.fieldOf("type").forGetter { it.type.toString() },
                Codec.STRING.fieldOf("id").forGetter { it.id.toString() }
            ).apply(instance) { data, type, id ->
                PictureData(
                    data.orElse(null),
                    PictureEncoding.valueOf(type),
                    UUID.fromString(id)
                )
            }
        }

        val PACKET_CODEC: StreamCodec<ByteBuf, PictureData> = ByteBufCodecs.fromCodec(CODEC.codec())
    }


}