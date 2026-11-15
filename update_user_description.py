import internetarchive as ia

access_key = "GlkhBc76XXzqCRXs"
secret_key = "VdsHRVwguutjTLxs"

config = {
    "s3": {
        "access": access_key,
        "secret": secret_key
    }
}

item = ia.get_item("saftv-android", config=config)

description_html = """
<div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; line-height: 1.6; color: #24292e; max-width: 860px; margin: 0 auto;">

  <!-- Hero Banner (HTML Styled) -->
  <div style="background: linear-gradient(135deg, #0d1117 0%, #161f2e 50%, #0d1117 100%); border: 2px solid #23354d; border-radius: 16px; padding: 35px 20px; text-align: center; margin-top: 10px; margin-bottom: 25px; box-shadow: 0 8px 30px rgba(0,0,0,0.4);">
    <div style="display: inline-block; background: rgba(29, 161, 242, 0.15); border: 1px solid #1da1f2; border-radius: 20px; padding: 6px 18px; color: #1da1f2; font-size: 13px; font-weight: bold; margin-bottom: 15px;">
      ● CANLI YAYIN &bull; ANDROID TV & MOBİL &bull; v2.1.0
    </div>
    <h1 style="color: #ffffff; font-size: 52px; font-weight: 800; margin: 0; letter-spacing: 2px;">SAF TV</h1>
    <p style="color: #8da2bb; font-size: 16px; margin: 10px 0 25px 0;">Sıfır Reklam &bull; Sıfır Şişkinlik &bull; 133 KB Işık Hızında Canlı Televizyon</p>
    
    <div style="display: flex; justify-content: center; gap: 15px; flex-wrap: wrap;">
      <div style="background: #141b28; border: 1px solid #27374d; border-radius: 10px; padding: 14px 20px; min-width: 180px; text-align: left;">
        <span style="color: #27ae60; font-size: 11px; font-weight: bold; display: block; margin-bottom: 2px;">MİMARİ</span>
        <strong style="color: #fff; font-size: 15px;">Ultra Hafif (133 KB)</strong>
        <span style="color: #8da2bb; font-size: 12px; display: block; margin-top: 4px;">0.1s Açılış &bull; Asla Kasmaz</span>
      </div>
      <div style="background: #141b28; border: 1px solid #27374d; border-radius: 10px; padding: 14px 20px; min-width: 180px; text-align: left;">
        <span style="color: #f39c12; font-size: 11px; font-weight: bold; display: block; margin-bottom: 2px;">YAYINLAR</span>
        <strong style="color: #fff; font-size: 15px;">61 Canlı Kanal</strong>
        <span style="color: #8da2bb; font-size: 12px; display: block; margin-top: 4px;">Resmi HLS &bull; Arama Desteği</span>
      </div>
      <div style="background: #141b28; border: 1px solid #27374d; border-radius: 10px; padding: 14px 20px; min-width: 180px; text-align: left;">
        <span style="color: #a78bfa; font-size: 11px; font-weight: bold; display: block; margin-bottom: 2px;">YENİ SÜRÜM</span>
        <strong style="color: #fff; font-size: 15px;">v2.1.0 Stabil</strong>
        <span style="color: #8da2bb; font-size: 12px; display: block; margin-top: 4px;">%80-%150 Hassas Ölçek &bull; PiP &bull; Uyku</span>
      </div>
    </div>
  </div>

  <!-- Büyük Direkt İndir Butonu (Tablo Tabanlı - E-posta/Web Uyumlu) -->
  <div style="text-align: center; margin: 30px 0;">
    <table align="center" border="0" cellpadding="0" cellspacing="0" style="margin: 0 auto; border-collapse: collapse;">
      <tr>
        <td align="center" bgcolor="#0969da" style="background-color: #0969da; border-radius: 10px; padding: 16px 36px; border: 2px solid #044289; box-shadow: 0 6px 20px rgba(9, 105, 218, 0.4);">
          <a href="https://archive.org/download/saftv-android/releases/SafTV_v2.1.0_vc14.apk" style="color: #ffffff !important; font-size: 20px; font-weight: bold; text-decoration: none; display: inline-block;">
            ⬇️ SAF TV v2.1.0 İNDİR (SafTV_v2.1.0_vc14.apk - 133 KB)
          </a>
        </td>
      </tr>
    </table>
    <p style="margin-top: 12px; font-size: 14px; color: #57606a;">
      ✓ Reklamsız &bull; ✓ Ücretsiz &bull; ✓ Doğrudan İndirme &bull; ✓ Boyut: Sadece 133 KB
    </p>
    <p style="margin-top: 8px; font-size: 13px; color: #24292e;">
      Doğrudan APK: <a href="https://archive.org/download/saftv-android/releases/SafTV_v2.1.0_vc14.apk" style="color: #0969da; font-weight: bold;">SafTV_v2.1.0_vc14.apk İndir</a> | Sabit Link: <a href="https://archive.org/download/saftv-android/SafTV.apk" style="color: #0969da; font-weight: bold;">SafTV.apk</a> | 📦 <a href="https://archive.org/download/saftv-android/SafTV_All_Releases_Archive.zip" style="color: #0969da; font-weight: bold;">Tüm Sürümler Arşivi (ZIP)</a> | 📊 <a href="https://archive.org/download/saftv-android/SafTV_Kanal_Listesi.xlsx" style="color: #0969da; font-weight: bold;">Kanal Listesi (Excel)</a>
    </p>
  </div>

  <hr style="border: none; border-top: 1px solid #d0d7de; margin: 30px 0;" />

  <!-- v2.1.0 Yenilikleri & Neden Saf TV? -->
  <h2 style="color: #0d1117; border-bottom: 2px solid #0969da; padding-bottom: 6px;">🌟 v2.1.0 Yenilikleri</h2>
  <ul style="font-size: 15px; line-height: 1.8;">
    <li><strong>📏 Yüzdesel Hassas Arayüz Ölçeği (%80 — %150 Skalası):</strong> Android TV, TV Box, Tablet ve Telefonlar için endüstri standardı (TiviMate ve IPTV tarzı) 7 kademeli ölçekleme:
      <ul>
        <li><code>[● Otomatik (Akıllı Cihaz Tespiti)]</code>: TV için %130, Telefon için %100 otomatik uygular.</li>
        <li><code>[%80 (Ultra Kompakt)]</code>: Ekranda maksimum sayıda (12+) kanal görmek isteyenler için.</li>
        <li><code>[%90 (Kompakt)]</code>: Küçük ekranlı telefonlar veya kompakt görünüm için.</li>
        <li><code>[%100 (Standart Mobil)]</code>: Standart mobil ve tablet arayüz boyutu.</li>
        <li><code>[%115 (Tablet / Yakın TV)]</code>: Büyük tabletler, katlanabilir ekranlar veya 1-2 metreden izlenen TV'ler için.</li>
        <li><code>[%130 (Büyük TV / 3m)]</code>: 3 metreden kumandayla net okunan ideal 10-foot TV UI boyutu.</li>
        <li><code>[%150 (Dev / Uzak TV)]</code>: 4+ metre uzak dev ekranlar veya görme kolaylığı isteyen kullanıcılar için devasa butonlar ve puntolar.</li>
      </ul>
    </li>
    <li><strong>📐 Dinamik Orantısal Çekmece Genişliği:</strong> Seçilen ölçeğe göre kanal menüsü genişliği 230dp ile 390dp arasında dinamik olarak genişler veya daralır; ekranın %52'sini asla aşmayacak şekilde sınırlandırılır.</li>
    <li><strong>💾 Tam Kalıcı Ayar Yedekleme:</strong> Seçilen arayüz ölçeği tercihi yerel bellekte saklanır ve Ayarlar Yedekleme / Geri Yükleme (JSON) sistemine dahil edilir.</li>
    <li><strong>📱 Resim İçinde Resim (PiP - Picture-in-Picture):</strong> Android 8.0 ve üzeri cihazlarda üst bardaki <code>[🗗 PiP]</code> butonuyla veya canlı yayın esnasında Ana Ekran (Home) tuşuna basıldığında küçülerek ekranda yüzen pencereye geçer. Diğer uygulamaları kullanırken veya mesajlaşırken yayın kesintisiz izlenir.</li>
    <li><strong>🌙 Uyku Zamanlayıcısı (Sleep Timer):</strong> Ayarlar Tercihler sekmesinden <code>Kapalı</code>, <code>15</code>, <code>30</code>, <code>60</code>, <code>90</code>, <code>120 dakika</code> seçenekleriyle zamanlayıcı ayarlanabilir. Canlı kalan süre anlık olarak gösterilir, süre dolduğunda yayını durdurarak uygulamayı nazikçe kapatır.</li>
    <li><strong>🎧 Ekran Kapalı Dinleme Modu (Arka Planda Ses):</strong> Ayarlar Tercihler sekmesinden tek tuşla açılır. Telefon kilitlendiğinde veya ekran kapatıldığında yayının sesi çalmaya devam eder; haber, tartışma veya müzik dinlerken olağanüstü batarya tasarrufu sağlar.</li>
    <li><strong>🚪 Güvenli Çıkış Mekanizması:</strong> Üst barda <code>[🚪 Çıkış]</code> butonu ve kumandadan çıkış için onay penceresi mevcuttur.</li>
    <li><strong>⏸️ Canlı Yayını Duraklatma / Oynatma:</strong> Dokunmatik ekran ortasındaki <code>[⏸ / ▶]</code> butonu veya kumandanın Play/Pause tuşları ile yayın kontrol edilir.</li>
    <li><strong>⚡ Ultra Hafiflik Korundu (133 KB):</strong> Hiçbir harici kütüphane eklenmeden saf yerel Android mimarisiyle 0.1 saniyede açılış devam ediyor.</li>
  </ul>

  <hr style="border: none; border-top: 1px solid #d0d7de; margin: 30px 0;" />

  <!-- Kolay Kurulum Rehberi -->
  <h2 style="color: #0d1117; border-bottom: 2px solid #0969da; padding-bottom: 6px;">📲 Nasıl Kurulur? (Çok Kolay)</h2>
  
  <h4 style="color: #1f2328; margin-bottom: 6px;">📱 Telefona veya Tablete Kurulum:</h4>
  <ol style="font-size: 14px; margin-top: 4px;">
    <li>Yukarıdaki mavi <strong>"SAF TV v2.1.0 İndir"</strong> butonuna dokunup <code>SafTV_v2.1.0_vc14.apk</code> dosyasını indirin.</li>
    <li>İndirilen dosyaya dokunun ve <strong>"Yükle"</strong> deyin. <em>(Eğer ilk kez dışarıdan uygulama yüklüyorsanız tarayıcınıza izin vermeniz istenebilir).</em></li>
    <li>Uygulama anında açılacak ve canlı yayın başlayacaktır!</li>
  </ol>

  <h4 style="color: #1f2328; margin-bottom: 6px;">📺 Android TV veya TV Box'a Kurulum:</h4>
  <ol style="font-size: 14px; margin-top: 4px;">
    <li><strong>En Kolay Yol:</strong> Telefonunuza ve Televizyonunuza Google Play'den ücretsiz <em>"Send Files to TV"</em> uygulamasını kurun. <code>SafTV.apk</code> dosyasını telefonunuzdan TV'ye kablosuz gönderip açın.</li>
    <li><strong>USB ile:</strong> <code>SafTV.apk</code> dosyasını bir USB belleğe atıp TV'nize takın ve TV'deki dosya yöneticisiyle yükleyin.</li>
  </ol>

  <hr style="border: none; border-top: 1px solid #d0d7de; margin: 30px 0;" />

  <!-- Kullanım Rehberi -->
  <h2 style="color: #0d1117; border-bottom: 2px solid #0969da; padding-bottom: 6px;">🎮 Nasıl Kullanılır?</h2>
  
  <div style="display: flex; flex-wrap: wrap; gap: 20px; margin-top: 15px;">
    <div style="flex: 1; min-width: 280px; background: #f6f8fa; padding: 18px; border-radius: 8px; border: 1px solid #d0d7de;">
      <h3 style="margin-top: 0; color: #0969da;">📺 TV Kumandası İle:</h3>
      <ul style="font-size: 14px; line-height: 1.7; padding-left: 20px;">
        <li><strong>Yukarı / Aşağı Tuşları:</strong> Kanalları değiştirir.</li>
        <li><strong>Sol / Sağ Tuşları:</strong> Önceki / Sonraki kanala geçer.</li>
        <li><strong>OK (Orta Tuş):</strong> Ekranın solundan Kanal Listesi Çekmecesini açar.</li>
        <li><strong>Play / Pause Tuşları:</strong> Canlı yayını duraklatır / devam ettirir.</li>
        <li><strong>Yeşil Tuş:</strong> Yayın kalitesini seçer (1080p, 720p, 480p).</li>
        <li><strong>Sarı Tuş:</strong> Kanalı Favorilere (⭐) ekler / çıkarır.</li>
        <li><strong>Mavi Tuş:</strong> Ekranı tam ekran veya orantılı (16:9 Fit) yapar.</li>
        <li><strong>Menü Tuşu:</strong> 4 Sekmeli Ayarlar Menüsünü açar (Kanallar, Tercihler, Yedekleme, Hakkında).</li>
        <li><strong>Geri Tuşu:</strong> Açık pencereleri kapatır, pencere yoksa Güvenli Çıkış Diyaloğunu açar.</li>
      </ul>
    </div>

    <div style="flex: 1; min-width: 280px; background: #f6f8fa; padding: 18px; border-radius: 8px; border: 1px solid #d0d7de;">
      <h3 style="margin-top: 0; color: #0969da;">📱 Telefon Dokunmatiği İle:</h3>
      <ul style="font-size: 14px; line-height: 1.7; padding-left: 20px;">
        <li><strong>Yukarı / Aşağı Kaydır:</strong> Sonraki/önceki kanala geçer.</li>
        <li><strong>Soldan Sağa Çek:</strong> Kanal listesini açar.</li>
        <li><strong>Ekrana Tek Dokunma:</strong> Kanal adını, butonları ve duraklatma kontrolünü gösterir/gizler.</li>
        <li><strong>[🗗 PiP] Butonu:</strong> Yayını ekranda yüzen küçük pencereye dönüştürür.</li>
        <li><strong>Ortadaki [⏸ / ▶] Butonu:</strong> Canlı yayını duraklatır veya devam ettirir.</li>
        <li><strong>Ekrana Çift Dokunma:</strong> Ekran oranını değiştirir (Fit / Stretch).</li>
        <li><strong>Ekrandaki Butonlar:</strong> [📶 Kalite], [📺 Ekran], [🗗 PiP], [⚙️ Ayarlar], [🚪 Çıkış], [◀ Önceki], [▶ Sonraki].</li>
      </ul>
    </div>
  </div>

  <hr style="border: none; border-top: 1px solid #d0d7de; margin: 30px 0;" />

  <!-- 61 Kanal Listesi -->
  <h2 style="color: #0d1117; border-bottom: 2px solid #0969da; padding-bottom: 6px;">📋 Uygulamadaki 61 Kanal</h2>
  <p style="font-size: 14px; line-height: 1.8;">
    <strong>Ulusal (14):</strong> TRT 1, Kanal D, ATV, Show TV, Star TV, NOW TV, TV8, Kanal 7, A2 TV, Teve2, Beyaz TV, 360 TV, TV4, TRT Türk.<br />
    <strong>Haber (16):</strong> TRT Haber, NTV, HaberTürk, A Haber, Sözcü TV, Haber Global, Halk TV, 24 TV, TGRT Haber, Bloomberg HT, Tele1, TVNET, Flash Haber, TRT 3 / TBMM TV, CNN Türk, DHA Canlı.<br />
    <strong>Spor (6):</strong> TRT Spor, TRT Spor Yıldız, A Spor, TV8.5, HT Spor, FB TV.<br />
    <strong>Belgesel & Yaşam (6):</strong> TRT Belgesel, DMAX, TLC, TGRT Belgesel, National Geographic Wild, Çiftçi TV.<br />
    <strong>Çocuk & Eğitim (7):</strong> TRT Çocuk, Minika Çocuk, Minika GO, TRT Diyanet Çocuk, TRT EBA İlkokul, TRT EBA Ortaokul, TRT EBA Lise.<br />
    <strong>Müzik (4):</strong> TRT Müzik, Power TV HD, Dream Türk, Kral Pop TV.<br />
    <strong>Kültür & Dini (8):</strong> TRT 2, Diyanet TV, TRT World, TRT Avaz, TRT Arabi, TRT Kurdî, Lalegül TV (3 Kalite HD), Semerkand TV.
  </p>

</div>
"""

print("[*] Updating metadata on Archive.org with pure modern HTML banner for v2.1.0...")
res = item.modify_metadata({"description": description_html})
print(f"[+] Metadata update response: {res.status_code}")
