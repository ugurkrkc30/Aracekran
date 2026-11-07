package com.aracekran.app

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.location.LocationListener
import android.location.LocationManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper

/** Telefon ekranını araç yüzeyine yansıtır. İsteğe bağlı park kilidi açıksa hareket halinde keser (varsayılan kapalı). */
class MirrorService : Service() {
    private var proj: MediaProjection? = null
    private var vd: VirtualDisplay? = null
    private var moving = false
    private val h = Handler(Looper.getMainLooper())
    private val loc by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private val ll = LocationListener { l -> moving = parkLock() && l.hasSpeed() && l.speed * 3.6f > 5f; h.post { attach() } }

    private fun parkLock() = getSharedPreferences("p", MODE_PRIVATE).getBoolean("lock", false)

    override fun onBind(i: Intent?): IBinder? = null

    @SuppressLint("MissingPermission")
    override fun onStartCommand(i: Intent?, f: Int, id: Int): Int {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel("m", "Yansıtma", NotificationManager.IMPORTANCE_LOW))
        startForeground(1, Notification.Builder(this, "m").setContentTitle("Araç Ekran yansıtıyor")
            .setSmallIcon(android.R.drawable.ic_menu_view).build())
        val code = i?.getIntExtra("code", 0) ?: 0
        val data = i?.getParcelableExtra<Intent>("data")
        if (data == null) { stopSelf(); return START_NOT_STICKY }
        val mpm = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        proj = mpm.getMediaProjection(code, data).also { it.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() { stopSelf() } }, h) }
        MirrorBus.onSurface = { h.post { attach() } }
        try { loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, ll) } catch (_: Exception) {}
        attach()
        return START_NOT_STICKY
    }

    private fun attach() {
        val s = MirrorBus.carSurface
        if (s == null || moving) { vd?.release(); vd = null; return }
        if (vd == null && MirrorBus.w > 0) {
            vd = proj?.createVirtualDisplay("arac", MirrorBus.w, MirrorBus.h, MirrorBus.dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, s, null, h)
        }
    }

    override fun onDestroy() {
        try { loc.removeUpdates(ll) } catch (_: Exception) {}
        vd?.release(); proj?.stop(); MirrorBus.onSurface = null
    }
}
