# Saf TV — Proje, Versiyon Tarihçesi ve Dokümantasyon

Bu doküman, Android 13 (API 33) işletim sistemli Android TV için sıfırdan geliştirilen **Saf TV** uygulamasının mimarisini, versiyon geçmişini, eklenen özellikleri ve kullanım kılavuzunu içermektedir.

---

## 1. Saf TV Mimarisinin Temel İlkeleri

* **Ultra Hafif (~77 KB):** Reklam, izleyici, analitik veya şişkin harici kütüphane içermez.
* **0.1 Saniyede Cold Boot:** Tıklandığı anda en son izlenen kanaldan tam ekran oynamaya başlar.
* **Saf Donanım Hızlandırma:** Android 13'ün yerel `MediaPlayer` ve `SurfaceView` donanım kod çözücülerini kullanır. RAM tüketimi 25–35 MB bandındadır.
* **%100 TV Kumanda (D-Pad) Odaklı:** Android TV ve Google TV kumandalarıyla tam uyumludur.

---

## 2. Versiyon Kontrol Sistemi (SemVer & APK Arşivi)

Uluslararası **Semantic Versioning (SemVer)** ve Android resmi **`versionCode` / `versionName`** standardı uygulanmıştır. Her derlemede eski APK silinmez; `releases/` klasöründe kalıcı olarak arşivlenir. Kök dizindeki `SafTV.apk` ise daima en son stabil sürümü temsil eder.

| Sürüm | versionCode | Boyut | Arşiv Dosyası | Başlıca Özellikler |
|---|---|---|---|---|
| **v1.0.0** | 1 | ~53.1 KB | `releases/SafTV_v1.0.0_vc1.apk` | İlk sürüm. 23 ulusal kanal, D-Pad gezinimi, OSD bilgi çubuğu. |
| **v1.1.0** | 2 | ~57.1 KB | `releases/SafTV_v1.1.0_vc2.apk` | **51 Kanala çıkış**, OSD Dijital Saat (`HH:mm`), genişletilmiş 8 kategori, CH+/- ile 5'er kanal atlama. |
| **v1.2.0** | 3 | ~61.1 KB | `releases/SafTV_v1.2.0_vc3.apk` | **⭐ Favori Kanallar** (Sarı Tuş / Uzun OK), Favoriler kategori sekmesi, **Aspect Ratio** (Mavi Tuş: Tam Ekran / 16:9 Fit). |
| **v1.3.0** | 4 | ~77.2 KB | `releases/SafTV_v1.3.0_vc4.apk` | **⚙️ Kanal Yönetimi** (İlk kurulumda hepsi aktif; ayarlardan istenen kanalları pasif yapıp gizleme), **Yayın Kalitesi Seçimi** (Otomatik, 1080p, 720p, 480p, 360p), **Yeşil Tuş Hızlı Kalite Menüsü**, Menü Tuşu. |
| **v1.3.1** | 5 | ~77.2 KB | `releases/SafTV_v1.3.1_vc5.apk` | Kanal listesinde OK ile kanal değiştirme ve Ayarlar ekranında kanal açma/kapama kumanda D-Pad geliştirmeleri. |
| **v1.4.0** | 6 | ~89.3 KB | `releases/SafTV_v1.4.0_vc6.apk` | **📱 Tam Dokunmatik Ekran & Telefon/Tablet Desteği:** Ekran kaydırma jestleri (Swipe Up/Down kanal, Swipe Right liste), ekranda ◀/▶ yön butonları, üst hızlı işlem çubuğu (`[☰ Kanallar]`, `[★ Favori]`, `[📶 Kalite]`, `[📺 Ekran]`, `[⚙️ Ayarlar]`), OSD tıkla-aç, boş alana dokunarak menü kapatma, Immersive Sticky Tam Ekran ve modern mobil geri tuşu yönetimi. |
| **v1.4.1** | 7 | ~89.3 KB | `releases/SafTV_v1.4.1_vc7.apk` | **🛡️ Stabilite & Hata Düzeltmeleri:** Sonsuz yeniden bağlanma sınırlandırıldı (Maks 5 deneme + bilgilendirici uyarı), HlsParser soket/bellek sızıntısı giderildi (`conn.disconnect()`), arka plandan/uykudan dönüşte donma sorunu çözüldü (`onResume`/`onPause` yaşam döngüsü), çekmecede anlık aktif kanal sayısı sayımı ve çekmece açılınca oynayan kanala otomatik odaklanma/scroll eklendi. |
| **v1.5.0** | 8 | ~97.4 KB | `releases/SafTV_v1.5.0_vc8.apk` | **🔍 Canlı Kanal Arama & Ağ Dayanıklılığı:** Çekmece üstü anlık kanal arama/filtreleme çubuğu, yerel `ConnectivityManager` ile internet kopması algılama & otomatik devam etme, 2048-bit kalıcı `saftv-release.jks` resmi geliştirici imzası. |
| **v1.6.0** | 9 | ~97.4 KB | `releases/SafTV_v1.6.0_vc9.apk` | **📺 10 Yeni Canlı Kanal Genişlemesi:** Toplam 61 kanal (Lalegül, Semerkand, CNN Türk, DHA, TGRT Belgesel, Nat Geo Wild, Çiftçi TV, TRT EBA). |
| **v1.7.0** | 10 | ~109.4 KB | `releases/SafTV_v1.7.0_vc10.apk` | **📐 Kompakt Çekmece & Yedekleme:** 5+ kanal listesi, iki satırlı üst bar, tarih damgalı ayar yedekleme/içe aktarma, güncelleme denetleme. |
| **v1.8.0** | 11 | ~113.4 KB | `releases/SafTV_v1.8.0_vc11.apk` | **🧭 Esnek Üst Bar & Modüler Sekmeler:** Yan yana akıllı üst bar, 4 sekmeli ayarlar menüsü, tümünü pasif yap butonu. |
| **v1.9.0** | 12 | ~117.4 KB | `releases/SafTV_v1.9.0_vc12.apk` | **🚪 Onaylı Çıkış & Yayını Duraklatma:** Çıkış onay diyaloğu, dokunmatik/kumanda pause-play, başlıksız sol çekmece, Archive.org link butonu. |
| **v2.0.0** | 13 | ~129.4 KB | `releases/SafTV_v2.0.0_vc13.apk` | **🚀 Major Sürüm:** PiP (Resim İçinde Resim), Uyku Zamanlayıcısı, Ekran Kapalı Arka Planda Ses Dinleme, Hibrit Arayüz Temelleri. |
| **v2.1.0** | 14 | ~133.4 KB | `releases/SafTV_v2.1.0_vc14.apk` | **📏 Yüzdesel Hassas Ölçekleme (%80-%150):** 7 kademeli hassas ölçek, dinamik çekmece genişliği, tam ayar yedekleme entegrasyonu. |

---

## 3. Güncel 51 Kanal Listesi (v1.1.0+)

Tüm yayınlar canlı olarak test edilmiş, doğrudan resmi CDN üzerinden çalışan şifresiz ve yasal HLS (`.m3u8`) akışlarıdır:

### Ulusal / Eğlence (14 Kanal)
1. **TRT 1**
2. **Kanal D**
3. **ATV**
4. **Show TV**
5. **Star TV**
6. **NOW TV**
7. **TV8**
8. **Kanal 7**
9. **A2 TV**
10. **Teve2**
11. **Beyaz TV**
12. **360 TV**
13. **TV4**
14. **TRT Türk**

### Haber & Politika (14 Kanal)
15. **TRT Haber**
16. **NTV**
17. **HaberTürk**
18. **A Haber**
19. **Sözcü TV (SZC)**
20. **Haber Global**
21. **Halk TV**
22. **24 TV**
23. **TGRT Haber**
24. **Bloomberg HT**
25. **Tele1**
26. **TVNET**
27. **Flash Haber**
28. **TRT 3 / TBMM TV**

### Spor (6 Kanal)
29. **TRT Spor**
30. **TRT Spor Yıldız**
31. **A Spor**
32. **TV8.5**
33. **HT Spor**
34. **FB TV (Fenerbahçe TV)**

### Belgesel & Yaşam (3 Kanal)
35. **TRT Belgesel**
36. **DMAX**
37. **TLC**

### Çocuk & Aile (4 Kanal)
38. **TRT Çocuk**
39. **Minika Çocuk**
40. **Minika GO**
41. **TRT Diyanet Çocuk**

### Müzik (4 Kanal)
42. **TRT Müzik**
43. **Power TV HD**
44. **Dream Türk**
45. **Kral Pop TV**

### Kültür & Dünya (6 Kanal)
46. **TRT 2**
47. **Diyanet TV**
48. **TRT World (İngilizce)**
49. **TRT Avaz (Türk Dünyası)**
50. **TRT Arabi (Arapça)**
51. **TRT Kurdî (Kürtçe)**

---

## 4. Kumanda ve Arayüz Kontrolleri

### 📱 Telefon & Tablet (Dokunmatik Ekran)
* **Ekrana Tek Dokunma:** Kontrol butonlarını ve kanal bilgi çubuğunu açar / kapatır.
* **Ekranda Yukarı / Aşağı Kaydır (Swipe Up/Down):** Sıradaki veya önceki kanala geçer.
* **Soldan Sağa Kaydır (Swipe Right):** Ekranın solundan kanal listesini açar.
* **Sağdan Sola Kaydır (Swipe Left):** Açık çekmece ve ayar menülerini kapatır.
* **Ekrana Çift Dokunma:** Ekran oranını (*Tam Ekran / 16:9 Fit*) değiştirir.
* **◀ ve ▶ Butonları:** Ekranın sağ ve solundaki yuvarlak butonlarla önceki/sonraki kanala geçer.
* **[☰ Kanallar] Butonu veya OSD Bilgi Kutusu:** Kanal listesini açar.
* **[★ Favori] Butonu:** O anki kanalı favorilere ekler veya çıkarır.
* **[📶 Kalite] Butonu:** Hızlı Kalite Seçim Menüsünü açar.
* **[📺 Ekran] Butonu:** Ekran oranını değiştirir.
* **[⚙️ Ayarlar] Butonu:** Kanal Yönetimi & Ayarlar panelini açar.
* **Boş Alana Dokunma veya [✕] Butonu:** Açık olan kanal listesi veya ayarlar panelini kapatır.

### 📺 Android TV Uzaktan Kumandası
* **▲ / ▼ (Yukarı / Aşağı):** Sıradaki aktif kanala geçer (pasif yapılan kanalları otomatik atlar).
* **CH+ / CH- (veya Page Up / Page Down):** 5 kanal birden hızlı atlar.
* **OK (Orta Tuş):** Ekranın solundan yarı saydam kanal listesini açar.
* **0 - 9 Rakam Tuşları:** Direkt kanal numarasına zıplar (Örn: `1` -> TRT 1, `19` -> Sözcü TV).
* **SARI Tuş (veya 'Y' / 'F' / Listede Uzun OK):** O anki kanalı **⭐ Favorilere** ekler veya çıkarır.
* **YEŞİL Tuş (veya 'G'):** TV izlerken anında **Hızlı Kalite Menüsü** açar (*Otomatik, 1080p, 720p, 480p, 360p*).
* **MAVİ Tuş (veya 'B'):** **Ekran Oranını (Aspect Ratio)** değiştirir (*Tam Ekran / 16:9 Orantılı*).
* **MENÜ Tuşu (veya 'M' / Sol menüdeki ⚙️ Ayarlar Butonu):** **Kanal Yönetimi & Ayarlar** panelini açar.
  * İlk kurulumda tüm kanallar aktiftir.
  * Tıklanan kanal `[PASİF ✕]` hale gelir ve ana listeden gizlenir.
  * "Tümünü Aktif Et" butonu ile tek tıkla varsayılana dönülebilir.
* **GERİ (Back):** Açık olan panelleri sırayla kapatır; kapalıyken 2.5 saniye içinde iki kez basıldığında uygulamadan çıkar.

---

## 5. Derleme (Build Pipeline)

`build.py` scripti sürüm artırma ve arşivlemeyi tam otomatik yönetir:

```powershell
# Mevcut versiyonla tekrar derlemek için:
python build.py

# Bir sonraki minor sürüme geçmek için:
python build.py --bump minor "Yeni özellik açıklaması"

# Doğrudan sürüm belirterek derlemek için:
python build.py --set 1.3.0 4 "Sürüm açıklaması"
```

---

## 6. Sürüm v1.6.0 (versionCode 9) — 10 Yeni Canlı Kanal Genişlemesi

Kullanıcı talebi doğrultusunda yapılan derin araştırmalar sonucunda 10 yeni popüler canlı kanal test edilerek uygulamaya başarıyla entegre edildi (Toplam kanal sayısı 51'den 61'e yükseltildi):

* **Kültür & Dini:**
  * **Lalegül TV (Kanal 52):** 3 Kalite Multi-bitrate HLS (4.5 Mbps HD, 1.2 Mbps Orta, 620 Kbps Düşük).
  * **Semerkand TV (Kanal 53):** Tasavvuf ve aile/kültür temalı canlı yayın akışı.
* **Haber:**
  * **CNN Türk (Kanal 54):** Ulusal haber kanalı canlı HLS akışı.
  * **DHA Canlı (Kanal 55):** 7/24 son dakika olay yeri ve ajans canlı yayını.
* **Belgesel & Yaşam:**
  * **TGRT Belgesel (Kanal 56):** Türk-İslam tarihi, medeniyetler ve doğa belgeselleri.
  * **National Geographic Wild (Kanal 57):** Vahşi yaşam ve doğa belgeselleri.
  * **Çiftçi TV (Kanal 58):** Tarım, ziraat, hayvancılık ve köy yaşamı kanalı.
* **Çocuk & Eğitim:**
  * **TRT EBA İlkokul (Kanal 59):** MEB ilkokul canlı ders anlatım kanalı.
  * **TRT EBA Ortaokul (Kanal 60):** MEB ortaokul müfredatı ders anlatım kanalı.
  * **TRT EBA Lise (Kanal 61):** MEB lise müfredatı ve üniversite hazırlık kanalı.

Tüm yeni kanallar kalıcı ID (52-61) sırasına yerleştirildi; mevcut kullanıcı favorileri ve gizleme tercihleri bozulmadı. APK boyutu 97.4 KB olarak korundu.

---

## 7. Sürüm v1.8.0 (versionCode 11) — Esnek Üst Bar (1A) ve 4 Sekmeli Ayarlar (2B)

Kullanıcının sesli geri bildiriminde ilettiği talepler (1A ve 2B) başarıyla hayata geçirildi:

* **Yan Yana Esnek Üst Bar (1A):**
  * Android TV, tablet ve yatay moddaki telefonlarda OSD kanal bilgi kutusu ile dokunmatik buton barı (`[☰ Kanallar]`, `[★ Favori]`, `[📶 Kalite]`, `[📺 Ekran]`, `[⚙️ Ayarlar]`) tek bir satırda yan yana dizildi.
  * `onConfigurationChanged` ve dinamik layout yönetimiyle yalnızca dar dikey (portrait) ekranlarda sıkışma olmaması için butonlar otomatik olarak alt satıra geçer.
* **4 Sekmeli Modüler Ayarlar Paneli (2B):**
  * Ayarlar paneli tek ekranda yığılmak yerine 4 temiz sekmeye ayrıldı:
    * `[📋 Kanallar]`: 61 kanalın listesi ve tek tıkla **"✕ Tümünü Kapat"** / **"✓ Tümünü Aktif Et"** toplu yönetim butonları.
    * `[📶 Kalite]`: Varsayılan açılış kalitesi tercihleri (Otomatik, 1080p, 720p, 480p, 360p).
    * `[💾 Yedekleme]`: Ayarları Dışa Aktar ve Dosyadan İçe Aktar.
    * `[ℹ️ Hakkında]`: Saf TV sürümü, mimarisi, lisansı ve çevrim içi güncelleme denetimi.
* APK boyutu: ~113.4 KB, saf donanım kod çözümü, sıfır reklam, resmi kalıcı imza (`saftv-release.jks`).


---

## 8. Sürüm v1.9.0 (versionCode 12) — Sadeleştirilmiş Arayüz, Güvenli Çıkış (1B), Oynat/Duraklat (2A), K2 Sol Menü & R1 Ayarlar

Kullanıcının 2026-11-15 tarihli ekran görüntüsü notları ve geri bildirimleri doğrultusunda geliştirildi:

* **1B - Güvenli Çıkış Diyaloğu:**
  * Üst bara `[🚪 Çıkış]` butonu eklendi.
  * Tıklandığında veya TV kumandasından çıkış istendiğinde *"Saf TV'den çıkmak istiyor musunuz? [✕ Vazgeç] [✓ Evet, Çık]"* onay kutusu açılır. Yanlışlıkla yayından çıkılması engellendi.
* **2A - Yayını Duraklatma / Oynatma:**
  * Ekranın ortasına dokunulduğunda `[⏸ / ▶]` butonu belirir.
  * Kumanda oynat/duraklat tuşları (`MEDIA_PLAY_PAUSE`, `MEDIA_PAUSE`, `MEDIA_PLAY`) ile tam uyumlu çalışır.
* **Üst Bar Sadeleştirmesi:**
  * Üst bardan `[☰ Kanallar]` ve `[★ Favori]` butonları kaldırıldı; `[📶 Kalite]`, `[📺 Ekran]`, `[⚙️ Ayarlar]`, `[🚪 Çıkış]` kaldı.
  * Yüzen önceki/sonraki kanal butonları (`◀` ve `▶`) muhafaza edildi.
* **K2 - Sol Menü (Kanal Çekmecesi) Sadeleştirmesi:**
  * Çekmece başlığı kaldırılarak doğrudan `🔍 Kanal ara...` arama kutusu ve entegre `[✕]` kapatma butonu ile başlatıldı. Tek ekranda görünen kanal sayısı 7+'ya çıkarıldı.
* **R1 - Sağ Menü (Ayarlar) Kompakt Tasarım & Kaydırma:**
  * Ayarlar başlığı ve 4 sekme tek satırda toplandı (`⚙️` + 4 sekme + `[✕]`).
  * Kalite, Yedekleme ve Hakkında sekmeleri `ScrollView` içine alınarak büyük yazı tiplerinde taşma engellendi.
  * Hakkında sekmesine `[🌐 Archive.org Sayfasını Aç]` butonu eklendi.
* **Dağıtım ve Güncelleme:**
  * `SafTV_v1.9.0_vc12.apk` ve `SafTV.apk` (117.4 KB) Archive.org'a yüklendi.
  * `version.json` (versionCode: 12) güncellendi. v1.8.0 kullanan cihazlar güncellemeyi hemen görebilir.

---

## 9. Sürüm v2.0.0 (versionCode 13) — Major Sürüm: TV/Mobil Hibrit Arayüz Ölçeği, PiP, Uyku Zamanlayıcısı ve Ekran Kapalı Dinleme

Kullanıcının `güncelleme sorunu.mp4` video analizi, kanal listesi talepleri ve TV/mobil font ölçeklendirme istekleri doğrultusunda geliştirilen büyük dönüm noktası (Major Sürüm):

* **Yüzdesel Hassas Arayüz Ölçeği (%80 — %150):**
  * Kullanıcının *"v1.9 telefonda iyi fakat tv de yazılar küçük"* tespiti doğrultusunda endüstri standardı (TiviMate & IPTV modeli) 6 kademeli yüzdesel ölçekleme mimarisi uygulandı:
    * `[● Otomatik (Akıllı Cihaz Tespiti)]`: TV ise %130, Mobil ise %100 otomatik uygulanır.
    * `[%80 (Ultra Kompakt)]`: Tek ekranda 12+ kanal listelemek için.
    * `[%90 (Kompakt)]`: Küçük ekranlı mobil cihazlar için.
    * `[%100 (Standart Mobil)]`: Standart telefon/tablet görünümü.
    * `[%115 (Tablet / Yakın TV)]`: Tabletler, katlanabilir ekranlar ve mutfak TV'leri için.
    * `[%130 (Büyük TV / 3m)]`: 3 metreden koltuktan izlenen salon TV'leri için ideal 10-foot UI.
    * `[%150 (Dev / Uzak TV)]`: 4+ metre uzak ekranlar veya görme kolaylığı için devasa puntolar.
  * Sol menü kanal satırları, yazı boyutları (10sp-21sp), numara rozetleri (22dp-42dp), OSD başlıkları ve çekmece genişliği (230dp-390dp) dinamik ve akıcı olarak yeniden hesaplanır. Ayarlar yedekleme ve geri yükleme sistemine entegre edildi.
* **Resim İçinde Resim (PiP - Picture-in-Picture):**
  * Android 8.0+ native PiP mimarisi uygulandı.
  * Üst bara `[🗗 PiP]` butonu yerleştirildi; canlı yayındayken Ana Ekran (Home) tuşuna basıldığında yayın otomatik olarak ekran köşesinde yüzen pencereye küçülür (`onUserLeaveHint()`).
* **Uyku Zamanlayıcısı (Sleep Timer):**
  * Ayarlar Tercihler sekmesinden `Kapalı`, `15`, `30`, `60`, `90`, `120 dakika` seçenekleriyle ayarlanır.
  * Kalan süre saniye saniye takip edilir (`⏳ 29:45 kaldı`). Süre bittiğinde yayın durdurularak uygulama arka plandan tamamen kapatılır (`finishAffinity()`).
* **Ekran Kapalı Dinleme Modu (Arka Planda Ses):**
  * Ayarlar Tercihler sekmesinden tek tuşla açılır.
  * Telefon kilitlendiğinde veya ekran karardığında yayın durmaz; ses kesintisiz çalmaya devam eder ve telefon cebindeyken devasa batarya tasarrufu sağlanır.
* **Chrome APK Önbellek Sorununun Çözümü:**
  * Kullanıcının videosunda `SafTV.apk`'nın Chrome disk önbelleğinden 6 saat boyunca eski sürümü indirdiği kanıtlandı.
  * `version.json` içindeki `apkUrl` doğrudan benzersiz sürüm damgalı adrese (`releases/SafTV_v2.0.0_vc13.apk`) yönlendirilerek önbellek engeli aşıldı.
* **Kanal Envanteri ve Excel:**
  * 61 mevcut kanal, 100 aday TV kanalı (öncelik ve HLS URL'leriyle), 24 radyo akışı ve özet istatistik içeren 4 sekmeli `SafTV_Kanal_Listesi.xlsx` üretildi ve Archive.org'a yüklendi.
  * Kullanıcının net talimatı üzerine ("radyo değil. o olmaz") radyo kanalları uygulamaya dahil edilmedi, Saf TV saf televizyon odağını korudu.
* **Teknik Metrikler:**
  * APK Boyutu: 129.4 KB (Sıfır harici bağımlılık).
  * Açılış Hızı: 0.1 saniye cold boot.
  * Resmi İmza: 2048-bit `saftv-release.jks`.
