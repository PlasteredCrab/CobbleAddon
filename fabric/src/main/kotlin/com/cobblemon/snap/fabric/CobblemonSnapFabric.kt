/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.fabric

import com.cobblemon.snap.common.CobblemonSnap
import com.cobblemon.snap.common.api.CobblemonSnapImplementation
import com.cobblemon.snap.common.item.CobblemonSnapItems
import net.fabricmc.api.ModInitializer
import net.minecraft.core.Registry

class CobblemonSnapFabric : CobblemonSnapImplementation(), ModInitializer {
    fun initialize() {
        CobblemonSnap.implementation = this
        CobblemonSnap.initialize()
    }

    override fun registerItems() {
        CobblemonSnapItems.register { id, item ->
            Registry.register(CobblemonSnapItems.registry, id, item)
        }
    }

    override fun onInitialize() {
        this.initialize()
    }

}
