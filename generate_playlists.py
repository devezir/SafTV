import os
import json

base_dir = os.path.dirname(os.path.abspath(__file__))
master_file = os.path.join(base_dir, "channels_master.json")
playlists_dir = os.path.join(base_dir, "playlists")
m3u_dir = os.path.join(playlists_dir, "m3u")
json_dir = os.path.join(playlists_dir, "json")

os.makedirs(m3u_dir, exist_ok=True)
os.makedirs(json_dir, exist_ok=True)

with open(master_file, "r", encoding="utf-8") as f:
    all_channels = json.load(f)

# Define category rules
category_mapping = {
    "01_Ulusal": lambda c: c.get("category", "").startswith("Ulusal"),
    "02_Haber_ve_Finans": lambda c: c.get("category", "").startswith("Haber"),
    "03_Spor": lambda c: c.get("category", "").startswith("Spor"),
    "04_Belgesel_ve_Bilim": lambda c: c.get("category", "").startswith("Belgesel") or c.get("category", "").startswith("Bilim"),
    "05_Cocuk_ve_Egitim": lambda c: c.get("category", "").startswith("Çocuk"),
    "06_Muzik": lambda c: c.get("category", "").startswith("Müzik"),
    "07_Kultur_ve_Dini": lambda c: c.get("category", "").startswith("Kültür"),
    "08_Bolgesel": lambda c: c.get("category", "").startswith("Bölgesel"),
    "09_Ambiyans_ve_Doga": lambda c: c.get("category", "").startswith("Ambiyans")
}

category_titles = {
    "00_Tum_Kanallar": "Tüm Kanallar (Master Liste)",
    "01_Ulusal": "Ulusal Kanallar",
    "02_Haber_ve_Finans": "Haber ve Finans Kanalları",
    "03_Spor": "Spor Kanalları",
    "04_Belgesel_ve_Bilim": "Belgesel ve Bilim Kanalları",
    "05_Cocuk_ve_Egitim": "Çocuk ve Eğitim Kanalları",
    "06_Muzik": "Müzik Kanalları",
    "07_Kultur_ve_Dini": "Kültür ve Dini Kanallar",
    "08_Bolgesel": "Bölgesel ve Yerel TV Kanalları",
    "09_Ambiyans_ve_Doga": "Ambiyans, Doğa ve Canlı Kameralar"
}

groups = {
    "00_Tum_Kanallar": all_channels
}

for cat_key, check_fn in category_mapping.items():
    groups[cat_key] = [c for c in all_channels if check_fn(c)]

def write_m3u(file_path, title, channels):
    lines = ["#EXTM3U x-tvg-url=\"\""]
    for idx, c in enumerate(channels, 1):
        name = c.get("name", f"Kanal {idx}")
        cat = c.get("category", "Genel")
        url = c.get("url", "")
        ua = c.get("userAgent", "")
        
        inf = f'#EXTINF:-1 tvg-id="{c.get("id", idx)}" tvg-name="{name}" group-title="{cat}",{name}'
        lines.append(inf)
        if ua:
            lines.append(f'#EXTVLCOPT:http-user-agent={ua}')
        lines.append(url)
    
    with open(file_path, "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")

def write_json(file_path, channels):
    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(channels, f, ensure_ascii=False, indent=2)

print("="*60)
print("KANAL LİSTELERİ OLUŞTURULUYOR")
print("="*60)

for key, ch_list in groups.items():
    m3u_path = os.path.join(m3u_dir, f"{key}.m3u8")
    json_path = os.path.join(json_dir, f"{key}.json")
    
    write_m3u(m3u_path, category_titles.get(key, key), ch_list)
    write_json(json_path, ch_list)
    print(f"[+] {key:22}: {len(ch_list):3} kanal -> {os.path.basename(m3u_path)}, {os.path.basename(json_path)}")

# Generate KANAL_LISTESI.md
md_lines = [
    "# 📺 Saf TV — Kanal Listeleri Kataloğu (M3U8 & JSON)",
    "",
    "Bu dokümanda **Saf TV** bünyesinde bulunan **120 canlı TV kanalının** kategori bazlı ayrılmış ve tüm kanalları bir arada sunan güncel bağlantıları yer almaktadır.",
    "",
    "> [!TIP]",
    "> **Nasıl Kullanılır?**",
    "> - **Saf TV İle Açma:** `.json` veya `.m3u8` dosyasını cihazınızın `Download` klasörüne atıp **Ayarlar ➔ Yedekleme ➔ İçe Aktar** diyerek doğrudan yükleyebilirsiniz.",
    "> - **Harici Oynatıcılar:** `.m3u8` linklerini VLC Player, Tivimate, Kodi veya telefon/bilgisayar IPTV oynatıcılarında doğrudan açabilirsiniz.",
    "",
    "---",
    "",
    "## 📑 Kategori Listeleri İndirme Tablosu",
    "",
    "| No | Kategori | Kanal Sayısı | 📄 M3U8 Çalma Listesi | 💾 JSON Yedek Listesi |",
    "|:---:|---|:---:|:---:|:---:|"
]

for key in sorted(groups.keys()):
    title = category_titles.get(key, key)
    count = len(groups[key])
    m3u_rel = f"playlists/m3u/{key}.m3u8"
    json_rel = f"playlists/json/{key}.json"
    if key == "00_Tum_Kanallar":
        md_lines.append(f"| ⭐ | **{title}** | **{count} Kanal** | [⬇️ **{key}.m3u8**]({m3u_rel}) | [⬇️ **{key}.json**]({json_rel}) |")
    else:
        md_lines.append(f"| {key[:2]} | {title} | {count} Kanal | [⬇️ {key}.m3u8]({m3u_rel}) | [⬇️ {key}.json]({json_rel}) |")

md_lines.extend([
    "",
    "---",
    "",
    "## 📋 Tüm Kanalların Detaylı Dökümü (120 Kanal)",
    "",
    "| No | Kanal Adı | Kategori | Yayın Türü / Akış URL |",
    "|:---:|---|:---:|---|"
])

for c in all_channels:
    c_id = c.get("id", "-")
    name = c.get("name", "")
    cat = c.get("category", "")
    url = c.get("url", "")
    md_lines.append(f"| {c_id} | **{name}** | `{cat}` | `{url}` |")

md_lines.append("")

doc_file = os.path.join(base_dir, "KANAL_LISTESI.md")
with open(doc_file, "w", encoding="utf-8") as f:
    f.write("\n".join(md_lines))

print(f"\n[✓] Doküman üretildi: {doc_file}")
print("="*60)
