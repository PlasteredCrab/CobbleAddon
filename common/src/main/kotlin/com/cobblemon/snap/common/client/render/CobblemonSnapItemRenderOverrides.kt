/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.common.client.render

import com.cobblemon.snap.common.api.client.render.item.CobblemonSnapItemRenderer
import com.cobblemon.snap.common.item.PictureItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import kotlin.reflect.KClass

object CobblemonSnapItemRenderOverrides {
    val overrides = mutableMapOf<KClass<out Item>, CobblemonSnapItemRenderer>()

    val PICTURE = register(PictureItem::class, PictureItemRenderer())

    fun register(clazz: KClass<out Item>, renderer: CobblemonSnapItemRenderer): CobblemonSnapItemRenderer {
        overrides[clazz] = renderer
        return renderer
    }

    fun hasOverride(itemStack: ItemStack): Boolean {
        val item = itemStack.item
        return overrides.keys.contains(item::class)
    }

    fun getOverride(itemStack: ItemStack): CobblemonSnapItemRenderer {
        val item = itemStack.item
        return overrides[item::class]!!
    }
}