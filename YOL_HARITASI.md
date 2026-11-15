# 🗺️ Saf TV — Yol Haritası ve Kararlar (Roadmap)

Bu doküman; kullanıcının kararları doğrultusunda **Saf TV** projesinde kesinlikle istenmeyen (iptal edilen), şimdilik beklemede tutulan (aklımızdaki) ve aktif olarak geliştirilen tüm özellikleri ve mimari kararları kayıt altında tutar.

---

## 🏛️ Temel Felsefe (Asla Değişmeyecek İlkeler)
* **Ultra Hafif:** Harici şişkin kütüphane, reklam SDK'sı (AdMob vb.) veya analitik takipçi (Firebase vb.) kesinlikle eklenmeyecek.
* **Işık Hızında Açılış:** Cold boot 0.1 saniye bandında kalacak.
* **Saf Donanım:** Yalnızca Android yerel `MediaPlayer` ve `SurfaceView` donanım hızlandırması kullanılacak.
* **Hibrit Deneyim:** Hem Android TV kumandasıyla hem de Android telefon/tablet dokunmatik ekranıyla pürüzsüz çalışacak.

---

## ❌ Kesinlikle İstenmeyenler (İptal Edilenler)

| Konu | Neden İstenmiyor / İptal Edildi? | Karar Tarihi |
|---|---|---|
| **🖼️ Kanal Logoları** | APK boyutunu gereksiz yere megabaytlarca şişireceği ve bellek (RAM) tüketimini artıracağı için iptal edildi. Kanal adları ve metin tabanlı tasarım korunacak. | 14.11.2026 |
| **🏬 Google Play Store'a Yükleme** | Uygulama içinde hazır 51 adet Türk TV kanalı bulunduğu için Google Play telif (DMCA / Re-broadcast) kurallarına takılır; geliştirici hesabının kapatılma riski vardır. Dağıtım doğrudan APK, GitHub veya bağımsız kanallarla yapılacak. | 14.11.2026 |

---

## ⏳ Aklımdakiler / Şimdilik Beklemede Olanlar

| Konu | Açıklama | Durum |
|---|---|---|
| **📱 Ses ve Parlaklık Dikey Kaydırma Jestleri** | Telefon ve tablette video izlerken ekranın solundan parlaklık, sağından ses ayarlama özelliği. | Şimdilik düşünülmüyor, ancak felsefeye uygun olduğu için ileride değerlendirilmek üzere cepte. |
| **📁 Harici M3U Dosya / Link Desteği** | Kullanıcının kendi kanal listesini USB veya link ile yükleyebilmesi. | Şimdilik düşünülmüyor, ileride evrensel bir oynatıcıya evrilmek istenirse bakılacak. |
| **📺 EPG (Canlı Yayın Akışı)** | Ekranda "Şu an: Ana Haber Bülteni (20:00 - 21:00)" şeklinde program bilgisi gösterme. | Hafif SAX parser ile yapılabilir, şimdilik beklemede. |
| **💤 TV Uyku / Otomatik Kapanma** | Belirlenen süre sonunda yayını durdurup uygulamayı kapatma (`finishAffinity`) ve TV ana ekranına dönme. | Android güvenlik kısıtı nedeniyle TV donanımı doğrudan kapatılamadığı için beklemede. |
| **⚙️ İlk Kurulumda 15–20 Kanalın Aktif Gelmesi** | İlk kurulumda tüm kanallar yerine en popüler 15-20 kanalın aktif başlaması, diğerlerinin ayarlardan isteğe bağlı açılması. | Kullanıcı kararıyla "Yapmayı Düşündüklerimiz" listesine eklendi, şimdilik beklemede tutuluyor. |

---

## 🚀 Aktif Geliştirme (v1.5.0 ve Sonrası)

* [x] **Kalıcı Release Keystore Üretimi:** Test anahtarı yerine sabit 2048-bit `saftv-release.jks` anahtarı üretilecek; güncellemelerde imza çakışması ve Play Protect uyarısı engellenecek.
* [x] **Ağ / İnternet Kopması Bildirimi:** Wi-Fi veya Ethernet kesildiğinde kullanıcıya bildirim verme (`"📶 İnternet Bağlantısı Yok"`), internet geri geldiğinde kanalı otomatik taze başlatma.
* [x] **Kanal Listesi Canlı Filtreleme (Hızlı Arama):** Çekmece üstünde harf yazıldıkça 51 kanal arasından anında süzen kompakt arama çubuğu.
* [x] **Çekmece Kompakt Kanal Listesi (v1.7.0):** Tek satır kanal kartı ve sıkılaştırılmış başlık/kategori marjinleri ile dikeyde 1 kanal yerine aynı anda en az 5 kanalın rahatça görünmesi.
* [x] **Üst Bar Çakışma & Taşma Koruması (v1.7.0):** Büyük fontta ve dar ekranlarda OSD ve dokunmatik butonların birbirinin üzerine binmesini engelleyen iki satırlı akış düzeni.
* [x] **Tarih Damgalı Ayarları Dışa / İçe Aktarma (v1.7.0):** İzin gerektirmeyen harici dizine `saf_tv_yedek_YYYYMMDD_HHmmss.json` formatında ayarları yedekleme ve tek tıkla geri yükleme.
* [x] **Hakkında & Güncellemeleri Denetle (v1.7.0):** Ayarlar paneli altında sürüm bilgisi ve Archive.org üzerinden çevrim içi sürüm kontrolü.
* [x] **WhatsApp Link Görseli (v1.7.0):** Archive.org linki paylaşıldığında varsayılan bina simgesi yerine şık Saf TV afişinin çıkması için `__ia_thumb.jpg` güncellemesi.
