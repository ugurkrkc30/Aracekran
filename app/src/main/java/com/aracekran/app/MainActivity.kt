package com.aracekran.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContent { MaterialTheme { Screen() } }
    }

    @Composable
    fun Screen() {
        var vi by remember { mutableIntStateOf(0) }
        var wireless by remember { mutableStateOf(true) }
        val v = VEHICLES[vi]
        val sp = getSharedPreferences("p", MODE_PRIVATE)
        var lock by remember { mutableStateOf(sp.getBoolean("lock", false)) }
        val perm = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
        val proj = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
            if (r.data != null) startForegroundService(Intent(this, MirrorService::class.java)
                .putExtra("code", r.resultCode).putExtra("data", r.data))
        }
        Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp).statusBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Araç Ekran", style = MaterialTheme.typography.headlineMedium)
            Text("Aracınızı seçin", style = MaterialTheme.typography.titleMedium)
            VEHICLES.forEachIndexed { i, x ->
                Row { RadioButton(selected = i == vi, onClick = { vi = i }); Text(x.name, Modifier.padding(top = 12.dp)) }
            }
            if (v.wireless) Row { Switch(wireless, { wireless = it }); Text("  Kablosuz bağlantı", Modifier.padding(top = 12.dp)) }
            Text(v.note)
            Text("Kurulum adımları", style = MaterialTheme.typography.titleMedium)
            steps(v, wireless && v.wireless).forEachIndexed { i, s -> Text("${i + 1}. $s") }
            Button(onClick = {
                perm.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS))
                proj.launch((getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager).createScreenCaptureIntent())
            }) { Text("Yansıtmayı başlat") }
            Row { Switch(lock, { lock = it; sp.edit().putBoolean("lock", it).apply() }); Text("  Park kilidi (hız 5 km/s'i geçince kes)", Modifier.padding(top = 12.dp)) }
            Text("Not: Ön ekran sürücünün görüş alanındadır; sürücünün sürüş sırasında video izlemesi trafikte dikkat dağıtır ve ceza gerektirebilir.")
        }
    }
}
