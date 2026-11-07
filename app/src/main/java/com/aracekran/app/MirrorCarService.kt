package com.aracekran.app

import android.content.Intent
import androidx.car.app.*
import androidx.car.app.model.*
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.validation.HostValidator

class MirrorCarService : CarAppService() {
    override fun createHostValidator(): HostValidator = HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    override fun onCreateSession(): Session = object : Session() {
        override fun onCreateScreen(intent: Intent): Screen = MirrorScreen(carContext)
    }
}

class MirrorScreen(ctx: CarContext) : Screen(ctx) {
    init {
        ctx.getCarService(AppManager::class.java).setSurfaceCallback(object : SurfaceCallback {
            override fun onSurfaceAvailable(c: SurfaceContainer) {
                MirrorBus.w = c.width; MirrorBus.h = c.height; MirrorBus.dpi = c.dpi
                MirrorBus.carSurface = c.surface; MirrorBus.onSurface?.invoke()
            }
            override fun onSurfaceDestroyed(c: SurfaceContainer) {
                MirrorBus.carSurface = null; MirrorBus.onSurface?.invoke()
            }
        })
    }
    override fun onGetTemplate(): Template = NavigationTemplate.Builder()
        .setActionStrip(ActionStrip.Builder().addAction(
            Action.Builder().setTitle("Kapat").setOnClickListener { carContext.finishCarApp() }.build()).build())
        .build()
}
