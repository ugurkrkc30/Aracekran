package com.aracekran.app

data class Vehicle(val name: String, val screen: String, val wired: Boolean, val wireless: Boolean, val note: String)

// Not: kablosuz destek bilgileri üretici verisine göre doğrulanmalı. Listeye yeni araç eklemek için satır eklemek yeterli.
val VEHICLES = listOf(
    Vehicle("Fiat Egea / Egea Cross (2021+) – Uconnect 10.25\"", "10.25\"", true, true, "Kablosuz Android Auto'yu araç ayarlarından açın."),
    Vehicle("Fiat Egea (2021+) – Uconnect 7\"", "7\"", true, false, "Yalnızca kablolu bağlantı. Orijinal ya da veri destekli kablo kullanın."),
    Vehicle("Diğer araç – Android Auto (kablolu)", "-", true, false, "Veri destekli USB kablosu kullanın."),
    Vehicle("Diğer araç – Android Auto (kablosuz)", "-", true, true, "Telefonda Bluetooth ve Wi-Fi açık olmalı."),
)

fun steps(v: Vehicle, wireless: Boolean): List<String> {
    val s = mutableListOf(
        "Telefonda Android Auto uygulamasını güncelleyin.",
        "Android Auto ayarlarında 'Sürüm' satırına 10 kez dokunun, 'Geliştirici ayarları'nı açın.",
        "Geliştirici ayarlarında 'Bilinmeyen kaynaklar'ı etkinleştirin.",
    )
    s += if (wireless && v.wireless) listOf("Aracın ekranında kablosuz Android Auto'yu seçip telefonu eşleştirin.")
         else listOf("Telefonu veri destekli kabloyla araca bağlayın.")
    s += listOf(
        "Bu uygulamada 'Yansıtmayı başlat'a basıp ekran kaydı iznini verin.",
        "Araç ekranındaki Android Auto menüsünden 'Araç Ekran'ı açın. Telefonu açıp kullanmak istediğiniz uygulamayı başlatın.",
    )
    return s
}
