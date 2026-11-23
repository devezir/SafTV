package com.saftv.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * SafTV - OTA Güncelleme ve APK Kurulum Yöneticisi
 * Güncelleme kontrolü, arka plan indirme ve paket kurulumunu tek merkezden yönetir (DRY).
 */
public class UpdateManager {

    private final Activity activity;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private File pendingInstallFile = null;

    public UpdateManager(Activity activity) {
        this.activity = activity;
    }

    public void onResume() {
        if (pendingInstallFile != null && pendingInstallFile.exists()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || activity.getPackageManager().canRequestPackageInstalls()) {
                File fileToInstall = pendingInstallFile;
                pendingInstallFile = null;
                installApk(fileToInstall);
            }
        }
    }

    public int getAppVersionCode() {
        try {
            return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 29;
        }
    }

    public String getAppVersionName() {
        try {
            return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "2.16.0";
        }
    }

    public void checkUpdatesOnline() {
        Toast.makeText(activity, "🔄 Güncellemeler denetleniyor...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] endpoints = new String[] {
                    "https://raw.githubusercontent.com/devezir/SafTV/main/version.json?t=" + System.currentTimeMillis(),
                    "https://cdn.jsdelivr.net/gh/devezir/SafTV@main/version.json?t=" + System.currentTimeMillis()
                };

                JSONObject json = null;
                int maxFoundVc = -1;
                for (String endpointUrl : endpoints) {
                    try {
                        URL url = new URL(endpointUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setUseCaches(false);
                        conn.setDefaultUseCaches(false);
                        conn.setConnectTimeout(5000);
                        conn.setReadTimeout(5000);
                        conn.setRequestProperty("User-Agent", "SafTV-Updater");
                        conn.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                        conn.setRequestProperty("Pragma", "no-cache");

                        if (conn.getResponseCode() == 200) {
                            String resp = IoUtils.readStreamToString(conn.getInputStream());
                            conn.disconnect();
                            JSONObject candidate = new JSONObject(resp);
                            int candidateVc = candidate.optInt("versionCode", 0);
                            if (candidateVc > maxFoundVc) {
                                maxFoundVc = candidateVc;
                                json = candidate;
                            }
                            if (maxFoundVc > getAppVersionCode()) {
                                break;
                            }
                        } else {
                            conn.disconnect();
                        }
                    } catch (Exception ignored) {}
                }

                if (json != null) {
                    final int remoteVc = json.optInt("versionCode", 0);
                    final String remoteVn = json.optString("versionName", "");
                    final String downloadUrl = json.optString("apkUrl",
                            "https://github.com/devezir/SafTV/releases/download/v" + remoteVn + "/SafTV_v" + remoteVn + "_vc" + remoteVc + ".apk");

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (remoteVc > getAppVersionCode()) {
                                new AlertDialog.Builder(activity)
                                        .setTitle("🚀 Yeni Sürüm Mevcut!")
                                        .setMessage("Saf TV v" + remoteVn + " yayında.\nYeni sürümü şimdi indirip kurmak ister misiniz?")
                                        .setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startApkDownload(downloadUrl, remoteVn);
                                            }
                                        })
                                        .setNeutralButton("Tarayıcıda Aç", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                openInBrowser(downloadUrl);
                                            }
                                        })
                                        .setNegativeButton("Daha Sonra", null)
                                        .show();
                            } else {
                                Toast.makeText(activity, "✓ Saf TV güncel (v" + getAppVersionName() + ")", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Güncelleme sunucusuna ulaşılamadı (İnterneti kontrol edin)", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void startApkDownload(final String apkUrl, final String versionName) {
        float density = activity.getResources().getDisplayMetrics().density;
        int padH = (int) (24 * density);
        int padV = (int) (16 * density);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(padH, padV, padH, padV);

        final TextView tvStatus = new TextView(activity);
        tvStatus.setText("İndiriliyor: %0 (0 KB / 0 KB)");
        tvStatus.setTextColor(Color.WHITE);
        tvStatus.setTextSize(14);
        layout.addView(tvStatus);

        final ProgressBar progressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (12 * density)
        );
        pbParams.topMargin = (int) (12 * density);
        pbParams.bottomMargin = (int) (12 * density);
        progressBar.setLayoutParams(pbParams);
        layout.addView(progressBar);

        final TextView tvSpeed = new TextView(activity);
        tvSpeed.setText("Bağlanıyor...");
        tvSpeed.setTextColor(AppColors.TEXT_MUTED);
        tvSpeed.setTextSize(12);
        tvSpeed.setGravity(Gravity.RIGHT);
        layout.addView(tvSpeed);

        final AlertDialog downloadDialog = new AlertDialog.Builder(activity)
                .setTitle("📥 Saf TV v" + versionName + " İndiriliyor")
                .setView(layout)
                .setCancelable(false)
                .setNegativeButton("İptal", null)
                .show();

        final boolean[] isCancelled = new boolean[]{false};
        downloadDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                isCancelled[0] = true;
                downloadDialog.dismiss();
                Toast.makeText(activity, "İndirme iptal edildi", Toast.LENGTH_SHORT).show();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                File outputDir = activity.getExternalFilesDir(null);
                if (outputDir == null) {
                    outputDir = activity.getCacheDir();
                }
                final File targetApk = new File(outputDir, "SafTV_Update_v" + versionName + ".apk");

                boolean success = false;
                String errorMsg = "";

                try {
                    URL url = new URL(apkUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(15000);
                    conn.setInstanceFollowRedirects(true);
                    conn.setRequestProperty("User-Agent", "SafTV-Downloader");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == 307 || responseCode == 308) {
                        String newUrl = conn.getHeaderField("Location");
                        conn.disconnect();
                        url = new URL(newUrl);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(15000);
                        conn.setRequestProperty("User-Agent", "SafTV-Downloader");
                        responseCode = conn.getResponseCode();
                    }

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        throw new Exception("HTTP " + responseCode + " " + conn.getResponseMessage());
                    }

                    final int fileLength = conn.getContentLength();
                    InputStream input = conn.getInputStream();
                    FileOutputStream output = new FileOutputStream(targetApk);

                    byte[] data = new byte[8192];
                    long total = 0;
                    int count;
                    long startTime = System.currentTimeMillis();
                    long lastUpdateTime = 0;

                    while ((count = input.read(data)) != -1) {
                        if (isCancelled[0]) {
                            output.close();
                            input.close();
                            conn.disconnect();
                            if (targetApk.exists()) targetApk.delete();
                            return;
                        }

                        total += count;
                        output.write(data, 0, count);

                        long now = System.currentTimeMillis();
                        if (now - lastUpdateTime > 200 || total == fileLength) {
                            lastUpdateTime = now;
                            final long curTotal = total;
                            final int progress = (fileLength > 0) ? (int) (curTotal * 100 / fileLength) : 0;
                            long elapsed = now - startTime;
                            final double speedKbps = elapsed > 0 ? (curTotal / 1024.0) / (elapsed / 1000.0) : 0;

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isCancelled[0]) return;
                                    progressBar.setProgress(progress);
                                    String curStr = (curTotal / 1024) + " KB";
                                    String totStr = (fileLength > 0) ? (fileLength / 1024) + " KB" : "? KB";
                                    tvStatus.setText("İndiriliyor: %" + progress + " (" + curStr + " / " + totStr + ")");
                                    tvSpeed.setText(String.format(Locale.US, "%.1f KB/s", speedKbps));
                                }
                            });
                        }
                    }

                    output.flush();
                    output.close();
                    input.close();
                    conn.disconnect();
                    success = true;

                } catch (Exception e) {
                    errorMsg = e.getMessage() != null ? e.getMessage() : "Bilinmeyen hata";
                    if (targetApk.exists()) targetApk.delete();
                }

                final boolean finalSuccess = success;
                final String finalError = errorMsg;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadDialog.isShowing()) {
                            downloadDialog.dismiss();
                        }
                        if (isCancelled[0]) return;

                        if (finalSuccess && targetApk.exists() && targetApk.length() > 0) {
                            installApk(targetApk);
                        } else {
                            new AlertDialog.Builder(activity)
                                    .setTitle("⚠️ İndirme Başarısız")
                                    .setMessage("Güncelleme indirilemedi: " + finalError + "\nTarayıcı ile indirmek ister misiniz?")
                                    .setPositiveButton("Tarayıcıda Aç", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            openInBrowser(apkUrl);
                                        }
                                    })
                                    .setNegativeButton("Kapat", null)
                                    .show();
                        }
                    }
                });
            }
        }).start();
    }

    public void installApk(final File targetApk) {
        if (targetApk == null || !targetApk.exists()) {
            Toast.makeText(activity, "Hata: APK dosyası bulunamadı", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.getPackageManager().canRequestPackageInstalls()) {
                pendingInstallFile = targetApk;
                new AlertDialog.Builder(activity)
                        .setTitle("⚙️ Kurulum İzni Gerekli")
                        .setMessage("Saf TV'nin güncellemeyi kurabilmesi için 'Bilinmeyen uygulamaları yükle' iznini açmanız gerekmektedir.")
                        .setPositiveButton("Ayarlara Git", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                                    activity.startActivity(intent);
                                } catch (Exception e) {
                                    try {
                                        activity.startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
                                    } catch (Exception ignored) {}
                                }
                            }
                        })
                        .setNegativeButton("İptal", null)
                        .show();
                return;
            }
        }

        try {
            Uri apkUri = Uri.parse("content://com.saftv.app.apkprovider/" + targetApk.getName());
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            activity.startActivity(installIntent);
        } catch (Exception e) {
            new AlertDialog.Builder(activity)
                    .setTitle("⚠️ Kurulum Başlatılamadı")
                    .setMessage("Paket yükleyici açılamadı: " + e.getMessage() + "\nTarayıcı üzerinden indirmek ister misiniz?")
                    .setPositiveButton("Tarayıcıda Aç", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openInBrowser("https://github.com/devezir/SafTV/releases");
                        }
                    })
                    .setNegativeButton("Kapat", null)
                    .show();
        }
    }

    public void openInBrowser(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "Tarayıcı açılamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
