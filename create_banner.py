import os
import math
from PIL import Image, ImageDraw, ImageFont, ImageFilter

width, height = 1280, 720

# 1. Base Dark Slate Navy Background
img = Image.new("RGB", (width, height), (13, 17, 23))
draw = ImageDraw.Draw(img)

# Vertical subtle dark gradient
for y in range(height):
    # from #161F2E at top to #0B0E14 at bottom
    r = int(22 - (y / height) * 11)
    g = int(31 - (y / height) * 17)
    b = int(46 - (y / height) * 26)
    draw.line([(0, y), (width, y)], fill=(r, g, b))

# 2. Add very soft background glow using a separate blurred layer
glow_layer = Image.new("RGBA", (width, height), (0, 0, 0, 0))
glow_draw = ImageDraw.Draw(glow_layer)
# Cyan soft glow behind title
glow_draw.ellipse([width//2 - 280, 80, width//2 + 280, 320], fill=(20, 115, 230, 45))
# Dark blue soft glow around
glow_layer = glow_layer.filter(ImageFilter.GaussianBlur(radius=50))
img.paste(Image.alpha_composite(img.convert("RGBA"), glow_layer).convert("RGB"), (0, 0))
draw = ImageDraw.Draw(img)

# Fonts
font_dir = "C:/Windows/Fonts"
font_huge = ImageFont.truetype(f"{font_dir}/segoeuib.ttf", 92)
font_sub = ImageFont.truetype(f"{font_dir}/segoeui.ttf", 25)
font_badge = ImageFont.truetype(f"{font_dir}/segoeuib.ttf", 15)
font_card_head = ImageFont.truetype(f"{font_dir}/segoeuib.ttf", 22)
font_card_sub = ImageFont.truetype(f"{font_dir}/segoeui.ttf", 16)
font_footer = ImageFont.truetype(f"{font_dir}/segoeui.ttf", 14)

# 3. Top Pill Badge: "● CANLI TELEVİZYON • ANDROID TV & MOBİL"
badge_text = "● CANLI YAYIN  •  ANDROID TV & MOBİL"
b_bbox = draw.textbbox((0, 0), badge_text, font=font_badge)
bw = b_bbox[2] - b_bbox[0]
bh = b_bbox[3] - b_bbox[1]
bx = (width - bw) // 2
by = 55
px, py = 20, 8
draw.rounded_rectangle([bx - px, by - py, bx + bw + px, by + bh + py], radius=16, fill=(18, 30, 49), outline=(41, 128, 185), width=1)
draw.text((bx, by), badge_text, fill=(52, 152, 219), font=font_badge)

# 4. Hero Title: "SAF TV" with gradient or clean white glow
title_text = "SAF TV"
t_bbox = draw.textbbox((0, 0), title_text, font=font_huge)
tw = t_bbox[2] - t_bbox[0]
tx = (width - tw) // 2
ty = 115

# Subtle shadow
draw.text((tx, ty + 4), title_text, fill=(5, 8, 12), font=font_huge)
draw.text((tx, ty), title_text, fill=(255, 255, 255), font=font_huge)

# 5. Subtitle
sub_text = "Sıfır Reklam  •  Sıfır Şişkinlik  •  97 KB Işık Hızında Canlı Televizyon"
s_bbox = draw.textbbox((0, 0), sub_text, font=font_sub)
sw = s_bbox[2] - s_bbox[0]
sx = (width - sw) // 2
sy = 235
draw.text((sx, sy), sub_text, fill=(140, 165, 195), font=font_sub)

# 6. Three Feature Highlight Cards
cards = [
    {
        "badge": "MİMARİ",
        "badge_color": (39, 174, 96),
        "title": "Ultra Hafif (~97 KB)",
        "desc": "0.1 Saniyede Açılış\n25–35 MB RAM Tüketimi\nSaf Donanım Hızlandırma"
    },
    {
        "badge": "YAYINLAR",
        "badge_color": (243, 156, 18),
        "title": "51 Doğrulanmış Kanal",
        "desc": "Resmi ve Kesintisiz HLS\n1080p, 720p, 480p Kalite\nCanlı Arama & Favoriler"
    },
    {
        "badge": "KONTROL",
        "badge_color": (155, 89, 182),
        "title": "Hibrit Deneyim",
        "desc": "Android TV D-Pad Kumanda\n+ Telefon Kaydırma Jestleri\nSinematik Tam Ekran"
    }
]

card_w = 350
card_h = 200
spacing = 30
total_w = len(cards) * card_w + (len(cards) - 1) * spacing
start_x = (width - total_w) // 2
card_y = 330

for i, card in enumerate(cards):
    cx = start_x + i * (card_w + spacing)
    rect = [cx, card_y, cx + card_w, card_y + card_h]
    
    # Card base
    draw.rounded_rectangle(rect, radius=16, fill=(20, 27, 40), outline=(38, 52, 75), width=2)
    
    # Mini Tag
    tag_bbox = draw.textbbox((0, 0), card["badge"], font=font_footer)
    tag_w = tag_bbox[2] - tag_bbox[0]
    tag_h = tag_bbox[3] - tag_bbox[1]
    draw.rounded_rectangle([cx + 24, card_y + 20, cx + 24 + tag_w + 14, card_y + 20 + tag_h + 8], radius=6, fill=(card["badge_color"][0]//5, card["badge_color"][1]//5, card["badge_color"][2]//5), outline=card["badge_color"], width=1)
    draw.text((cx + 31, card_y + 24), card["badge"], fill=card["badge_color"], font=font_footer)
    
    # Title
    draw.text((cx + 24, card_y + 58), card["title"], fill=(255, 255, 255), font=font_card_head)
    
    # Lines
    lines = card["desc"].split("\n")
    for li, line in enumerate(lines):
        draw.text((cx + 24, card_y + 105 + li * 26), f"• {line}", fill=(145, 165, 190), font=font_card_sub)

# 7. Modern Bottom Footer
footer_text = "Resmi Sürüm: v1.5.0  •  2048-bit RSA İmzalı  •  Açık Kaynak & Bağımsız Dağıtım  •  archive.org/details/saftv-android"
foot_bbox = draw.textbbox((0, 0), footer_text, font=font_footer)
foot_w = foot_bbox[2] - foot_bbox[0]
foot_x = (width - foot_w) // 2
draw.text((foot_x, 645), footer_text, fill=(80, 105, 135), font=font_footer)

# Save
out_path = r"d:\godlama\SafTV - android\saftv_banner.png"
img.save(out_path, "PNG", quality=95)

artifact_dir = r"C:\Users\Vezir\.gemini\antigravity\brain\759eb797-8427-4419-bfed-45ffc7b4f055"
artifact_img_path = os.path.join(artifact_dir, "saftv_banner.png")
img.save(artifact_img_path, "PNG", quality=95)
print(f"Polished banner saved to {out_path} and {artifact_img_path}")
