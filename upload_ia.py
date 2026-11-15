import os
import sys
import time
import glob
import urllib.parse
import requests

try:
    sys.stdout.reconfigure(line_buffering=True)
except Exception:
    pass

base_dir = os.path.dirname(os.path.abspath(__file__))
keys_file = os.path.join(base_dir, "archive.org api keys.txt")

if not os.path.exists(keys_file):
    print("Error: archive.org api keys.txt not found!")
    sys.exit(1)

with open(keys_file, "r", encoding="utf-8") as f:
    lines = f.readlines()

access_key = ""
secret_key = ""
for line in lines:
    if "access key" in line.lower():
        access_key = line.split()[-1].strip()
    elif "secret key" in line.lower():
        secret_key = line.split()[-1].strip()

if not access_key or not secret_key:
    print("Error: Could not parse access_key or secret_key from keys file.")
    sys.exit(1)

print(f"[*] Access Key: {access_key[:4]}...{access_key[-4:]}")
print(f"[*] Secret Key: {secret_key[:4]}...{secret_key[-4:]}")

identifier = "saftv-android"
s3_endpoint = f"https://s3.us.archive.org/{identifier}"

def to_uri_header(val):
    return f"uri({urllib.parse.quote(val)})"

title = "Saf TV — Android TV, Telefon & Tablet Canlı Yayın Uygulaması"
creator = "Saf TV"
subject = "saftv; android tv; iptv; canli tv; turkce tv; open source; apk; lightweight; live television"
language = "tur"
mediatype = "software"

description_html = """
<h2>📺 Saf TV — Android TV, Telefon & Tablet Canlı Yayın Uygulaması</h2>
<p><strong>Android TV, Google TV, Android Telefon ve Tabletler için özel olarak geliştirilmiş; sıfır reklam, sıfır izleyici, ultra hafif (~97 KB), tam dokunmatik ve kumanda destekli ışık hızında saf canlı televizyon deneyimi.</strong></p>

<hr/>

<h3>🌟 Neden Saf TV?</h3>
<ul>
  <li><strong>Ultra Hafif (~97 KB):</strong> Harici şişkin kütüphane içermez. Cihazın depolama alanında neredeyse hiç yer kaplamaz.</li>
  <li><strong>0.1 Saniyede Açılış (Cold Boot):</strong> Bekletici splash ekranları veya karmaşık menüler yoktur. Tıklandığı anda son izlenen kanal tam ekran oynamaya başlar.</li>
  <li><strong>Saf Donanım Kod Çözümü:</strong> Android yerel <code>MediaPlayer</code> ve <code>SurfaceView</code> donanım hızlandırmasını kullanır. RAM tüketimi yalnızca 25–35 MB civarındadır; cihazı asla ısıtmaz veya yormaz.</li>
  <li><strong>Resmi ve Yasal Akışlar:</strong> Kanallar üçüncü parti korsan sunuculardan değil, yayıncı kuruluşların doğrudan resmi CDN sunucularından HLS (.m3u8) formatında çekilir.</li>
  <li><strong>Çift Cihaz Uyumu (TV + Telefon / Tablet):</strong> Hem Android TV kumandası (D-Pad) ile %100 uyumludur hem de Android telefon/tabletlerde tam dokunmatik ekran, kaydırma jestleri ve ekrandaki dokunmatik kontrollerle kusursuz çalışır.</li>
  <li><strong>Sinematik Tam Ekran (Immersive Mode):</strong> Telefonlarda durum çubuğu ve navigasyon tuşları otomatik gizlenir; kenardan kenara gerçek tam ekran TV yayını sunar.</li>
  <li><strong>Sabit Geliştirici İmzası:</strong> Kalıcı 2048-bit <code>saftv-release.jks</code> mührüyle imzalanır; güncelleme yaparken eski sürümü silmeye gerek kalmaz, doğrudan üzerine kurulur.</li>
</ul>

<hr/>

<h3>🚀 Başlıca Özellikler</h3>
<ul>
  <li><strong>51 Doğrulanmış Kanal:</strong> Ulusal, Haber, Spor, Belgesel, Çocuk, Müzik ve Kültür kategorilerinde Türkiye'nin en popüler kanalları.</li>
  <li><strong>Canlı Kanal Arama & Filtreleme:</strong> Çekmecede harf yazıldıkça 51 kanal arasından anında filtreleme.</li>
  <li><strong>Canlı Ağ / İnternet Kopması Bildirimi:</strong> Wi-Fi veya Ethernet kesildiğinde uyarı verir; bağlantı geri geldiğinde yayını otomatik yeniden başlatır.</li>
  <li><strong>Gelişmiş Kanal Yönetimi:</strong> Ayarlar menüsünden istenmeyen kanalları gizleyebilme.</li>
  <li><strong>Çoklu Çözünürlük ve Kalite Seçimi:</strong> Otomatik, 1080p Full HD, 720p HD, 480p SD ve 360p Tasarruf modları.</li>
  <li><strong>Favori Kanallar:</strong> Kumandada Sarı Tuş, mobilde tek dokunuşla kanalları yıldızlama ve sadece favorileri listeleme.</li>
  <li><strong>Ekran Oranı:</strong> Tam Ekran (Stretch) ve 16:9 Orantılı (Fit) modları arasında geçiş.</li>
  <li><strong>Canlı Dijital Saat:</strong> Ekranın sol üstünde OSD üzerinde canlı dijital saat (HH:mm).</li>
</ul>

<hr/>

<h3>🎮 Kontrol Rehberi</h3>
<h4>📱 Telefon & Tablet (Dokunmatik):</h4>
<ul>
  <li><strong>Yukarı / Aşağı Kaydır:</strong> Sonraki / Önceki kanala geçer</li>
  <li><strong>Soldan Sağa Kaydır:</strong> Kanal Listesi Çekmecesini açar</li>
  <li><strong>Sağdan Sola Kaydır:</strong> Açık menüleri kapatır</li>
  <li><strong>Çift Dokunma:</strong> Ekran oranını değiştirir (Tam Ekran / 16:9 Fit)</li>
  <li><strong>Tek Dokunma:</strong> Kontrol butonlarını ve OSD kanal bilgi çubuğunu açar / kapatır</li>
</ul>

<h4>📺 Android TV (Kumanda):</h4>
<ul>
  <li><strong>YUKARI / AŞAĞI:</strong> Kanal değiştirir</li>
  <li><strong>OK (Orta Tuş):</strong> Kanal Listesi Çekmecesini açar</li>
  <li><strong>YEŞİL Tuş:</strong> Hızlı Kalite Seçim Menüsünü açar</li>
  <li><strong>SARI Tuş:</strong> Favorilere ekler / çıkarır</li>
  <li><strong>MAVİ Tuş:</strong> Ekran oranını değiştirir</li>
  <li><strong>MENÜ Tuşu:</strong> Kanal Yönetimi & Ayarlar panelini açar</li>
  <li><strong>CH+ / CH-:</strong> 5'er kanal hızlı atlar</li>
  <li><strong>Rakam Tuşları (0-9):</strong> Doğrudan kanal numarasına zıplar</li>
</ul>

<hr/>

<h3>📋 Kanal Kataloğu (51 Kanal)</h3>
<p><strong>Ulusal (14):</strong> TRT 1, Kanal D, ATV, Show TV, Star TV, NOW TV, TV8, Kanal 7, A2 TV, Teve2, Beyaz TV, 360 TV, TV4, TRT Türk.<br/>
<strong>Haber (14):</strong> TRT Haber, NTV, HaberTürk, A Haber, Sözcü TV, Haber Global, Halk TV, 24 TV, TGRT Haber, Bloomberg HT, Tele1, TVNET, Flash Haber, TRT 3 / TBMM TV.<br/>
<strong>Spor (6):</strong> TRT Spor, TRT Spor Yıldız, A Spor, TV8.5, HT Spor, FB TV.<br/>
<strong>Belgesel (3):</strong> TRT Belgesel, DMAX, TLC.<br/>
<strong>Çocuk (4):</strong> TRT Çocuk, Minika Çocuk, Minika GO, TRT Diyanet Çocuk.<br/>
<strong>Müzik (4):</strong> TRT Müzik, Power TV HD, Dream Türk, Kral Pop TV.<br/>
<strong>Kültür (6):</strong> TRT 2, Diyanet TV, TRT World, TRT Avaz, TRT Arabi, TRT Kurdî.</p>
"""

auth_header = f"LOW {access_key}:{secret_key}"

def upload_file(local_path, remote_name, is_bucket_creation=False, max_retries=6):
    url = f"{s3_endpoint}/{remote_name}"
    headers = {
        "authorization": auth_header,
    }
    if is_bucket_creation:
        headers.update({
            "x-archive-auto-make-bucket": "1",
            "x-archive-meta-title": to_uri_header(title),
            "x-archive-meta-mediatype": mediatype,
            "x-archive-meta-creator": to_uri_header(creator),
            "x-archive-meta-subject": to_uri_header(subject),
            "x-archive-meta-language": language,
            "x-archive-meta-description": to_uri_header(description_html),
            "x-archive-meta-licenseurl": "https://creativecommons.org/licenses/by-nc-nd/4.0/",
        })
    
    file_size = os.path.getsize(local_path)
    print(f"\n[*] Uploading {remote_name} ({file_size} bytes / ~{file_size/1024:.1f} KB)...")
    
    with open(local_path, "rb") as f:
        data = f.read()
    
    if remote_name.endswith(".apk"):
        headers["Content-Type"] = "application/vnd.android.package-archive"
    elif remote_name.endswith(".md") or remote_name.endswith(".txt"):
        headers["Content-Type"] = "text/plain; charset=utf-8"
        
    for attempt in range(1, max_retries + 1):
        try:
            res = requests.put(url, data=data, headers=headers, timeout=60)
            if res.status_code in [200, 201]:
                print(f"[+] Successfully uploaded: {remote_name} (Status: {res.status_code})")
                return True
            elif res.status_code == 503 or "SlowDown" in res.text:
                wait_sec = attempt * 8
                print(f"[!] Archive.org queue busy (503 SlowDown). Waiting {wait_sec}s before retry ({attempt}/{max_retries})...")
                time.sleep(wait_sec)
            else:
                print(f"[-] Failed to upload {remote_name}: {res.status_code} - {res.text}")
                if attempt < max_retries:
                    time.sleep(5)
                else:
                    return False
        except Exception as e:
            print(f"[!] Network error: {e}, retrying ({attempt}/{max_retries})...")
            time.sleep(5)
    return False

# 1. First upload SafTV.apk (this initializes the bucket with metadata)
latest_apk = os.path.join(base_dir, "SafTV.apk")
if not os.path.exists(latest_apk):
    print("Error: SafTV.apk not found in root!")
    sys.exit(1)

success = upload_file(latest_apk, "SafTV.apk", is_bucket_creation=True)
if not success:
    print("Fatal: Could not initialize Archive.org item!")
    sys.exit(1)

# 2. Upload Documentation
readme_path = os.path.join(base_dir, "README.md")
if os.path.exists(readme_path):
    upload_file(readme_path, "README.md")

release_hist_path = os.path.join(base_dir, "RELEASE_HISTORY.md")
if os.path.exists(release_hist_path):
    upload_file(release_hist_path, "RELEASE_HISTORY.md")

roadmap_path = os.path.join(base_dir, "YOL_HARITASI.md")
if os.path.exists(roadmap_path):
    upload_file(roadmap_path, "YOL_HARITASI.md")

# 3. Upload all releases
release_apks = sorted(glob.glob(os.path.join(base_dir, "releases", "*.apk")))
for r_apk in release_apks:
    fname = os.path.basename(r_apk)
    upload_file(r_apk, f"releases/{fname}")

print("\n" + "="*60)
print("ARCHIVE.ORG UPLOAD COMPLETE!")
print(f"Page Details URL:  https://archive.org/details/{identifier}")
print(f"Direct Download:   https://archive.org/download/{identifier}/SafTV.apk")
print("="*60 + "\n")
