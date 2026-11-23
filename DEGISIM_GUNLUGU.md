# 📜 Saf TV — Değişim Günlüğü (Sürüm Tarihçesi)

Bu doküman, **Saf TV** uygulamasının ilk sürümünden bugüne kadar yayınlanan tüm sürümlerini, eklenen özellikleri, hata düzeltmelerini ve doğrudan indirilebilir APK arşiv bağlantılarını kronolojik olarak listeler.

---

## 🧭 Tüm Sürümler ve İndirme Bağlantıları Tablosu

Aşağıdaki tablodan dilediğiniz sürümün APK dosyasını tek tıkla doğrudan indirebilirsiniz:

| Sürüm | Sürüm Kodu (versionCode) | Yayın Tarihi | APK Boyutu | Doğrudan APK İndir | Başlıca Yenilikler ve Odak Noktası |
|:---:|:---:|:---:|:---:|:---:|---|
| **v2.16.0** | 29 | 23.11.2026 | ~229.5 KB | [⬇️ **SafTV_v2.16.0_vc29.apk**](https://github.com/devezir/SafTV/releases/download/v2.16.0/SafTV_v2.16.0_vc29.apk) | 📐 **Evrensel Arayüz Ölçeği (Universal UI Scale):** Tüm menü, çekmece ve diyalogların tek merkezden kusursuz simetrisi. Sağ EPG Çekmecesi dinamik genişlik (`330dp * scale`) ve program kartı ölçeklemesi (`EpgProgramAdapter`), OSD Mini EPG metin boyutları, Hızlı Kalite paneli ve 9 sekmeli Ayarlar paneli tam entegre edildi. |
| **v2.15.0** | 28 | 23.11.2026 | ~225.5 KB | [⬇️ **SafTV_v2.15.0_vc28.apk**](https://github.com/devezir/SafTV/releases/download/v2.15.0/SafTV_v2.15.0_vc28.apk) | 📅 **Full Pro EPG (Elektronik Program Rehberi) Motoru:** Sıfır harici kütüphane ile yerel `XmlPullParser` ve `GZIPInputStream` akış ayrıştırıcı. OSD Mini EPG (Şu An + Sırada + Canlı İlerleme Çubuğu ve Kalan Dakika), Sol Çekmecede 2 satırlı anlık program gösterimi, Sağ Çekmecede 24 saatlik tam gün akış rehberi (Canlı rozeti ve açıklamalar), Ayarlar Sekme 9 (EPG Aç/Kapat, Manuel Yenileme, Kotalı Ağ Koruması). |
| **v2.14.0** | 27 | 23.11.2026 | ~202.1 KB | [⬇️ **SafTV_v2.14.0_vc27.apk**](https://github.com/devezir/SafTV/releases/download/v2.14.0/SafTV_v2.14.0_vc27.apk) | 🏗️ **Tam Kapsamlı DRY Mimari Refaktörü & Kod Tabanı Optimizasyonu:** `BaseChannelAdapter` ile kanal ve ayarlar adaptörleri tekilleştirildi; `SelectorGroupHelper` fluent builder ile 5 farklı ayar butonu grubu tek çatı altında toplandı. `AppPreferences` ve `AppColors` ile dağınık tercihler ve renk kodları standartlaştırıldı. `IoUtils` ile dosya/ağ I/O akışları konsolide edildi; `DataUsageManager` kota fonksiyonları birleştirildi ve XML stilleri yeniden yapılandırıldı (%40 daha sade ve hızlı kod tabanı). |
| **v2.13.0** | 26 | 21.11.2026 | ~193.4 KB | [⬇️ **SafTV_v2.13.0_vc26.apk**](https://github.com/devezir/SafTV/releases/download/v2.13.0/SafTV_v2.13.0_vc26.apk) | 📶 **Kalite Menüsünde Çift Katmanlı Canlı Durum Rozeti (Çözünürlük + Anlık Hız) ve Dinamik Buton Vurgusu (Seçenek C):** Hem Yeşil Tuş Hızlı Kalite Paneline hem Ayarlar Kalite sekmesine anlık oynatılan çözünürlük (1080p, 720p vb.) ve tüketim hızını (Mbps) gösteren şık canlı rozet eklendi. Aktif kalite butonuna yeşil `●` noktası, vurgulu renk ve kumandada otomatik odaklanma entegre edildi. |
| **v2.12.0** | 25 | 21.11.2026 | ~189.4 KB | [⬇️ **SafTV_v2.12.0_vc25.apk**](https://github.com/devezir/SafTV/releases/download/v2.12.0/SafTV_v2.12.0_vc25.apk) | ⏱️ **OSD Süresi Ayarı (2, 3, 5, 10 sn, Süresiz) & Kumandada Çift Kademeli Akıllı OK Tuşu:** Ayarlar ➔ Görünüm sekmesine OSD gösterim süresi tercihi eklendi. TV kumandasında OK tuşu çift kademeli hale getirildi: 1. basışta OSD bilgi çubuğu, 2. basışta kanal listesi açılır; BACK tuşu OSD açıkken OSD'yi anında kapatır, kapalıyken çıkış diyaloğunu açar. |
| **v2.11.0** | 24 | 21.11.2026 | ~185.4 KB | [⬇️ **SafTV_v2.11.0_vc24.apk**](https://github.com/devezir/SafTV/releases/download/v2.11.0/SafTV_v2.11.0_vc24.apk) | 📻 **Sadece Ses (Radyo Modu) Kalıcılığı & Bilgilendirme Ekranı:** Sadece Ses modu geçici ekran koruyucudan kalıcı bir kalite moduna dönüştürüldü. Ekrana dokunma/tıklama, OK veya BACK tuşlarıyla kazara videoya dönme engellendi; videoyu açmak için Kalite Menüsü şart koşuldu ve şık bilgilendirme rozeti eklendi. |
| **v2.10.0** | 23 | 20.11.2026 | ~185.4 KB | [⬇️ **SafTV_v2.10.0_vc23.apk**](https://github.com/devezir/SafTV/releases/download/v2.10.0/SafTV_v2.10.0_vc23.apk) | 🛡️ **Çıkış (BACK) Diyaloğuna "⚙️ Ayarlar" Erişimi (Emniyet Kilidi):** Settings veya Menu tuşu bulunmayan minimalist Android TV kumandalarında (Mi Box, Chromecast vb.) kumandadan sadece GERİ (BACK) tuşuna basarak anında Ayarlar paneline geçebilme garantisi. |
| **v2.9.0** | 22 | 18.11.2026 | ~185.4 KB | [⬇️ **SafTV_v2.9.0_vc22.apk**](https://github.com/devezir/SafTV/releases/download/v2.9.0/SafTV_v2.9.0_vc22.apk) | 📁 **Gerçek Download Klasörüne Dışa Aktarma (MediaStore API), Scoped Storage İçe Aktarma & 10 Bağımsız Kategori Listesi:** Android 11+ kısıtlı `/Android/data/` klasörü yerine doğrudan kullanıcı tarafından erişilebilir `/storage/emulated/0/Download/` klasörüne yazma, sıfır depolama izni, mükerrer önleme ve 120 kanalın 10 ayrı M3U8 ve JSON listeleri. |
| **v2.8.0** | 21 | 17.11.2026 | ~185.4 KB | [⬇️ **SafTV_v2.8.0_vc21.apk**](https://github.com/devezir/SafTV/releases/download/v2.8.0/SafTV_v2.8.0_vc21.apk) | 📲 **Uygulama İçi Doğrudan OTA Güncelleme & Canlı İndirme Çubuğu:** Harici web tarayıcısına gerek kalmadan TV kumandasıyla tek tıkla doğrudan indirme, canlı yüzde ve KB sayacı, sıfır harici kütüphaneli yerel `ApkProvider` ile Android Paket Yükleyicisini otomatik başlatma, kesintisiz TV deneyimi ve ayar/kanal koruma güvencesi. |
| **v2.7.0** | 20 | 17.11.2026 | ~173.4 KB | [⬇️ **SafTV_v2.7.0_vc20.apk**](https://github.com/devezir/SafTV/releases/download/v2.7.0/SafTV_v2.7.0_vc20.apk) | 📻 **Sadece Ses (Radyo Modu) & %95+ Veri Tasarrufu:** Video rendering'i tamamen kapatıp sadece sesi dinleme modu (Haber/tartışma programları için ideal), HLS bağımsız ses veya minimum bant genişlikli akış seçimi, OLED dostu siyah radyo bilgi paneli, TV yanma koruması (Burn-in Protection), tek tuşla (OK/Dokun/BACK) anında videoya dönüş ve Hızlı Kalite (Yeşil Tuş) entegrasyonu. |
| **v2.6.0** | 19 | 17.11.2026 | ~169.4 KB | [⬇️ **SafTV_v2.6.0_vc19.apk**](https://github.com/devezir/SafTV/releases/download/v2.6.0/SafTV_v2.6.0_vc19.apk) | 📶 **Kotalı Ağ Farkındalığı, Canlı OSD Veri Sayacı, 8. Sekme & 3 Bağımsız Kota:** Otomatik kotalı Wi-Fi (hotspot) ve mobil veri tespiti, Kotalı ağ davranışı (Serbest / Uyar / Engelle), OSD bilgi çubuğunda anlık hız & oturum sayacı rozeti, Ayarlar menüsünde 8. sekme (📶 Ağ & Veri: Anlık hız, Oturum, Bugün, Rolling Son 30 Gün), Oturum / Günlük / Son 30 Gün için bağımsız kota limitleri ve elle GB/MB girişi. |
| **v2.5.0** | 18 | 16.11.2026 | ~149.4 KB | [⬇️ **SafTV_v2.5.0_vc18.apk**](https://github.com/devezir/SafTV/releases/download/v2.5.0/SafTV_v2.5.0_vc18.apk) | 🎛️ **Kanal Sıralama (A+B Hibrit), Otomatik Sıralama Şablonları & Alt Bilgi Ölçeklemesi:** Ayarlar menüsünden D-Pad ile canlı kanal taşıma, hedef sıra numarasıyla anında ışınlama, A-Z / Kategori / Varsayılan sıralama şablonları, Liste Sırası = Kanal No eşlemesi ve çekmece alt ipucu yazısının arayüz ölçeğiyle birebir büyümesi. |
| **v2.4.0** | 17 | 16.11.2026 | ~137.4 KB | [⬇️ **SafTV_v2.4.0_vc17.apk**](https://github.com/devezir/SafTV/releases/download/v2.4.0/SafTV_v2.4.0_vc17.apk) | 🛡️ **%100 Yasal Çekirdek & Çift APK Mimarisi:** Açık kaynak koddan tüm yayınlar çıkarıldı (tamamen yasal IPTV oynatıcı). M3U/M3U8 ve JSON dosya/URL yükleme desteği, Ayarlar menüsünden tek tıkla liste sıfırlama, tohumlu özel APK desteği. |
| **v2.3.0** | 16 | 16.11.2026 | ~137.4 KB | [⬇️ **SafTV_v2.3.0_vc16.apk**](https://github.com/devezir/SafTV/releases/download/v2.3.0/SafTV_v2.3.0_vc16.apk) | 📐 **Genişletilmiş Arayüz Ölçeği (%60 — %200):** Ultra mikro %60'tan dev ekran %200'e 8 kademeli skala, dinamik yüzde koruması, internet yeniden bağlantısında uyarıların anında silinmesi güvencesi. |
| **v2.2.0** | 15 | 15.11.2026 | ~133.4 KB | [⬇️ **SafTV_v2.2.0_vc15.apk**](https://github.com/devezir/SafTV/releases/download/v2.2.0/SafTV_v2.2.0_vc15.apk) | 🎛️ **7 Bağımsız Sekmeli Yatay Kayan Ayarlar Menüsü:** Kalabalık tercihler menüsü 7 bağımsız sekmeye ayrıldı (📋 Kanallar, 📶 Kalite, 📏 Görünüm, 🌙 Uyku, 🎧 Ses Modu, 💾 Yedekleme, ℹ️ Hakkında). Kenar gölge geçişi (Fading Edge), TV kumandası D-Pad otomatik ortalama ve dokunmatik kaydırma. |
| **v2.1.0** | 14 | 15.11.2026 | ~133.4 KB | [⬇️ **SafTV_v2.1.0_vc14.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v2.1.0_vc14.apk) | 📏 **Yüzdesel Hassas Arayüz Ölçeği (%80 - %150):** 7 kademeli akıllı ölçekleme, dinamik çekmece genişliği, GitHub tabanlı 0 saniye OTA güncelleme sistemi. |
| **v2.0.0** | 13 | 15.11.2026 | ~129.4 KB | [⬇️ **SafTV_v2.0.0_vc13.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v2.0.0_vc13.apk) | 🚀 **Büyük Kilometre Taşı:** 📱 Resim İçinde Resim (PiP - Yüzen Pencere), 🌙 Akıllı Uyku Zamanlayıcısı, 🎧 Ekran Kapalı Dinleme Modu (Arka Planda Ses). |
| **v1.9.0** | 12 | 15.11.2026 | ~117.4 KB | [⬇️ **SafTV_v1.9.0_vc12.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.9.0_vc12.apk) | 🚪 **Güvenli Çıkış:** Kazara kapanmayı önleyen çıkış onay penceresi, ⏸️ Canlı yayını duraklatma/oynatma, 📐 Başlıksız 7+ kanallı sol çekmece menüsü. |
| **v1.8.0** | 11 | 15.11.2026 | ~113.4 KB | [⬇️ **SafTV_v1.8.0_vc11.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.8.0_vc11.apk) | 🧭 **Esnek Üst Kontrol Çubuğu:** Yan yana akıllı buton dizilimi, 4 sekmeli modüler ayarlar paneli (Kanallar, Tercihler, Yedekleme, Hakkında) ve 'Tümünü Kapat' butonu. |
| **v1.7.0** | 10 | 15.11.2026 | ~109.4 KB | [⬇️ **SafTV_v1.7.0_vc10.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.7.0_vc10.apk) | 📐 **Kompakt Çekmece:** Kompakt 5+ kanal listesi, iki satırlı taşmayan üst bar, tarih damgalı ayar yedekleme ve geri yükleme sistemi, çevrim içi güncelleme denetleme. |
| **v1.6.0** | 9 | 15.11.2026 | ~97.4 KB | [⬇️ **SafTV_v1.6.0_vc9.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.6.0_vc9.apk) | 📺 **10 Yeni Canlı Kanal Genişlemesi:** Toplam 61 kanal (Lalegül TV, Semerkand TV, CNN Türk, DHA Canlı, TGRT Belgesel, Nat Geo Wild, Çiftçi TV, TRT EBA İlkokul, Ortaokul, Lise). |
| **v1.5.0** | 8 | 14.11.2026 | ~97.4 KB | [⬇️ **SafTV_v1.5.0_vc8.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.5.0_vc8.apk) | 🔍 **Canlı Kanal Arama & Ağ Dayanıklılığı:** Çekmece içi anlık harf arama, yerel internet kopması algılama ve oto-devam etme, kalıcı resmi geliştirici anahtarı. |
| **v1.4.1** | 7 | 14.11.2026 | ~89.3 KB | [⬇️ **SafTV_v1.4.1_vc7.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.4.1_vc7.apk) | 🛡️ **Kararlılık ve Hata Düzeltmeleri:** Maksimum 5 deneme sınırı, soket sızıntısı önleme, uykudan dönüşte donmayı engelleme, anlık kanal sayacı, aktif kanala otomatik odaklanma. |
| **v1.4.0** | 6 | 14.11.2026 | ~89.3 KB | [⬇️ **SafTV_v1.4.0_vc6.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.4.0_vc6.apk) | 📱 **Tam Dokunmatik Ekran Desteği:** Ekran kaydırma jestleri, ekranda yön butonları, OSD tıkla-aç, boş alana dokunarak kapatma ve tam ekran sinematik mod. |
| **v1.3.1** | 5 | 14.11.2026 | ~77.2 KB | [⬇️ **SafTV_v1.3.1_vc5.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.3.1_vc5.apk) | 🐛 **Kumanda D-Pad İyileştirmeleri:** Kanal listesinde OK ile kanal değiştirme ve ayarlar menüsünde kanal açma/kapama odaklanma geliştirmeleri. |
| **v1.3.0** | 4 | 14.11.2026 | ~77.2 KB | [⬇️ **SafTV_v1.3.0_vc4.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.3.0_vc4.apk) | ⚙️ **Kanal Yönetimi & Kalite Seçimi:** İstenmeyen kanalları gizleme, çoklu çözünürlük seçimi (1080p, 720p, 480p, 360p) ve Yeşil Tuş hızlı kalite paneli. |
| **v1.2.0** | 3 | 14.11.2026 | ~61.1 KB | [⬇️ **SafTV_v1.2.0_vc3.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.2.0_vc3.apk) | ⭐ **Favori Kanallar:** Sarı Tuş ile favori ekleme/çıkarma, favoriler kategori sekmesi, Mavi Tuş ile Ekran En-Boy Oranı (*Tam Ekran / 16:9 Doğal*). |
| **v1.1.0** | 2 | 14.11.2026 | ~57.1 KB | [⬇️ **SafTV_v1.1.0_vc2.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.1.0_vc2.apk) | 📺 **51 Kanala Çıkış:** Canlı dijital saat göstergesi (`HH:mm`), genişletilmiş 8 kategori sekmesi, kumandadan 5'er kanal hızlı atlama. |
| **v1.0.0** | 1 | 14.11.2026 | ~53.1 KB | [⬇️ **SafTV_v1.0.0_vc1.apk**](https://github.com/devezir/SafTV/releases/download/v2.1.0/SafTV_v1.0.0_vc1.apk) | 🚀 **İlk Temel Sürüm:** 23 ulusal kanal, saf yerel donanım hızlandırmalı video oynatıcı, D-Pad kumanda gezinimi, bilgi çubuğu. |

---

## 🔍 Detaylı Sürüm Notları

### 📐 v2.16.0 (versionCode: 29) — 23 Kasım 2026
* **Evrensel Arayüz Ölçeği (Universal UI Scale) & Tam Menü Simetrisi:**
  * **Sağ Çekmece (24 Saatlik EPG Yayın Akışı):** Sabit 380dp genişlik yerine sol çekmece gibi dinamik `330dp * scale` (ekranın %58'ini aşmayacak şekilde) formülüne bağlandı. `EpgProgramAdapter` içine `setUiScale` entegre edilerek program saatleri, canlı rozeti (`● CANLI`), başlıklar ve açıklamalar ölçekle kusursuz orantılandı.
  * **OSD Mini EPG Kartı:** Üstteki kanal adı büyürken alttaki program bilgilerinin küçük kalma tutarsızlığı giderildi. Şu an, sırada, kalan dakika ve ilerleme çubuğu tam senkronize büyüyor.
  * **Hızlı Kalite Paneli (Yeşil Tuş):** Çözünürlük ve hız rozetleri ile 6 kalite butonu büyük ekranlarda uzaktan rahatça okunacak ve kumandayla kolayca seçilecek şekilde dinamik ölçeklendi.
  * **Ayarlar Paneli (9 Sekme):** Panel genişliği `500dp * scale` olarak esnetildi, sekme butonları ve kalite rozetleri ölçekle uyumlu hale getirildi.
  * **Dokunmatik Bar & Ekran Butonları:** `[📅 Akış]` butonu, yan ok butonları (`◀ / ▶`) ve ortadaki `[⏸ / ▶]` butonu da ölçekleme zincirine bağlandı.

### 📅 v2.15.0 (versionCode: 28) — 23 Kasım 2026
* **Full Pro EPG (Elektronik Program Rehberi) Motoru:**
  * **Sıfır Harici Kütüphane & Yerel XMLTV Ayrıştırıcı:** `EpgManager` ile sıkıştırılmış `.xml.gz` akışları doğrudan `GZIPInputStream` ve Android `XmlPullParser` üzerinden işlenir. 178 kanal ve ~15.000 programlık veriden yalnızca aktif kanallar ve geçerli zaman penceresi filtrelenerek bellek yükü 1 MB'ın altında tutuldu.
  * **OSD Mini EPG (Canlı Yayın Bilgi Kartı):** Kanal geçişinde veya OK / INFO / SAĞ tuşlarına basıldığında ekran altında açılır. O an yayında olan programın saati, adı, canlı ilerleme çubuğu (`ProgressBar`) ve kalan dakika (`"35 dk kaldı"`) ile sıradaki program bilgisi saniyelik güncellemelerle gösterilir.
  * **Sol Çekmece (2 Satırlı Kanal Listesi):** Kanal adının hemen altında `🔴 Şu anki program adı` gösterilir; kanalı açmadan önce yayında ne olduğunu görme kolaylığı sağlar.
  * **Sağ Çekmece (24 Saatlik Tam Gün Yayın Akışı - EPG Drawer):** TV kumandasında OSD açıkken `SAĞ (DPAD_RIGHT)` veya `INFO`, dokunmatik ekranda `[📅 Akış]` butonu veya sağ kenardan sola kaydırma jesti ile açılır. Kanalın 24 saatlik tüm yayın akışını, detaylı açıklamalarını ve canlı rozeti (`● CANLI`) kartlar halinde listeler; o an yayında olan programa otomatik odaklanır.
  * **Ayarlar Sekme 9 (📅 Yayın Akışı):** EPG Açma/Kapama anahtarı, tek tıkla manuel yenileme (168 KB), son güncelleme durumu ve mobil veri/hotspot kotalı ağ koruması eklendi.
  * **Yedekleme & Geri Yükleme:** EPG tercihleri ve kotalı ağ güvenlik ayarları JSON yedekleme sistemine tam entegre edildi.

### 🏗️ v2.14.0 (versionCode: 27) — 23 Kasım 2026
* **Tam Kapsamlı DRY (Don't Repeat Yourself) Mimari Refaktörü:**
  * **BaseChannelAdapter:** `ChannelAdapter` ve `SettingsChannelAdapter` sınıflarındaki ~85 satırlık ortak UI ölçekleme matematiği (DPI, padding, text size), liste dinleyicileri ve adapter temel metotları tek bir soyut sınıfa çıkarıldı.
  * **SelectorGroupHelper (Fluent Builder):** UI Ölçeği, OSD Süresi, Uyku Zamanlayıcı, Hızlı Kalite ve Ayarlar Kalite menülerindeki yüzlerce satırlık tekrarlayan buton dinleyicileri ve `● ` dot/renk döngüleri tek satırlık fluent API ile yönetilir hale getirildi.
  * **AppPreferences & AppColors:** Uygulama genelindeki dağınık `getSharedPreferences` anahtarları ve `#34D399`, `#8B949E`, `#FBBF24`, `#EF4444` gibi sabit renkler tek merkezde toplandı.
  * **IoUtils:** Dosya ve HTTP bağlantı akışlarındaki tekrarlayan `BufferedReader` / `InputStreamReader` blokları `readStreamToString` ve `readFileToString` ile basitleştirildi.
  * **DataUsageManager Kota Konsolidasyonu:** Oturum, Günlük ve 30 Günlük veri kotası kontrolü, JSON tarih filtreleme döngüleri ve bildirim şablonları ortak fonksiyonel arayüzlerle optimize edildi.
  * **XML Renk & Stil Şablonları:** `res/values/styles.xml` içine `TvSettingsButton.Row` ve `TvSettingsButton.Full` stilleri eklendi, `activity_main.xml` içindeki onlarca tekrarlayan buton stili temizlendi.
* **Sonuç:** Uygulama boyutu ve bellek yönetimi daha optimize hale geldi, bakım kolaylığı ve kararlılık en üst seviyeye taşındı.

### 📶 v2.13.0 (versionCode: 26) — 21 Kasım 2026
* **Çift Katmanlı Canlı Kalite Rozeti (Seçenek C):**
  * Hızlı Kalite Paneli (`Yeşil Tuş`) ve Ayarlar ➔ `Kalite` sekmesine iki satırlı canlı bilgi kartı eklendi:
    * 1. Satır: `📺 Canlı Akış: 1920x1080 (Full HD) • 4.8 Mbps` (veya `📻 Canlı Ses Akışı • 128 Kbps`)
    * 2. Satır: `⚙️ Tercih: Otomatik (Akışa Göre)`
  * Özellikle "Otomatik" moddayken yayının o an kaç pikselde aktığı ve internet hızı canlı olarak saniye saniye izlenebilir.
* **Dinamik Seçim Noktası (`● `) ve Renk Vurgusu:**
  * Seçili olan kalite butonu parlak yeşil renge (`#34D399`), kalın yazı tipine (`BOLD`) ve başına onay noktasına (`● `) kavuşur.
  * Seçili olmayan butonlar sade beyaz/normal görünüme geçer.
* **Kumandada Akıllı Odaklanma (Focus Memory):**
  * Hızlı Kalite Paneli açıldığında kumanda imleci doğrudan o an seçili olan butonun üzerinde başlar.
* **Canlı Çözünürlük ve Hız Takibi:**
  * Yayın çözünürlüğü veya anlık internet hızı değiştiğinde açık olan kalite paneli anında kendini günceller.

### ⏱️ v2.12.0 (versionCode: 25) — 21 Kasım 2026
* **OSD Gösterim Süresi Tercihi (Ayarlar ➔ 📏 Görünüm):**
  * Kanal bilgi çubuğu (OSD) ve dokunmatik kontrollerin otomatik kapanma süresi kullanıcı seçimine bağlandı.
  * Seçenekler: `2 sn (Hızlı)`, `3 sn (Standart - Varsayılan)`, `5 sn (Geniş)`, `10 sn (Uzun)`, `Süresiz (Manuel)`.
  * `Süresiz` seçildiğinde otomatik zamanlayıcı kapanır; OSD ancak kullanıcı ekrana dokunduğunda veya kumandadan `BACK` bastığında kapanır.
  * Tercih JSON yedekleme/geri yükleme sistemine (`pref_osd_timeout`) dahil edildi.
* **Kumandada Çift Kademeli Akıllı `OK` Tuşu (Seçenek 1 Uydu/IPTV Modeli):**
  * Tam ekranda canlı yayın izlenirken `OK` tuşuna basıldığında önce OSD bilgi çubuğu açılır (kanal no, adı, kategori, kalite, saat, veri sayacı).
  * OSD açıkken `OK` tuşuna ikinci kez basıldığında sol kanal listesi çekmecesi (`openDrawer`) açılır.
  * Böylece hem tek tıkla kanal ve yayın bilgilerine hızlıca bakılır, hem de kanal listesi açma refleksi korunur.
* **Kumandada `BACK` Tuşu Emniyet Entegrasyonu:**
  * OSD açıkken `BACK` basıldığında OSD anında kapatılır.
  * OSD kapalıyken `BACK` basıldığında çıkış onay diyaloğu (`showExitDialog`) açılır.
  * Kısır döngü tamamen engellendi; uygulamadan güvenli çıkış güvenceye alındı.
* **Dokunmatik Ekran Tam Uyumu:**
  * Ekranda boş alana tek dokunuş (`single tap`) ile OSD açılır ve ayarlanan süre kadar kalır; OSD açıkken tekrar dokunulduğunda anında kapanır.

### 📻 v2.11.0 (versionCode: 24) — 21 Kasım 2026
* **Sadece Ses (Radyo Modu) Kalıcılığı (Kalıcı Tercih Mantığı):**
  * Sadece Ses seçildiğinde, ekrana dokunulduğunda veya kumandada `OK` / `BACK` tuşlarına basıldığında görüntünün aniden geri gelmesi (ekran koruyucu gibi davranması) sonlandırıldı.
  * Kalite tercihi "Sadece Ses" olduğu sürece kanal değiştirilse veya kontroller açılsa dahi radyo modunda kalmaya devam eder (%95+ veri tasarrufu ve siyah ekran koruması korunur).
* **Kazara Görüntü Açılmasını Önleme & Normal Kontrol Davranışı:**
  * Ekrana dokunulduğunda veya tıklandığında yalnızca OSD kontrolleri açılıp kapanır (`toggleOverlay`).
  * Kumandadan `OK` tuşuna basıldığında kanal çekmecesi (`openDrawer`) normal şekilde açılır, radyo modundan çıkmadan kanal listesi gezilebilir ve kanal değiştirilebilir.
  * Kumandadan `BACK` tuşuna basıldığında çıkış onay diyaloğu açılır.
* **Görüntüyü Geri Açma Yolu & Bilgilendirme Rozeti:**
  * Görüntü yalnızca kullanıcı Kalite Menüsü'nden (Yeşil Tuş veya Ayarlar ➔ Kalite) açıkça bir video çözünürlüğü (Otomatik, 1080p, 720p vb.) seçtiğinde geri açılır.
  * Siyah radyo ekranına şık bir bilgilendirme rozeti eklendi:
    `"💡 Görüntüyü açmak için Kalite Menüsü'nden (Yeşil Tuş veya Ayarlar ➔ Kalite) bir video çözünürlüğü seçiniz."`
  * OLED TV'ler için burn-in (piksel yanma) koruyucu hareket mekanizması korunmaya devam etmektedir.

### 🛡️ v2.10.0 (versionCode: 23) — 20 Kasım 2026
* **Minimalist Kumandalar İçin Çıkış Diyaloğundan Ayarlara Erişim:**
  * Günümüz Android TV kumandalarının çoğunda (Xiaomi Mi Stick/Box, Chromecast with Google TV, TCL, Onn vb.) "MENU" veya "SETTINGS" tuşları bulunmamaktadır.
  * Bu kumandalarla ayarlar menüsüne erişimi %100 garantilemek için `GERİ (BACK)` tuşuna basıldığında açılan onay penceresine **`[⚙️ Ayarlar]`** butonu eklendi.
  * Kullanıcı kumandadan BACK'e bastığında diyalog açılır, D-Pad SOL ile `⚙️ Ayarlar` seçilip `OK` dendiğinde anında 8 sekmeli Ayarlar menüsüne geçilir.

### 📁 v2.9.0 (versionCode: 22) — 18 Kasım 2026
* **Gerçek İndirilenler (Download) Klasörüne Dışa Aktarma:**
  * Önceden uygulamanın özel gizli klasörüne (`/Android/data/...`) yazıldığı için TV dosya yöneticilerinde bulunamayan yedek dosyası sorunu tamamen giderildi.
  * Android'in resmi `MediaStore.Downloads` API'si entegre edildi. Artık `saf_tv_yedek_...json` doğrudan ana `Download` klasörüne yazılır.
  * Kullanıcıdan tehlikeli hiçbir depolama izni istenmez (Android 10+ yerel API).
* **Akıllı & Korumalı İçe Aktarma (`importSettings`):**
  * Hem genel `Download` klasörü, hem `MediaStore` ortamı, hem de dahili depolama eş zamanlı taranır.
  * Aynı isimli yedek dosyalarının listeyi kirletmesi (mükerrer kayıt) engellendi.
  * Kullanıcı ister tarayıcıdan indirsin, ister USB bellekten atsın; tüm `.json`, `.m3u`, `.m3u8` dosyaları anında algılanır.
* **10 Bağımsız Kategori & Master Çalma Listesi Kataloğu:**
  * 120 aktif kanal hem VLC/Tivimate uyumlu `.m3u8`, hem Saf TV uyumlu `.json` formatında `playlists/` klasörüne ayrıştırıldı.
  * Tüm döküm [`KANAL_LISTESI.md`](KANAL_LISTESI.md) dokümanı olarak yayına alındı.

### 📲 v2.8.0 (versionCode: 21) — 17 Kasım 2026
* **Uygulama İçi Doğrudan OTA Güncelleme (In-App Direct Updater):**
  * Yeni sürüm çıktığında kullanıcıyı televizyon keyfinden koparan ve harici tarayıcıya yönlendiren hantal akış tamamen kaldırıldı.
  * Artık "Güncelle" dendiğinde APK doğrudan Saf TV içerisinden, arka planda güvenle indirilir.
* **Canlı İlerleme Çubuğu ve Boyut Sayacı (Progress Dialog):**
  * İndirme sırasında ekranda TV kumandasıyla uyumlu şık bir diyalog açılır: Yüzde göstergesi, ilerleme çubuğu ve anlık indirilen/toplam boyut sayacı (Örn: `110 KB / 185 KB`).
  * İstenirse "İptal" butonuyla indirme anında güvenle iptal edilebilir.
* **Sıfır Kütüphaneli Yerel `ApkProvider` (Zero-Dependency ContentProvider):**
  * Saf TV'nin 0 kütüphane ilkesine sadık kalarak, AndroidX'e ihtiyaç duymadan saf Android `ContentProvider` mimarisiyle `ApkProvider` sınıfı geliştirildi.
  * Android 7.0+ `FileUriExposedException` kuralı ve Android 8.0+ `REQUEST_INSTALL_PACKAGES` izin kontrolü kusursuz şekilde yönetildi.
* **Otomatik Sistem Paket Yükleyici Başlatma:**
  * İndirme tamamlandığı anda Android'in yerel sistem kurulum ekranı (`Bu uygulamanın bir güncellemesini yüklemek istiyor musunuz? [Yükle]`) ekrana gelir.
  * Kumandadan "Yükle"ye basıldığında uygulama anında güncellenir; mevcut kanal listesi, favoriler ve ayarlar **%100 korunur**.
* **Çift Katmanlı Hata ve İzin Güvencesi:**
  * Bilinmeyen kaynaklar izni kapalıysa kullanıcı tek tıkla ayar ekranına yönlendirilir; geri döndüğünde kurulum kaldığı yerden devam eder.
  * Olası ağ kopmalarında "Tekrar Dene" ve "Tarayıcıda Aç" yedek seçenekleri hazır tutulur.

### 📻 v2.7.0 (versionCode: 20) — 17 Kasım 2026
* **Sadece Ses (Radyo Modu) & Devasa Veri Tasarrufu (%95+):**
  * Ekranı izlemeyen, sadece haber veya tartışma programlarını dinlemek isteyen kullanıcılar için görüntüyü kapatıp yalnızca sesi aktarma özelliği eklendi.
  * **Veri Tasarrufu:** 1080p videoda saatte ~2 GB harcanırken, Sadece Ses modunda saatte yalnızca **~30 - 60 MB** veri tüketilir (16 kat daha az veri kullanımı).
* **Akıllı HLS Akış Çözücü (`HlsParser`):**
  * Akışta bağımsız ses parçası varsa (`EXT-X-MEDIA:TYPE=AUDIO`, 64-128 Kbps AAC) video hiç indirilmez.
  * Bağımsız ses parçası olmayan kanallarda en düşük varyant (min bandwidth / 240p-360p) seçilir ve video dekoderi (`setDisplay(null)`) tamamen durdurulur.
* **OLED Dostu Ekran & TV Yanma Koruması (Burn-in Protection):**
  * Ekran piksellerinin %95'i kapatılarak panel enerji tüketimi sıfıra yaklaştırılır.
  * Ortada şık bir radyo kartı (Kanal adı, radyo dalga ikonu ve tasarruf rozeti) gösterilir.
  * OLED TV'lerde statik piksel yanmasını önlemek için kart her 60 saniyede bir hafifçe yer değiştirir (Pixel Shift).
* **Zahmetsiz Kontrol ve Hızlı Dönüş:**
  * **Yeşil Tuş (Hızlı Kalite Menüsü):** Kalite listesine `📻 Sadece Ses (Tasarruf)` seçeneği eklendi.
  * **Anında Geri Açma:** Kumandadan **OK**, **BACK** tuşlarına basıldığında veya ekrana tek tıklandığında anında normal video moduna geri dönülür.
  * Kanal değiştirildiğinde (D-Pad YUKARI/AŞAĞI) radyo modu bozulmadan bir sonraki kanalın sesine kesintisiz geçilir.

### 📶 v2.6.0 (versionCode: 19) — 17 Kasım 2026
* **Tam Otomatik Kotalı Ağ Tespiti:**
  * Android yerleşik `ConnectivityManager.isActiveNetworkMetered()` API'si ile taşınabilir 4G/5G Wi-Fi modemler, telefon hotspot paylaşımları ve hücresel mobil veri hatasız tespit edilir.
  * Normal Wi-Fi (Sınırsız) ve Ethernet bağlantıları akıllıca ayrıştırılır.
* **Kotalı Ağ Davranış Politikası (3 Mod):**
  * `[✓ Serbest Oynat]`: Ağ kotalı olsa bile yayınlar sınırsız oynatılır.
  * `[⚠️ Uyar & Onay İste]`: Kotalı ağdayken kanal açılmak istendiğinde uyarı diyalogu gösterilir, kullanıcı onayıyla yayına başlanır ve oturum boyunca tekrar rahatsız etmez.
  * `[⛔ Engelle]`: Kotalı ağda canlı yayın oynatımı engellenir ve ekranda bilgilendirme mesajı verilir.
* **Canlı Veri Sayacı & OSD Entegrasyonu:**
  * **OSD Bilgi Çubuğu Rozeti:** Saatin hemen yanında canlı akış hızı ve bu oturumda tüketilen veriyi gösteren dinamik rozet (`📶 2.4 Mbps • 140 MB` / Kotalı ağda kehribar rengi `⚠️ Kotalı • 1.8 Mbps • 45 MB`). Arayüz ölçeğiyle orantılı büyür/küçülür.
  * **Ayarlar 8. Sekme ("📶 Ağ & Veri"):** Anlık Hız (Mbps), Bu Oturumda (MB), Bugün (MB) ve geriye dönük dinamik **Son 30 Gün (Rolling 30 Days)** toplam tüketimi gösterilir.
  * **Sayaç Sıfırlama:** `[🔄 Sıfırla]` butonuyla tüm veri sayaçlarını tek tıkla sıfırlama imkanı.
* **3 Bağımsız Kota Limiti & Özel Giriş:**
  * **Oturum Kotası**, **Günlük Kota** ve **Son 30 Günlük Kota** için birbirinden bağımsız limit belirleme.
  * Hazır şablonlar (500 MB, 1 GB, 2 GB, 5 GB, 10 GB, 25 GB, 50 GB) + `[✏️ Özel Limit Gir...]` ile kumanda/dokunmatik uyumlu serbest GB/MB girişi.
  * **Yumuşak Durdurma:** Kota aşıldığında yayın nazikçe duraklatılır ve kullanıcıya devam etme ya da durdurma seçeneği sunulur.
* **Sıfır Ek Yük:** Harici kütüphane eklenmeden doğrudan Linux kernel `TrafficStats` soket sayacıyla geliştirildi (%0 CPU / RAM yükü).

### 🎛️ v2.5.0 (versionCode: 18) — 16 Kasım 2026
* **Kanal Sıralama & Yeniden Düzenleme (A+B Hibrit Mimarisi):**
  * **Ayarlar -> Kanallar Sekmesine Entegre:** Kullanıcı güvenliği için sıralama işlemleri doğrudan ayarlar menüsündeki kanal yönetim listesine yerleştirildi.
  * **[Yöntem A] Canlı Taşı (Seç & Taşı Modu):** Kumandadan kanala uzun basıldığında *"Canlı Taşı"* seçilebilir. Kanal kehribar rengiyle vurgulanır; kumandanın **YUKARI / AŞAĞI** tuşlarına basarak kanal canlı olarak yer değiştirir, **OK** tuşuyla yeni yerine kilitlenir.
  * **[Yöntem B] Numarayla Işınla:** 100+ kanallı listelerde tuşa onlarca kez basmak yerine doğrudan hedef sıra numarası (örn: 1) girilerek kanal istenen sıraya ışınlanır.
  * **En Başa / En Sona Taşı:** Tek tıkla kanalı 1. sıraya veya en sona alma kısayolları.
* **Klasik TV Mantığı (Liste Sırası = Kanal Numarası):**
  * Bir kanal taşındığında sıra numarası (`id`) otomatik olarak `1, 2, 3...` şeklinde güncellenir. Kumandadan `1` tuşlandığında artık 1. sıradaki kanal açılır.
  * **Favori ve Gizleme Koruması:** Kanallar yeniden numaralandırılsa dahi kullanıcının favorileri ve pasif kanal ayarları akıllı nesne eşlemesiyle %100 korunur.
  * **Yedekleme Uyumluluğu:** Ayarlar dışa aktarıldığında (`saftv_backup.json`), kanallar kullanıcının belirlediği yeni sıralamayla kaydedilir ve içe aktarıldığında bu sıra bozulmadan geri yüklenir.
* **Otomatik Sıralama Şablonları:**
  * `[🔤 A-Z]`: Kanalları Türkçe alfabetik sıraya dizer.
  * `[📑 Kategori]`: Kanalları öncelikli yayın türlerine göre (Ulusal, Haber, Spor, Belgesel, Çocuk, Müzik, Kültür, Bölgesel) bloklar halinde gruplar.
  * `[🔄 Sıfırla]`: İstenildiği an listenin ilk orijinal dizilimine tek tıkla geri dönme güvencesi.
* **Çekmece Alt Bilgi & Arama Kutusu Ölçeklemesi:**
  * Çekmecenin en altındaki `[OK/Dokun] İzle | [Uzun Bas] Favori | [✕/GERİ] Kapat` rehber metni ve arama kutusu fontları arayüz ölçeğiyle (`10 * scale`) birebir uyumlu hale getirildi. %200 dev ekranda 20sp, %60'ta 6sp olarak ölçeklenir.

---

### 🛡️ v2.4.0 (versionCode: 17) — 16 Kasım 2026
* **%100 Yasal Açık Kaynak Çekirdeği (Tamamen Bağımsız IPTV Oynatıcı):**
  * Uygulamanın Java kaynak kodlarından tüm sabit kanal isimleri, kategorileri ve yayın URL'leri kaldırıldı.
  * Saf TV artık VLC, Kodi veya TiviMate gibi tamamen tarafsız, yasal ve bağımsız bir medya oynatıcıdır.
* **Gelişmiş Çalma Listesi İçe ve Dışa Aktarma (M3U & JSON):**
  * **Dosyadan İçe Aktar:** Cihazın `Download` klasöründeki standart `.m3u`, `.m3u8` veya `.json` çalma listelerini otomatik algılar. Birden fazla dosya varsa şık bir seçim penceresi sunar.
  * **Web Bağlantısından İndir (URL):** Doğrudan HTTP/HTTPS bağlantısı veya kısaltılmış linkler (`tinyurl.com/...`, `is.gd/...`) üzerinden canlı liste indirme. Otomatik HTTP yönlendirme (301/302) ve modern tarayıcı User-Agent başlığı ile tam uyumluluk.
  * **Tek Tıkla Sıfırlama:** Ayarlar menüsüne eklenen *"Çalma Listesini Temizle"* butonuyla liste istenildiği an sıfırlanabilir.
  * **Tam Sistem Yedeği:** "Ayarları & Kanalları Dışa Aktar" özelliği artık kullanıcının özel kanallarını da tek bir JSON dosyasına eksiksiz yedekler.
* **Çift APK Mimarisi (Tohumlu & Tohumsuz):**
  * **SafTV.apk (Yasal Sürüm):** Açık kaynak repoda ve GitHub'da yer alan, sıfır kanalla açılan, harici liste ekleme özellikli tertemiz medya oynatıcı.
  * **SafTV_Full.apk (Özel Tohumlu Sürüm):** Özel dağıtım için; ilk kurulumda 160 onaylı kanalı otomatik yükleyen, uygulamanın kendisinin bile sonradan eklenmiş gibi gördüğü dinamik tohum mimarisi.
  * İki sürüm de aynı paket adı (`com.saftv.app`) ve aynı imza anahtarını kullandığından, birbirlerinin üzerine veri kaybı olmadan doğrudan güncellenebilir.
* **0 Kanal Güvenlik Koruması:**
  * Çalma listesi boşken veya temizlendiğinde uygulamanın çökmesi engellendi; kullanıcıya yönlendirici bir bilgilendirme ekranı sunuldu.
* **Yol Haritası Notu:**
  * Gelecek sürümler için (v2.6.0) *"Kanal Sıralama & Yeniden Düzenleme"* özelliği planlama havuzuna alındı.

---

### 📐 v2.3.0 (versionCode: 16) — 16 Kasım 2026
* **Genişletilmiş Arayüz Ölçeği (%60 — %200 Skalası):**
  * Kullanıcı ihtiyaçları ve dev salon televizyonları göz önüne alınarak ölçek sınırları radikal şekilde genişletildi:
    * `[%60 Ultra Mikro (18+ Kanal)]`: Yüksek çözünürlüklü tablet ve monitörlerde tek ekranda maksimum kanal listelemek isteyenlere.
    * `[%80 Kompakt]`: Küçük ekranlar ve kompakt telefonlar için.
    * `[%100 Standart Mobil]`: Standart mobil ve tablet düzeni.
    * `[%125 Tablet / Küçük TV]`: Mutfak TV'leri ve yakın mesafeden izleme.
    * `[%150 Büyük TV / 3m]`: 3 metreden kumandayla net okunan standart televizyon boyutu.
    * `[%175 Dev Ekran / 75"+ TV]`: 75 inç ve üzeri dev televizyonlar için optimize büyük butonlar.
    * `[%200 Maksimum Erişilebilirlik (4m+ Salon TV)]`: 4+ metre koltuk mesafesinden veya gözlük takmadan rahatça okunan devasa puntolar.
  * **Dinamik Çift Yönlü Güvenlik Koruması:** Sol menü ekranın %52'sini kesinlikle aşamaz; %60 ölçekte dahi kanal adlarının kesilmemesi için asgari 180dp taban genişliği güvenceye alındı.
* **İnternet Yeniden Bağlantı Güvencesi:**
  * Ağ koptuğunda çıkan üst kırmızı rozet ve ekranın ortasındaki *"📶 İnternet bağlantısı kesildi!"* uyarısının internet geldiği ilk milisaniyede ekrandan silinmesi garanti altına alındı; yayının otomatik ve pürüzsüzce devam etmesi sağlandı.

---

### 🎛️ v2.2.0 (versionCode: 15) — 15 Kasım 2026
* **7 Bağımsız Sekmeli Yatay Kayan Ayarlar Menüsü:**
  * Eski "Tercihler" sekmesi altında sıkışan tüm bağımsız ayarlar kendi özel sekmelerine ayrıldı:
    1. `📋 Kanallar`: Aktif/pasif kanal yönetimi ve toplu açma/kapama butonları.
    2. `📶 Kalite`: Otomatik, 1080p, 720p, 480p ve 360p varsayılan akış kalitesi tercihi.
    3. `📏 Görünüm`: %80 - %150 arası hassas 7 kademeli arayüz ve yazı boyutu ölçekleme.
    4. `🌙 Uyku`: 15, 30, 60, 90, 120 dakikalık geri sayımlı akıllı uyku zamanlayıcısı.
    5. `🎧 Ses Modu`: Ekran kapalıyken yayının sesini kesintisiz dinleme modu (pil tasarruflu).
    6. `💾 Yedekleme`: Kanalları, favorileri ve görünüm tercihlerini dışa/içe aktarma.
    7. `ℹ️ Hakkında`: Sürüm bilgisi, doğrudan güncelleme denetleme ve GitHub bağlantısı.
* **Akıllı Yatay Kaydırma ve Otomatik Ortalama (HorizontalScrollView):**
  * **Kenar Yumuşatma (Fading Edge):** Sekmelerin sağ kenarında 28dp'lik şeffaflık/gölge geçişi uygulanarak kullanıcının menünün yatayda kaydığını ilk bakışta sezgisel olarak anlaması sağlandı.
  * **TV Kumandası D-Pad Otomatik Ortalama:** Kumandanın sağ/sol tuşlarıyla sekmeler arasında gezinirken veya tıklandığında, odaklanan sekme akıcı bir animasyonla ekranın tam merkezine otomatik hizalanır.
  * **Dokunmatik Kaydırma:** Telefon ve tabletlerde parmakla sağa-sola son derece akıcı ve takılmasız kaydırma desteği.

---

### 📏 v2.1.0 (versionCode: 14) — 15 Kasım 2026
* **Hassas Yüzdesel Arayüz Ölçeği (%80 — %150 Skalası):**
  * Android TV, TV Box ve telefonlar için TiviMate ve IPTV endüstri standardı 7 kademeli ölçekleme sistemi getirildi.
  * `[● Otomatik]`: Cihaz türünü tanır; televizyonda %130, mobilde %100 ölçeği devreye sokar.
  * `[%80 Ultra Kompakt]`: Tek ekranda 12'den fazla kanalı görmek isteyenler için daraltılmış düzen.
  * `[%90 Kompakt]`: Küçük ekranlı mobil aygıtlar için optimize görünüm.
  * `[%100 Standart Mobil]`: Standart mobil ve tablet yerleşimi.
  * `[%115 Tablet / Yakın TV]`: Geniş tabletler, katlanabilir ekranlar veya mutfak TV'leri için ideal boyut.
  * `[%130 Büyük TV / 3m]`: 3 metreden kumandayla net okunan standart televizyon boyutu.
  * `[%150 Dev TV / Uzak TV]`: 4+ metre mesafeden veya gözlük takmadan rahatça okunabilen devasa butonlar ve puntolar.
* **Dinamik Çekmece Genişliği:**
  * Menü genişliği ölçeğe göre esner (`270dp * ölçek`); ekranın %52'sini kesinlikle aşmayacak şekilde sınırlandırılarak arka plandaki canlı yayının görünmesi sağlanır.
* **GitHub Tabanlı 0-Saniye OTA Güncelleme Altyapısı:**
  * Güncelleme motoru yüksek hızlı GitHub Releases ve Raw CDN ağına bağlandı. Archive.org kotaları veya gecikmeleri nedeniyle yaşanan bekleme sorunları kökten çözüldü.

---

### 🚀 v2.0.0 (versionCode: 13) — 15 Kasım 2026
* **Resim İçinde Resim (PiP - Yüzen Pencere):**
  * Android 8.0 ve üzeri cihazlarda üst barda `[🗗 PiP]` butonu veya yayındayken Ana Ekran (Home) tuşuna basıldığında yayın ekranda yüzen küçük pencereye dönüşür. Diğer uygulamalar kullanılırken yayın devam eder.
* **Akıllı Uyku Zamanlayıcısı:**
  * Ayarlar menüsünden 15, 30, 60, 90, 120 dakika olarak ayarlanabilir. Canlı kalan süre anlık gösterilir, süre bitiminde yayını durdurarak uygulamayı nazikçe kapatır.
* **Ekran Kapalı Dinleme Modu (Arka Planda Ses):**
  * Telefon kilitlendiğinde veya ekran kapatıldığında yayının sesi çalmaya devam eder; haber, tartışma veya müzik kanallarında maksimum pil tasarrufu sağlar.

---

### 🚪 v1.9.0 (versionCode: 12) — 15 Kasım 2026
* **Onay Pencereli Güvenli Çıkış:**
  * Canlı yayındayken yanlışlıkla GERİ tuşuna basılması durumunda ekrana onay diyaloğu getirilir: *"Saf TV'den çıkmak istiyor musunuz? [✕ Vazgeç] [✓ Evet, Çık]"*.
* **Canlı Yayını Duraklatma / Oynatma:**
  * Ekrana dokunulduğunda ortada beliren butonla veya kumandanın Oynat/Duraklat tuşlarıyla yayın tampondan durdurulabilir ve devam ettirilebilir.
* **Başlıksız 7+ Kanallı Sol Menü:**
  * Çekmece başlığı kaldırılarak doğrudan arama çubuğu ve kategorilerle başlaması sağlandı; ekranda aynı anda en az 7 kanal görünür hale getirildi.

---

### 🧭 v1.8.0 (versionCode: 11) — 15 Kasım 2026
* **Esnek ve Akıllı Üst Bar:** TV ve yatay ekranda tek satırda dizilen, dikey mobilde taşmayan esnek buton düzeni.
* **4 Sekmeli Modüler Ayarlar Paneli:** Kanallar, Tercihler, Yedekleme ve Hakkında sekmeleri ayrıldı.
* **Tümünü Kapat Butonu:** Tek tıkla tüm kanalları pasif yapıp sadece istenen birkaç kanalı açabilme imkanı.

---

### 📐 v1.7.0 (versionCode: 10) — 15 Kasım 2026
* **Kompakt Çekmece Düzeni:** Tek ekranda en az 5 kanal kartı görünür hale getirildi.
* **Tarih Damgalı Ayar Yedekleme:** Tercihleri ve kanal listesini JSON dosyası olarak cihaza kaydetme ve geri yükleme.
* **Çevrim İçi Güncelleme Denetimi:** Ayarlar ekranından doğrudan en yeni sürümü denetleme.

---

### 📺 v1.6.0 (versionCode: 9) — 15 Kasım 2026
* **10 Yeni Canlı Kanal Genişlemesi:** Lalegül TV, Semerkand TV, CNN Türk, DHA Canlı, TGRT Belgesel, National Geographic Wild, Çiftçi TV, TRT EBA İlkokul, TRT EBA Ortaokul, TRT EBA Lise eklenerek toplam kanal sayısı 61'e çıkarıldı.

---

### 🔍 v1.5.0 (versionCode: 8) — 14 Kasım 2026
* **Canlı Kanal Arama Çubuğu:** Çekmece açıldığında harf yazıldıkça anında filtreleme yapan arama motoru.
* **İnternet Kopması Bildirimi:** Ağ kesintilerini anlık algılayıp ekrana şık uyarı verme ve ağ geri geldiğinde yayını otomatik yeniden başlatma.
* **Resmi Geliştirici Sertifikası:** Uygulama resmi `saftv-release.jks` anahtarıyla imzalanarak kalıcı güncelleme uyumluluğu sağlandı.

---

### 🛡️ v1.4.1 (versionCode: 7) — 14 Kasım 2026
* **Dayanıklılık ve Kararlılık:** Yeniden bağlanma döngüsü maksimum 5 denemeyle sınırlandırıldı.
* **Bellek ve Soket İyileştirmeleri:** HlsParser bağlantıları temizlendi, cihaz uykudan uyandığında yaşanan siyah ekran donması giderildi.

---

### 📱 v1.4.0 (versionCode: 6) — 14 Kasım 2026
* **Tam Dokunmatik Ekran Deneyimi:** Yukarı/aşağı kaydırarak kanal değiştirme, soldan sağa çekerek kanal listesini açma, çift dokunarak ekran oranını değiştirme, ekranda yüzen yön butonları.

---

### 🐛 v1.3.1 (versionCode: 5) — 14 Kasım 2026
* Kanal listesinde OK tuşuna basıldığında kanalın hemen açılması sağlandı. Ayarlar ekranında kumanda gezinimi iyileştirildi.

---

### ⚙️ v1.3.0 (versionCode: 4) — 14 Kasım 2026
* **Kanal Yönetimi:** İstenmeyen kanalları pasif yapıp gizleyebilme.
* **Çoklu Kalite Seçimi:** 1080p, 720p, 480p, 360p yayın çözünürlükleri ve Yeşil Tuş hızlı seçim menüsü.

---

### ⭐ v1.2.0 (versionCode: 3) — 14 Kasım 2026
* **Favori Kanallar:** Sarı Tuş veya uzun dokunma ile kanalları yıldızlama, favoriler kategorisi.
* **Ekran Oranı:** Mavi Tuş ile Tam Ekran (Stretch) ve 16:9 Doğal (Fit) modları arasında geçiş.

---

### 📺 v1.1.0 (versionCode: 2) — 14 Kasım 2026
* Kanal sayısı 23'ten 51'e yükseltildi.
* Ekrana canlı dijital saat (`HH:mm`) ve 8 genişletilmiş kategori sekmesi eklendi. Kumandadan 5'er kanal atlama özelliği getirildi.

---

### 🚀 v1.0.0 (versionCode: 1) — 14 Kasım 2026
* İlk resmi sürüm. 23 ulusal kanal, sıfır reklam, yerel donanım hızlandırmalı video oynatıcı, D-Pad kumanda gezinimi ve bilgi ekranı.
