import os
import sys
import time
import urllib.parse
import requests

base_dir = os.path.dirname(os.path.abspath(__file__))
keys_file = os.path.join(base_dir, "archive.org api keys.txt")

with open(keys_file, "r", encoding="utf-8") as f:
    lines = f.readlines()

access_key = ""
secret_key = ""
for line in lines:
    if "access key" in line.lower():
        access_key = line.split()[-1].strip()
    elif "secret key" in line.lower():
        secret_key = line.split()[-1].strip()

identifier = "saftv-android"
s3_endpoint = f"https://s3.us.archive.org/{identifier}"
auth_header = f"LOW {access_key}:{secret_key}"

banner_path = os.path.join(base_dir, "saftv_banner.png")
if not os.path.exists(banner_path):
    print("Error: saftv_banner.png not found!")
    sys.exit(1)

with open(banner_path, "rb") as f:
    banner_data = f.read()

def upload_data(remote_name, data, content_type, extra_headers=None, max_retries=6):
    url = f"{s3_endpoint}/{remote_name}"
    headers = {
        "authorization": auth_header,
        "Content-Type": content_type
    }
    if extra_headers:
        headers.update(extra_headers)
        
    print(f"\n[*] Uploading {remote_name} ({len(data)} bytes)...")
    for attempt in range(1, max_retries + 1):
        try:
            res = requests.put(url, data=data, headers=headers, timeout=60)
            if res.status_code in [200, 201]:
                print(f"[+] Successfully uploaded {remote_name} (Status: {res.status_code})")
                return True
            elif res.status_code == 503 or "SlowDown" in res.text:
                wait_sec = attempt * 8
                print(f"[!] 503 SlowDown, waiting {wait_sec}s ({attempt}/{max_retries})...")
                time.sleep(wait_sec)
            else:
                print(f"[-] Status {res.status_code}: {res.text}")
                time.sleep(5)
        except Exception as e:
            print(f"[!] Error: {e}, waiting 5s...")
            time.sleep(5)
    return False

# 1. Upload saftv_banner.png
upload_data("saftv_banner.png", banner_data, "image/png")

# 2. Upload saftv-android_itemimage.png (Official Archive.org Item Cover/Thumbnail Convention)
upload_data("saftv-android_itemimage.png", banner_data, "image/png")

# 3. Upload Releases Archive ZIP if exists
zip_path = os.path.join(base_dir, "SafTV_All_Releases_Archive.zip")
if os.path.exists(zip_path):
    with open(zip_path, "rb") as f:
        zip_data = f.read()
    upload_data("SafTV_All_Releases_Archive.zip", zip_data, "application/zip")

print("\n" + "="*60)
print("UPLOAD COMPLETED!")
print(f"Details Page: https://archive.org/details/{identifier}")
print("="*60 + "\n")
