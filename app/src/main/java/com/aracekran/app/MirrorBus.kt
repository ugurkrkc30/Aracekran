package com.aracekran.app

import android.view.Surface

object MirrorBus {
    @Volatile var carSurface: Surface? = null
    @Volatile var w = 0
    @Volatile var h = 0
    @Volatile var dpi = 160
    @Volatile var onSurface: (() -> Unit)? = null
}
