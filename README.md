# 📺 Saf TV — Tanıtım ve Okubeni (Android TV, Telefon & Tablet Canlı Yayın Uygulaması)

<p align="center">
  <img src="saftv_banner.png" alt="Saf TV Tanıtım Görseli" width="100%" style="border-radius: 12px; box-shadow: 0 8px 30px rgba(0,0,0,0.5);" />
</p>

<p align="center">
  <a href="https://github.com/devezir/SafTV/releases/latest"><img src="https://img.shields.io/badge/S%C3%BCr%C3%BCm-v2.1.0_(vc_14)-blue?style=for-the-badge&logo=android" alt="Sürüm v2.1.0"></a>
  <a href="#"><img src="https://img.shields.io/badge/Boyut-Sadece_133_KB-brightgreen?style=for-the-badge" alt="Boyut 133 KB"></a>
  <a href="#"><img src="https://img.shields.io/badge/A%C3%A7%C4%B1l%C4%B1%C5%9F-0.1_Saniye-orange?style=for-the-badge" alt="0.1s Açılış Hızı"></a>
  <a href="#"><img src="https://img.shields.io/badge/Reklam-SIFIR_(%250)-red?style=for-the-badge" alt="Sıfır Reklam"></a>
  <a href="#"><img src="https://img.shields.io/badge/Kanal-61_Canl%C4%B1_Yay%C4%B1n-purple?style=for-the-badge" alt="61 Canlı Kanal"></a>
  <a href="#"><img src="https://img.shields.io/badge/Android-5.0%2B_(API_21%2B)-success?style=for-the-badge&logo=android" alt="Android 5.0+"></a>
</p>

---

## ⚡ Hızlı İndirme Bağlantıları

| Dosya | Boyut | Açıklama | Doğrudan İndir |
|---|---|---|:---:|
| 🚀 **Saf TV v2.1.0 (En Son Kararlı)** | **~133 KB** | Güncel, resmi imzalı en son APK | [⬇️ **SafTV_v2.1.0_vc14.apk İndir**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v2.1.0_vc14.apk) |
| 📱 **SafTV.apk (Sabit Bağlantı)** | **~133 KB** | Daima en son stabil sürümü işaret eder | [⬇️ **SafTV.apk İndir**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV.apk) |
| 📦 **Tüm 14 Sürümün Arşivi (ZIP)** | **~745 KB** | v1.0.0'dan v2.1.0'a tüm APK'lar tek pakette | [📦 **SafTV_All_Releases_Archive.zip**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_All_Releases_Archive.zip) |
| 📊 **Kanal Kataloğu (Excel)** | **~24 KB** | 61 kanalın kategori ve akış tablosu | [📊 **SafTV_Kanal_Listesi.xlsx**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_Kanal_Listesi.xlsx) |
| 📜 **Değişim Günlüğü** | — | Tüm sürümlerin kronolojik teknik detayları | [📜 **DEGISIM_GUNLUGU.md İncele**](DEGISIM_GUNLUGU.md) |

---

## 🌟 Neden "Saf TV"?

Piyasadaki popüler televizyon uygulamalarının neredeyse tamamı (İnat Box vb.) 30–50 MB boyutundadır; açılışta ve kanal geçişlerinde tam ekran reklamlar, video kütüphaneleri, kullanıcı takipçileri (AdMob, Unity Ads, Firebase, OneSignal) ve cihazı yoran hantal arayüzlerle doludur.

**Saf TV**, bu fazlalıkların tamamından arındırılarak **saf performans, hız ve minimalist estetik** ilkeleri üzerine sıfırdan inşa edilmiştir:

### 📊 Karşılaştırma Tablosu

| Kriter | Sıradan TV Uygulamaları (İnat Box vb.) | 📺 **Saf TV (v2.1.0)** |
|---|---|:---:|
| **Uygulama Boyutu** | 35 MB – 55 MB (Aşırı şişkin) | 🟢 **Sadece 133 KB (300 kat daha hafif!)** |
| **Açılış Hızı (İlk Tıklama)** | 4 – 8 saniye (Bekletici reklamlar ve yükleme) | 🟢 **0.1 saniye (Tıklandığı an canlı yayında)** |
| **Reklamlar & Tanıtımlar** | Var (Açılış, geçiş, banner, video reklam) | 🟢 **SIFIR (%100 Reklamsız, sonsuza dek)** |
| **RAM (Bellek) Tüketimi** | 180 MB – 350 MB (Cihazı ısıtır ve dondurur) | 🟢 **25 MB – 35 MB (Eski TV Box'larda bile yağ gibi)** |
| **Kullanıcı Takipçileri** | 8-15 analitik ve reklam kütüphanesi | 🟢 **SIFIR (İzleyici yok, tam kullanıcı mahremiyeti)** |
| **Video Kod Çözücü** | Şişkin üçüncü parti ExoPlayer paketleri | 🟢 **Android Saf Donanım Hızlandırması (`MediaPlayer`)** |
| **Cihaz Uyumu** | Sadece dokunmatik veya sadece kumanda | 🟢 **Hibrit: Android TV (Kumanda) + Mobil (Dokunmatik)** |
| **Güncelleme Hızı** | Kırık linkler, açılmayan sunucular | 🟢 **GitHub Releases & Raw CDN ile anlık OTA** |

---

## 🚀 Başlıca Özellikler

### 📏 1. Yüzdesel Hassas Arayüz Ölçeği (%80 — %150)
* Android TV, Google TV, TV Box, Tablet ve Telefonlar için IPTV & TiviMate endüstri standardı ölçekleme mimarisi:
  * `[● Otomatik]`: Cihazı akıllıca tespit eder (Televizyonda %130, Mobilde %100).
  * `[%80 (Ultra Kompakt)]`: Tek ekranda 12'den fazla kanal listelemek isteyenlere.
  * `[%90 (Kompakt)]`: Küçük ekranlı telefonlar için ideal yerleşim.
  * `[%100 (Standart Mobil)]`: Standart dikey/yatay mobil düzen.
  * `[%115 (Tablet / Yakın TV)]`: Tabletler, katlanabilir ekranlar veya 1-2 metre mesafeler için.
  * `[%130 (Büyük TV / 3m)]`: 3 metreden kumandayla net okunan standart televizyon boyutu.
  * `[%150 (Dev / Uzak TV)]`: 65"+ dev ekranlar ve görme kolaylığı isteyen kullanıcılar için devasa puntolar.
* **Dinamik Çekmece Genişliği:** Kanal listesi genişliği ölçekle orantılı hesaplanır (`270dp * ölçek`) ve ekranın %52'sini asla aşmayarak arkadaki canlı yayının görünürlüğünü garanti eder.

### 📱 2. Resim İçinde Resim (PiP - Yüzen Küçük Pencere)
* Android 8.0 (API 26) ve üzeri cihazlarda üst bardaki `[🗗 PiP]` butonuyla veya yayındayken **Ana Ekran (Home)** tuşuna basıldığında küçülerek ekranda yüzen pencereye geçer.
* WhatsApp'ta yazışırken, internette gezinirken veya başka bir uygulamadayken canlı televizyon yayını kesintisiz devam eder.

### 🌙 3. Akıllı Uyku Zamanlayıcısı
* `Kapalı`, `15`, `30`, `60`, `90`, `120 dakika` seçenekleriyle ayarlanabilir.
* Canlı kalan süre saniye hassasiyetinde (`⏳ 28:45 kaldı`) anlık gösterilir.
* Süre bittiğinde yayını durdurarak uygulamayı nazikçe ve tamamen kapatır (`finishAffinity()`).

### 🎧 4. Ekran Kapalı Dinleme Modu (Arka Planda Ses)
* Telefon kilitlendiğinde veya ekran kapatıldığında yayının sesi çalmaya devam eder.
* Haber, açık oturum veya müzik kanallarını dinlerken muazzam batarya tasarrufu sağlar.

### 🚪 5. Onay Pencereli Güvenli Çıkış
* Canlı yayındayken yanlışlıkla GERİ tuşuna basılması durumunda ekrana şık bir onay kutusu gelir:
  > *"Saf TV'den çıkmak istiyor musunuz? [✕ Vazgeç] [✓ Evet, Çık]"*

### ⏸️ 6. Canlı Yayını Duraklatma & Oynatma
* Dokunmatik ekranda ortadaki `[⏸ / ▶]` butonu veya TV kumandasının Play/Pause tuşları ile canlı yayın anlık dondurulabilir ve tampondan oynatılabilir.

### 🎬 7. Çoklu Çözünürlük & Kalite Seçimi
* `Otomatik`, `1080p Full HD`, `720p HD`, `480p SD` ve `360p Tasarruf` modları.
* Kumandada **Yeşil Tuş**, mobilde ise **[📶 Kalite]** butonu ile tek tıkla hızlı kalite paneli.

### ⭐ 8. Favori Kanallar & Hızlı Arama
* Kumandada **Sarı Tuş**, mobilde ise **[★ Favori]** butonu ile kanalları yıldızlama.
* Çekmece açıldığında arama çubuğuna harf yazıldıkça 61 kanal arasından anında canlı filtreleme.

### 📶 9. Otomatik Ağ Kopması Koruması
* Wi-Fi veya Ethernet koptuğunda kullanıcıyı yormayan akıllı uyarı (`"📶 İnternet Bağlantısı Kesildi!"`).
* İnternet geri geldiği anda canlı yayın otomatik taze bağlantıyla kaldığı yerden devam eder.

### 💾 10. Tarih Damgalı Ayarları Yedekleme & Geri Yükleme
* Tercihleriniz, favorileriniz, aktif/pasif kanallarınız ve arayüz ölçeğiniz `saf_tv_yedek_YYYYMMDD_HHmmss.json` formatında tek tıkla cihaz hafızasına yedeklenebilir ve geri yüklenebilir.

---

## 📦 Tüm 14 Sürümün Ayrı Ayrı APK İndirme Listesi

| Sürüm | Sürüm Kodu | Boyut | Doğrudan İndir (.apk) |
|:---:|:---:|:---:|:---:|
| **v2.1.0** | 14 | ~133 KB | [⬇️ SafTV_v2.1.0_vc14.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v2.1.0_vc14.apk) |
| **v2.0.0** | 13 | ~129 KB | [⬇️ SafTV_v2.0.0_vc13.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v2.0.0_vc13.apk) |
| **v1.9.0** | 12 | ~117 KB | [⬇️ SafTV_v1.9.0_vc12.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.9.0_vc12.apk) |
| **v1.8.0** | 11 | ~113 KB | [⬇️ SafTV_v1.8.0_vc11.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.8.0_vc11.apk) |
| **v1.7.0** | 10 | ~109 KB | [⬇️ SafTV_v1.7.0_vc10.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.7.0_vc10.apk) |
| **v1.6.0** | 9 | ~97 KB | [⬇️ SafTV_v1.6.0_vc9.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.6.0_vc9.apk) |
| **v1.5.0** | 8 | ~97 KB | [⬇️ SafTV_v1.5.0_vc8.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.5.0_vc8.apk) |
| **v1.4.1** | 7 | ~89 KB | [⬇️ SafTV_v1.4.1_vc7.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.4.1_vc7.apk) |
| **v1.4.0** | 6 | ~89 KB | [⬇️ SafTV_v1.4.0_vc6.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.4.0_vc6.apk) |
| **v1.3.1** | 5 | ~77 KB | [⬇️ SafTV_v1.3.1_vc5.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.3.1_vc5.apk) |
| **v1.3.0** | 4 | ~77 KB | [⬇️ SafTV_v1.3.0_vc4.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.3.0_vc4.apk) |
| **v1.2.0** | 3 | ~61 KB | [⬇️ SafTV_v1.2.0_vc3.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.2.0_vc3.apk) |
| **v1.1.0** | 2 | ~57 KB | [⬇️ SafTV_v1.1.0_vc2.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.1.0_vc2.apk) |
| **v1.0.0** | 1 | ~53 KB | [⬇️ SafTV_v1.0.0_vc1.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.0.0_vc1.apk) |

---

## 🎮 Kontrol ve Kullanım Rehberi

### 📺 Android TV Kumandası İle:
* **Yukarı / Aşağı Tuşları:** Kanalları değiştirir.
* **Sol / Sağ Tuşları:** Önceki / Sonraki kanala geçer.
* **OK (Orta Tuş):** Ekranın solundan Kanal Listesi Çekmecesini açar.
* **Play / Pause Tuşları:** Canlı yayını duraklatır / devam ettirir.
* **Yeşil Tuş:** Yayın kalitesini seçer (1080p, 720p, 480p).
* **Sarı Tuş:** Kanalı Favorilere (⭐) ekler / çıkarır.
* **Mavi Tuş:** Ekranı tam ekran veya orantılı (16:9 Doğal) yapar.
* **Menü Tuşu:** 4 Sekmeli Ayarlar Menüsünü açar (Kanallar, Tercihler, Yedekleme, Hakkında).
* **Geri Tuşu:** Menüleri kapatır; canlı yayındayken Onaylı Çıkış Penceresini açar.
* **CH+ / CH-:** 5'er kanal hızlı atlar.
* **Rakam Tuşları (0-9):** Doğrudan kanal numarasına zıplar.

### 📱 Telefon & Tablet Dokunmatiği İle:
* **Yukarı / Aşağı Kaydırma:** Sıradaki veya önceki kanala anında geçer.
* **Soldan Sağa Çekme:** Kanal listesi çekmecesini açar.
* **Ekrana Tek Dokunma:** Bilgi kutusunu, üst butonları ve oynat/duraklat tuşunu gösterir / gizler.
* **Ekrana Çift Dokunma:** Ekran oranını değiştirir (*16:9 Doğal / Tam Ekran*).
* **[🗗 PiP] Butonu:** Yayını ekranda yüzen küçük pencereye dönüştürür.
* **Ortadaki [⏸ / ▶] Butonu:** Canlı yayını duraklatır veya devam ettirir.
* **Ekran Butonları:** `[📶 Kalite]`, `[📺 Ekran]`, `[🗗 PiP]`, `[⚙️ Ayarlar]`, `[🚪 Çıkış]`, `[◀ Önceki]`, `[▶ Sonraki]`.

---

## 📋 Uygulamadaki 61 Canlı Kanal Listesi

Tüm yayınlar yayıncı kuruluşların doğrudan resmi CDN sunucularından şifresiz ve yasal HLS (`.m3u8`) protokolüyle çekilir:

```
📺 ULUSAL (14 Kanal)
├── TRT 1 HD
├── Kanal D HD
├── ATV HD
├── Show TV HD
├── Star TV HD
├── NOW TV HD (FOX)
├── TV8 HD
├── Kanal 7 HD
├── A2 TV HD
├── Teve2 HD
├── Beyaz TV HD
├── 360 TV HD
├── TV4 HD
└── TRT Türk HD

📰 HABER & EKONOMİ (16 Kanal)
├── TRT Haber HD
├── NTV HD
├── HaberTürk HD
├── A Haber HD
├── Sözcü TV HD
├── Haber Global HD
├── Halk TV HD
├── 24 TV HD
├── TGRT Haber HD
├── Bloomberg HT HD
├── Tele1 HD
├── TVNET HD
├── Flash Haber HD
├── TRT 3 / TBMM TV HD
├── CNN Türk HD
└── DHA Canlı HD

⚽ SPOR (6 Kanal)
├── TRT Spor HD
├── TRT Spor Yıldız HD
├── A Spor HD
├── TV8.5 HD
├── HT Spor HD
└── FB TV HD

🦁 BELGESEL & YAŞAM (6 Kanal)
├── TRT Belgesel HD
├── DMAX HD
├── TLC HD
├── TGRT Belgesel HD
├── National Geographic Wild HD
└── Çiftçi TV HD

👶 ÇOCUK & EĞİTİM (7 Kanal)
├── TRT Çocuk HD
├── Minika Çocuk HD
├── Minika GO HD
├── TRT Diyanet Çocuk HD
├── TRT EBA İlkokul HD
├── TRT EBA Ortaokul HD
└── TRT EBA Lise HD

🎵 MÜZİK (4 Kanal)
├── TRT Müzik HD
├── Power TV HD
├── Dream Türk HD
└── Kral Pop TV HD

🕌 KÜLTÜR & TEMATİK (8 Kanal)
├── TRT 2 Kültür Sanat HD
├── Diyanet TV HD
├── TRT World HD
├── TRT Avaz HD
├── TRT Arabi HD
├── TRT Kurdî HD
├── Lalegül TV HD (3 Kalite)
└── Semerkand TV HD
```

---

## 📲 Kurulum Rehberi

### 📱 Android Telefon veya Tablete Kurulum:
1. [SafTV_v2.1.0_vc14.apk](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v2.1.0_vc14.apk) bağlantısına tıklayarak APK dosyasını indirin.
2. İndirilen dosyaya dokunun ve **"Yükle"** deyin *(Bilinmeyen kaynaklara izin vermeniz istenebilir)*.
3. Uygulama saniyesinde açılacak ve canlı yayın hemen başlayacaktır!

### 📺 Android TV veya TV Box'a Kurulum:
* **Yöntem 1 (Kablosuz — En Kolayı):**
  1. Hem telefonunuza hem televizyonunuza Google Play'den ücretsiz **"Send Files to TV"** uygulamasını kurun.
  2. Telefonunuza indirdiğiniz `SafTV.apk` dosyasını bu uygulama üzerinden TV'nize gönderin ve TV'deki dosya yöneticisinden açıp yükleyin.
* **Yöntem 2 (USB Bellek İle):**
  1. `SafTV.apk` dosyasını bir USB belleğe kopyalayın.
  2. USB belleği TV'nize takın ve TV'deki herhangi bir Dosya Yöneticisi ile APK'ya tıklayıp kurun.

---

## 🛠️ Kaynak Koddan Derleme

Saf TV hiçbir harici kütüphane gerektirmez. Saf Android SDK araçlarıyla tek komutta derlenir:

```bash
git clone https://github.com/devezir/SafTV.git
cd SafTV
python build.py
```
*Derleme çıktısı saniyeler içinde kök dizinde üretilir.*

---

## ⚖️ Yasal Uyarı & Lisans

* **Saf TV**, bir yayın sağlayıcısı değildir; bünyesinde herhangi bir video akışı barındırmaz veya saklamaz.
* Uygulamadaki tüm TV yayınları, yayıncı kuruluşların internet üzerinden herkese açık ve şifresiz olarak sunduğu resmi HLS (`.m3u8`) akışlarıdır.
* Saf TV tamamen **açık kaynaklıdır**; ticari bir amaç gütmez, sıfır reklam içerir ve hiçbir kullanıcı verisini toplamaz.

---

<p align="center">
  <b>Saf TV — Gerçekten Saf, Işık Hızında Televizyon Deneyimi.</b><br/>
  Geliştirici: <a href="https://github.com/devezir">@devezir</a>
</p>
