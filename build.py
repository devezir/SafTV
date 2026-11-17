import os
import sys
import json
import re
import subprocess
import zipfile
import shutil
import glob
from datetime import datetime

base_dir = os.path.dirname(os.path.abspath(__file__))
tools_dir = r"C:\Users\Vezir\tools"
jdk_bin = r"C:\Users\Vezir\tools\jdk-21\bin"

aapt2 = os.path.join(tools_dir, "aapt2.exe")
android_jar = os.path.join(tools_dir, "android-33.jar")
r8_jar = os.path.join(tools_dir, "r8.jar")
signer_jar = os.path.join(tools_dir, "uber-apk-signer.jar")
javac = os.path.join(jdk_bin, "javac.exe")
java = os.path.join(jdk_bin, "java.exe")

version_file = os.path.join(base_dir, "version.json")
manifest_file = os.path.join(base_dir, "AndroidManifest.xml")
releases_dir = os.path.join(base_dir, "releases")
os.makedirs(releases_dir, exist_ok=True)

# 1. Version Management
with open(version_file, "r", encoding="utf-8") as f:
    ver_data = json.load(f)

cur_vc = ver_data.get("versionCode", 1)
cur_vn = ver_data.get("versionName", "1.0.0")

desc = "Build update"

if "--bump" in sys.argv:
    bump_idx = sys.argv.index("--bump")
    bump_type = sys.argv[bump_idx + 1] if len(sys.argv) > bump_idx + 1 else "minor"
    parts = [int(p) for p in cur_vn.split(".")]
    while len(parts) < 3:
        parts.append(0)
    
    if bump_type == "major":
        parts[0] += 1
        parts[1] = 0
        parts[2] = 0
    elif bump_type == "minor":
        parts[1] += 1
        parts[2] = 0
    else: # patch
        parts[2] += 1

    cur_vn = f"{parts[0]}.{parts[1]}.{parts[2]}"
    cur_vc += 1
    if len(sys.argv) > bump_idx + 2 and not sys.argv[bump_idx + 2].startswith("--"):
        desc = sys.argv[bump_idx + 2]
    ver_data["versionCode"] = cur_vc
    ver_data["versionName"] = cur_vn
    ver_data["apkName"] = f"SafTV_v{cur_vn}_vc{cur_vc}.apk"
    ver_data["apkUrl"] = f"https://github.com/devezir/SafTV/releases/download/v{cur_vn}/SafTV_v{cur_vn}_vc{cur_vc}.apk"
    ver_data["history"].append({
        "versionCode": cur_vc,
        "versionName": cur_vn,
        "apkName": f"SafTV_v{cur_vn}_vc{cur_vc}.apk",
        "date": datetime.now().strftime("%Y-%m-%d"),
        "description": desc
    })
    with open(version_file, "w", encoding="utf-8") as f:
        json.dump(ver_data, f, indent=2, ensure_ascii=False)
    print(f"[*] Version bumped to {cur_vn} (versionCode: {cur_vc})")

elif "--set" in sys.argv:
    idx = sys.argv.index("--set")
    cur_vn = sys.argv[idx + 1]
    cur_vc = int(sys.argv[idx + 2])
    if len(sys.argv) > idx + 3:
        desc = sys.argv[idx + 3]
    ver_data["versionCode"] = cur_vc
    ver_data["versionName"] = cur_vn
    ver_data["apkName"] = f"SafTV_v{cur_vn}_vc{cur_vc}.apk"
    ver_data["apkUrl"] = f"https://github.com/devezir/SafTV/releases/download/v{cur_vn}/SafTV_v{cur_vn}_vc{cur_vc}.apk"
    ver_data["history"].append({
        "versionCode": cur_vc,
        "versionName": cur_vn,
        "apkName": f"SafTV_v{cur_vn}_vc{cur_vc}.apk",
        "date": datetime.now().strftime("%Y-%m-%d"),
        "description": desc
    })
    with open(version_file, "w", encoding="utf-8") as f:
        json.dump(ver_data, f, indent=2, ensure_ascii=False)
    print(f"[*] Version set to {cur_vn} (versionCode: {cur_vc})")

# 2. Sync AndroidManifest.xml
with open(manifest_file, "r", encoding="utf-8") as f:
    manifest_content = f.read()

manifest_content = re.sub(r'android:versionCode="\d+"', f'android:versionCode="{cur_vc}"', manifest_content)
manifest_content = re.sub(r'android:versionName="[^"]+"', f'android:versionName="{cur_vn}"', manifest_content)

with open(manifest_file, "w", encoding="utf-8") as f:
    f.write(manifest_content)
print(f"[*] AndroidManifest.xml updated: versionCode={cur_vc}, versionName={cur_vn}")

os.chdir(base_dir)

# 3. Compile Resources
print("\n[1/5] Compiling Android resources with AAPT2...")
res_zip = os.path.join(base_dir, "compiled_res.zip")
if os.path.exists(res_zip):
    os.remove(res_zip)

cmd_compile = [aapt2, "compile", "--dir", "res", "-o", res_zip]
res = subprocess.run(cmd_compile, capture_output=True, text=True)
if res.returncode != 0:
    print("AAPT2 compile error:", res.stderr)
    sys.exit(1)
print("AAPT2 compile OK")

# 4. Link Resources & Generate R.java
print("[2/5] Linking resources and generating R.java...")
base_apk = os.path.join(base_dir, "base.apk")
if os.path.exists(base_apk):
    os.remove(base_apk)

cmd_link = [
    aapt2, "link", res_zip,
    "-I", android_jar,
    "--manifest", "AndroidManifest.xml",
    "-o", base_apk,
    "--java", "gen",
    "--auto-add-overlay"
]
res = subprocess.run(cmd_link, capture_output=True, text=True)
if res.returncode != 0:
    print("AAPT2 link error:", res.stderr)
    sys.exit(1)
print("AAPT2 link OK")

# 5. Compile Java
print("[3/5] Compiling Java code with javac...")
obj_dir = os.path.join(base_dir, "obj")
shutil.rmtree(obj_dir, ignore_errors=True)
os.makedirs(obj_dir, exist_ok=True)

java_files = glob.glob("gen/**/*.java", recursive=True) + glob.glob("src/**/*.java", recursive=True)
cmd_javac = [
    javac,
    "-encoding", "UTF-8",
    "-source", "1.8",
    "-target", "1.8",
    "-cp", android_jar,
    "-d", obj_dir
] + java_files
res = subprocess.run(cmd_javac, capture_output=True, text=True)
if res.returncode != 0:
    print("javac error:", res.stderr)
    print("stdout:", res.stdout)
    sys.exit(1)
print("javac OK")

# 6. Convert to DEX
print("[4/5] Converting classes to DEX with D8...")
bin_dir = os.path.join(base_dir, "bin")
shutil.rmtree(bin_dir, ignore_errors=True)
os.makedirs(bin_dir, exist_ok=True)

class_files = glob.glob("obj/**/*.class", recursive=True)
cmd_d8 = [
    java, "-cp", r8_jar,
    "com.android.tools.r8.D8",
    "--lib", android_jar,
    "--min-api", "21",
    "--output", bin_dir
] + class_files
res = subprocess.run(cmd_d8, capture_output=True, text=True)
if res.returncode != 0:
    print("D8 error:", res.stderr)
    sys.exit(1)
print("D8 OK. Generated DEX files:", os.listdir(bin_dir))

dex_file = os.path.join(bin_dir, "classes.dex")

# 7. Package Flavors & Sign
print("[5/5] Packaging flavors and signing with uber-apk-signer...")

flavors_to_build = []
if "--flavor" in sys.argv:
    f_idx = sys.argv.index("--flavor")
    f_val = sys.argv[f_idx + 1].lower() if len(sys.argv) > f_idx + 1 else "both"
    if f_val in ["legal", "full"]:
        flavors_to_build = [f_val]
    else:
        flavors_to_build = ["legal", "full"]
else:
    flavors_to_build = ["legal", "full"]

release_keystore = os.path.join(base_dir, "keystore", "saftv-release.jks")

def build_and_sign_flavor(flavor_name, seed_json_path, root_apk_name, release_apk_name):
    flavor_unsigned = os.path.join(base_dir, f"base_{flavor_name}.apk")
    shutil.copy2(base_apk, flavor_unsigned)
    
    with zipfile.ZipFile(flavor_unsigned, "a") as z:
        z.write(dex_file, "classes.dex")
        if seed_json_path and os.path.exists(seed_json_path):
            z.write(seed_json_path, "assets/seed_channels.json")
            print(f"[*] [{flavor_name.upper()}] Added {seed_json_path} -> assets/seed_channels.json")
        else:
            print(f"[*] [{flavor_name.upper()}] Clean build (0 seed channels, unseeded player)")

    out_dir = os.path.join(base_dir, f"out_{flavor_name}")
    shutil.rmtree(out_dir, ignore_errors=True)
    os.makedirs(out_dir, exist_ok=True)

    cmd_sign = [
        java, "-jar", signer_jar,
        "-a", flavor_unsigned,
        "-o", out_dir,
        "--allowResign"
    ]
    if os.path.exists(release_keystore):
        cmd_sign += [
            "--ks", release_keystore,
            "--ksAlias", "saftv",
            "--ksPass", "REDACTED_PASSWORD",
            "--ksKeyPass", "REDACTED_PASSWORD"
        ]
    res = subprocess.run(cmd_sign, capture_output=True, text=True)
    if res.returncode != 0:
        print(f"Signing error for {flavor_name}:", res.stderr)
        return None

    final_apks = glob.glob(os.path.join(out_dir, "*.apk"))
    if not final_apks:
        print(f"No APK generated for {flavor_name}")
        return None

    rel_apk_path = os.path.join(releases_dir, release_apk_name)
    shutil.copy2(final_apks[0], rel_apk_path)

    latest_apk_path = os.path.join(base_dir, root_apk_name)
    shutil.copy2(final_apks[0], latest_apk_path)

    # Clean temporary unsigned & out dir
    if os.path.exists(flavor_unsigned):
        os.remove(flavor_unsigned)
    shutil.rmtree(out_dir, ignore_errors=True)

    size = os.path.getsize(rel_apk_path)
    return rel_apk_path, latest_apk_path, size

results = {}

if "legal" in flavors_to_build:
    res = build_and_sign_flavor(
        flavor_name="legal",
        seed_json_path=None,
        root_apk_name="SafTV.apk",
        release_apk_name=f"SafTV_v{cur_vn}_vc{cur_vc}.apk"
    )
    if res:
        results["LEGAL (Tohumsuz / Yasal)"] = res

if "full" in flavors_to_build:
    master_seed = os.path.join(base_dir, "channels_master.json")
    if not os.path.exists(master_seed):
        print(f"[!] Warning: {master_seed} not found, full build will be unseeded.")
        master_seed = None
    res = build_and_sign_flavor(
        flavor_name="full",
        seed_json_path=master_seed,
        root_apk_name="SafTV_Full.apk",
        release_apk_name=f"SafTV_Full_v{cur_vn}_vc{cur_vc}.apk"
    )
    if res:
        ch_count = 0
        if master_seed and os.path.exists(master_seed):
            try:
                with open(master_seed, 'r', encoding='utf-8') as f:
                    ch_count = len(json.load(f))
            except Exception:
                pass
        results[f"FULL (Tohumlu / {ch_count} Kanal)"] = res

# Clean base.apk
if os.path.exists(base_apk):
    os.remove(base_apk)

print("\n" + "="*60)
print(f"BUILD FINISHED! Version: v{cur_vn} (versionCode: {cur_vc})")
for flavor, (rel_path, latest_path, size) in results.items():
    print(f"\n--- {flavor} ---")
    print(f"Archived APK: {rel_path}")
    print(f"Latest APK:   {latest_path}")
    print(f"File Size:    {size} bytes (~{size/1024:.1f} KB)")
print("="*60 + "\n")

# Purge jsDelivr cache to make updates instantly discoverable
try:
    import urllib.request
    urllib.request.urlopen("https://purge.jsdelivr.net/gh/devezir/SafTV@main/version.json", timeout=5)
    print("[*] jsDelivr cache purged (updates available instantly worldwide)")
except Exception:
    pass
