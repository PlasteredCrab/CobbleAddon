/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.common

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.snap.common.api.CobblemonSnapImplementation
import com.cobblemon.snap.common.api.event.PlatformEvents
import com.cobblemon.snap.common.event.handler.PokedexScanHandler
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object CobblemonSnap {
    const val MODID = "cobblemonsnap"
    val LOGGER: Logger = LogManager.getLogger()
    lateinit var implementation: CobblemonSnapImplementation
    
    fun initialize() {
        implementation.registerItems()

        registerEventHandlers()
    }

    private fun registerEventHandlers() {
        PlatformEvents.LEFT_CLICK_EVENT.subscribe(Priority.NORMAL, PokedexScanHandler::onLeftClick)
    }


}
