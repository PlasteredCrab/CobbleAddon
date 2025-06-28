/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.snap.common.api.event

import com.cobblemon.mod.common.api.reactive.CancelableObservable

object PlatformEvents {
    //Client side events
    val LEFT_CLICK_EVENT = CancelableObservable<LeftClickEvent>()
}