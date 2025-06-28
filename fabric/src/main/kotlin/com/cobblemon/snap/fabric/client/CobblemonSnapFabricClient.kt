/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.fabric.client

import com.cobblemon.snap.common.api.client.CobblemonSnapClientImpl
import com.cobblemon.snap.common.client.CobblemonSnapClient
import com.cobblemon.snap.fabric.client.event.FabricClientPlatformEventHandler
import net.fabricmc.api.ClientModInitializer

class CobblemonSnapFabricClient : CobblemonSnapClientImpl(), ClientModInitializer {
    override fun initialize() {
        CobblemonSnapClient.implementation = this
        FabricClientPlatformEventHandler.registerListeners()
        CobblemonSnapClient.initialize()
    }

    override fun onInitializeClient() {
        this.initialize()
    }

}
