package com.saftv.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * SafTV - Yedekleme, Geri Yükleme ve Çalma Listesi Yöneticisi
 * Ayarların ve kanal listelerinin JSON/M3U formatında dışa ve içe aktarılmasını tek merkezden yönetir (DRY).
 */
public class BackupManager {

    public interface BackupListener {
        void onChannelsReloaded();
        void onPreferencesRestored();
    }

    private final Activity activity;
    private final ChannelManager channelManager;
    private final BackupListener listener;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public BackupManager(Activity activity, ChannelManager channelManager, BackupListener listener) {
        this.activity = activity;
        this.channelManager = channelManager;
        this.listener = listener;
    }

    public void exportSettings() {
        try {
            JSONObject root = new JSONObject();
            root.put("app", "SafTV");
            int vc = 29;
            String vn = "2.16.0";
            try {
                vc = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
                vn = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
            } catch (Exception ignored) {}
            root.put("versionCode", vc);
            root.put("versionName", vn);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            root.put("backupDate", sdf.format(new Date()));

            AppPreferences ap = AppPreferences.getInstance(activity);
            JSONObject prefsObj = new JSONObject();
            prefsObj.put(AppPreferences.KEY_QUALITY, ap.getStreamQuality());
            prefsObj.put(AppPreferences.KEY_ASPECT_RATIO, ap.getAspectRatioMode());
            prefsObj.put(AppPreferences.KEY_LAST_CHANNEL, ap.getLastChannelIndex());
            prefsObj.put(AppPreferences.KEY_UI_SCALE, ap.getUiScale());
            prefsObj.put(AppPreferences.KEY_OSD_TIMEOUT, ap.getOsdTimeout());
            prefsObj.put(AppPreferences.KEY_BG_AUDIO, ap.isBackgroundAudioEnabled());
            prefsObj.put(AppPreferences.KEY_EPG_ENABLED, ap.isEpgEnabled());
            prefsObj.put(AppPreferences.KEY_EPG_METERED_BLOCK, ap.isEpgMeteredBlockEnabled());
            root.put("preferences", prefsObj);

            JSONArray favArray = new JSONArray();
            for (String f : channelManager.getFavoritesSet(activity)) {
                try {
                    favArray.put(Integer.parseInt(f));
                } catch (NumberFormatException ignored) {}
            }
            root.put("favorites", favArray);

            JSONArray disabledArray = new JSONArray();
            for (String d : channelManager.getDisabledChannelsSet(activity)) {
                try {
                    disabledArray.put(Integer.parseInt(d));
                } catch (NumberFormatException ignored) {}
            }
            root.put("disabled_channels", disabledArray);

            root.put("channels", new JSONArray(channelManager.exportToJson()));

            SimpleDateFormat fileDateSdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String fileName = "saf_tv_yedek_" + fileDateSdf.format(new Date()) + ".json";
            byte[] fileBytes = root.toString(2).getBytes("UTF-8");

            boolean writtenToPublic = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "application/json");
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                    Uri uri = activity.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    if (uri != null) {
                        OutputStream os = activity.getContentResolver().openOutputStream(uri);
                        if (os != null) {
                            os.write(fileBytes);
                            os.close();
                            writtenToPublic = true;
                        }
                    }
                } catch (Exception ignored) {}
            }

            if (!writtenToPublic) {
                try {
                    File publicDl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (publicDl != null) {
                        if (!publicDl.exists()) publicDl.mkdirs();
                        File pubFile = new File(publicDl, fileName);
                        FileOutputStream fos = new FileOutputStream(pubFile);
                        fos.write(fileBytes);
                        fos.close();
                        writtenToPublic = true;
                    }
                } catch (Exception ignored) {}
            }

            File appFile = null;
            File appDl = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (appDl != null) {
                if (!appDl.exists()) appDl.mkdirs();
                appFile = new File(appDl, fileName);
                FileOutputStream fos = new FileOutputStream(appFile);
                fos.write(fileBytes);
                fos.close();
            }

            String targetInfo = writtenToPublic
                    ? "Cihazın genel 'Download' (İndirilenler) klasörüne kaydedildi.\n\nDosya Adı: " + fileName
                    : "Uygulama klasörüne kaydedildi:\n" + (appFile != null ? appFile.getAbsolutePath() : fileName);

            new AlertDialog.Builder(activity)
                    .setTitle("✅ Yedekleme Başarılı")
                    .setMessage("Ayarlarınız ve kanal listeniz başarıyla dışa aktarıldı.\n\n" + targetInfo)
                    .setPositiveButton("Tamam", null)
                    .show();

        } catch (Exception e) {
            Toast.makeText(activity, "Yedekleme hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void importSettings() {
        try {
            List<File> fileList = new ArrayList<>();
            Set<String> seenNames = new HashSet<>();

            File publicDl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (publicDl != null && publicDl.exists() && publicDl.canRead()) {
                File[] arr = publicDl.listFiles();
                if (arr != null) {
                    for (File f : arr) {
                        String name = f.getName().toLowerCase();
                        if (name.endsWith(".json") || name.endsWith(".m3u") || name.endsWith(".m3u8")) {
                            if (!seenNames.contains(name)) {
                                seenNames.add(name);
                                fileList.add(f);
                            }
                        }
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    Uri queryUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                    String[] proj = new String[]{MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME};
                    Cursor c = activity.getContentResolver().query(queryUri, proj, null, null, MediaStore.MediaColumns.DATE_ADDED + " DESC");
                    if (c != null) {
                        int idCol = c.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
                        int nameCol = c.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                        while (c.moveToNext()) {
                            long id = c.getLong(idCol);
                            String displayName = c.getString(nameCol);
                            if (displayName != null) {
                                String dLower = displayName.toLowerCase();
                                if (dLower.endsWith(".json") || dLower.endsWith(".m3u") || dLower.endsWith(".m3u8")) {
                                    if (!seenNames.contains(dLower)) {
                                        seenNames.add(dLower);
                                        Uri itemUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id);
                                        File cachedFile = new File(activity.getCacheDir(), displayName);
                                        InputStream in = activity.getContentResolver().openInputStream(itemUri);
                                        if (in != null) {
                                            FileOutputStream fos = new FileOutputStream(cachedFile);
                                            byte[] buf = new byte[8192];
                                            int len;
                                            while ((len = in.read(buf)) != -1) {
                                                fos.write(buf, 0, len);
                                            }
                                            fos.close();
                                            in.close();
                                            fileList.add(cachedFile);
                                        }
                                    }
                                }
                            }
                        }
                        c.close();
                    }
                } catch (Exception ignored) {}
            }

            File dlDir = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (dlDir != null && dlDir.exists()) {
                File[] arr = dlDir.listFiles();
                if (arr != null) {
                    for (File f : arr) {
                        String name = f.getName().toLowerCase();
                        if (name.endsWith(".json") || name.endsWith(".m3u") || name.endsWith(".m3u8")) {
                            if (!seenNames.contains(name)) {
                                seenNames.add(name);
                                fileList.add(f);
                            }
                        }
                    }
                }
            }

            File intDir = activity.getFilesDir();
            if (intDir != null && intDir.exists()) {
                File[] arr = intDir.listFiles();
                if (arr != null) {
                    for (File f : arr) {
                        String name = f.getName().toLowerCase();
                        if (name.endsWith(".json") || name.endsWith(".m3u") || name.endsWith(".m3u8")) {
                            if (!name.equals("channels.json") && !seenNames.contains(name)) {
                                seenNames.add(name);
                                fileList.add(f);
                            }
                        }
                    }
                }
            }

            if (fileList.isEmpty()) {
                Toast.makeText(activity, "⚠️ İçe aktarılacak .json veya .m3u dosyası bulunamadı!\nDosyayı Download klasörüne kopyalayınız.", Toast.LENGTH_LONG).show();
                return;
            }

            if (fileList.size() == 1) {
                loadDataFromFile(fileList.get(0));
                return;
            }

            final File[] candidates = fileList.toArray(new File[0]);
            String[] fileNames = new String[candidates.length];
            for (int i = 0; i < candidates.length; i++) {
                fileNames[i] = candidates[i].getName();
            }

            new AlertDialog.Builder(activity)
                    .setTitle("📂 Yüklenecek Dosyayı Seçin")
                    .setItems(fileNames, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadDataFromFile(candidates[which]);
                        }
                    })
                    .setNegativeButton("İptal", null)
                    .show();

        } catch (Exception e) {
            Toast.makeText(activity, "Dosya tarama hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void loadDataFromFile(File file) {
        try {
            String content = IoUtils.readFileToString(file).trim();
            boolean isM3u = file.getName().toLowerCase().endsWith(".m3u")
                    || file.getName().toLowerCase().endsWith(".m3u8")
                    || content.startsWith("#EXTM3U")
                    || content.startsWith("#EXTINF");

            if (isM3u) {
                boolean ok = channelManager.importFromM3u(content, activity);
                if (ok) {
                    if (listener != null) listener.onChannelsReloaded();
                    Toast.makeText(activity, "✅ M3U Çalma Listesi Yüklendi (" + channelManager.getChannelCount() + " kanal)\n" + file.getName(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, "⚠️ M3U dosyası okunamadı veya geçerli kanal bulunamadı.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            if (content.startsWith("{")) {
                JSONObject root = new JSONObject(content);

                if (root.has("channels")) {
                    channelManager.importFromJson(root.getJSONArray("channels").toString(), activity);
                }

                if (root.has("preferences")) {
                    JSONObject p = root.getJSONObject("preferences");
                    AppPreferences ap = AppPreferences.getInstance(activity);
                    if (p.has(AppPreferences.KEY_QUALITY)) {
                        ap.setStreamQuality(p.getString(AppPreferences.KEY_QUALITY));
                    }
                    if (p.has(AppPreferences.KEY_ASPECT_RATIO)) {
                        ap.setAspectRatioMode(p.getInt(AppPreferences.KEY_ASPECT_RATIO));
                    }
                    if (p.has(AppPreferences.KEY_UI_SCALE)) {
                        ap.setUiScale(p.getString(AppPreferences.KEY_UI_SCALE));
                    }
                    if (p.has(AppPreferences.KEY_OSD_TIMEOUT)) {
                        ap.setOsdTimeout(p.getString(AppPreferences.KEY_OSD_TIMEOUT));
                    }
                    if (p.has(AppPreferences.KEY_BG_AUDIO)) {
                        ap.setBackgroundAudioEnabled(p.getBoolean(AppPreferences.KEY_BG_AUDIO));
                    }
                    if (p.has(AppPreferences.KEY_EPG_ENABLED)) {
                        ap.setEpgEnabled(p.getBoolean(AppPreferences.KEY_EPG_ENABLED));
                    }
                    if (p.has(AppPreferences.KEY_EPG_METERED_BLOCK)) {
                        ap.setEpgMeteredBlockEnabled(p.getBoolean(AppPreferences.KEY_EPG_METERED_BLOCK));
                    }
                }

                if (root.has("favorites")) {
                    JSONArray favArr = root.getJSONArray("favorites");
                    Set<String> favSet = new HashSet<>();
                    for (int i = 0; i < favArr.length(); i++) {
                        favSet.add(String.valueOf(favArr.getInt(i)));
                    }
                    channelManager.setFavoritesSet(favSet, activity);
                }

                if (root.has("disabled_channels")) {
                    JSONArray disArr = root.getJSONArray("disabled_channels");
                    Set<String> disSet = new HashSet<>();
                    for (int i = 0; i < disArr.length(); i++) {
                        disSet.add(String.valueOf(disArr.getInt(i)));
                    }
                    channelManager.setDisabledChannelsSet(disSet, activity);
                }

                channelManager.syncChannelSettings(activity);

                if (listener != null) {
                    listener.onChannelsReloaded();
                    listener.onPreferencesRestored();
                }

                Toast.makeText(activity, "✅ Yedek Başarıyla Yüklendi!\n" + file.getName(), Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(activity, "⚠️ Tanınmayan dosya formatı. Geçerli bir JSON veya M3U seçiniz.", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(activity, "Yükleme hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void downloadPlaylistFromUrl(final String urlStr) {
        Toast.makeText(activity, "🌐 Çalma listesi indiriliyor...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(15000);
                    conn.setRequestProperty("User-Agent", "SafTV-PlaylistDownloader");

                    if (conn.getResponseCode() == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        reader.close();
                        conn.disconnect();

                        final String content = sb.toString().trim();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean isM3u = urlStr.toLowerCase().endsWith(".m3u")
                                        || urlStr.toLowerCase().endsWith(".m3u8")
                                        || content.startsWith("#EXTM3U")
                                        || content.startsWith("#EXTINF");

                                if (isM3u) {
                                    boolean ok = channelManager.importFromM3u(content, activity);
                                    if (ok) {
                                        if (listener != null) listener.onChannelsReloaded();
                                        Toast.makeText(activity, "✅ M3U Çalma Listesi İndirildi (" + channelManager.getChannelCount() + " kanal)", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(activity, "⚠️ M3U ayrıştırılamadı.", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (content.startsWith("{") || content.startsWith("[")) {
                                    boolean ok = channelManager.importFromJson(content, activity);
                                    if (ok) {
                                        if (listener != null) listener.onChannelsReloaded();
                                        Toast.makeText(activity, "✅ JSON Çalma Listesi İndirildi (" + channelManager.getChannelCount() + " kanal)", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(activity, "⚠️ JSON ayrıştırılamadı.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(activity, "⚠️ Tanınmayan format.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        conn.disconnect();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "İndirme başarısız: HTTP hata kodu", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "İndirme hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
}
