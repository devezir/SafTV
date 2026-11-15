import os
import internetarchive as ia

base_dir = os.path.dirname(os.path.abspath(__file__))
access_key = "GlkhBc76XXzqCRXs"
secret_key = "VdsHRVwguutjTLxs"

config = {
    "s3": {
        "access": access_key,
        "secret": secret_key
    }
}

item = ia.get_item("saftv-android", config=config)

files_to_upload = {
    "SafTV.apk": os.path.join(base_dir, "SafTV.apk"),
    "releases/SafTV_v2.0.0_vc13.apk": os.path.join(base_dir, "releases", "SafTV_v2.0.0_vc13.apk"),
    "version.json": os.path.join(base_dir, "version.json"),
    "SafTV_All_Releases_Archive.zip": os.path.join(base_dir, "SafTV_All_Releases_Archive.zip"),
    "SafTV_Kanal_Listesi.xlsx": os.path.join(base_dir, "SafTV_Kanal_Listesi.xlsx"),
    "YOL_HARITASI.md": os.path.join(base_dir, "YOL_HARITASI.md")
}

print("[*] Starting upload with official internetarchive library...")
responses = item.upload(
    files_to_upload,
    retries=15,
    retries_sleep=12,
    verbose=True
)

for r in responses:
    print(f"[+] Response: {r.status_code}")

print("[*] Checking item files...")
item = ia.get_item("saftv-android", config=config)
for f in item.files:
    print(" -", f.get("name"), f.get("size"))
