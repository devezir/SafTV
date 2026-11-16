package com.saftv.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChannelManager {
    private static final String PREFS_FAV = "saf_tv_favs";
    private static final String KEY_FAV_SET = "favorite_channel_ids";

    private static final String PREFS_SETTINGS = "saf_tv_channel_settings";
    private static final String KEY_DISABLED_CHANNELS = "disabled_channels";

    private static final String CHANNELS_FILE_NAME = "channels.json";
    private static final String SEED_ASSET_NAME = "seed_channels.json";

    private static ChannelManager instance;
    private final List<Channel> channels;
    private boolean isInitialized = false;

    private ChannelManager() {
        channels = new ArrayList<>();
    }

    public static synchronized ChannelManager getInstance() {
        if (instance == null) {
            instance = new ChannelManager();
        }
        return instance;
    }

    public synchronized void init(Context context) {
        if (isInitialized && !channels.isEmpty()) {
            return;
        }
        if (context == null) return;

        channels.clear();

        // 1. Önce cihazın yerel depolama alanındaki (files/channels.json) dosyaya bak
        boolean loadedFromLocal = loadFromLocalFile(context);

        // 2. Eğer yerel dosya yoksa veya boşsa, APK içindeki tohum (seed) dosyasına bak (Full sürüm tohumu)
        if (!loadedFromLocal || channels.isEmpty()) {
            boolean loadedFromSeed = loadFromSeedAsset(context);
            if (loadedFromSeed && !channels.isEmpty()) {
                // Tohumdan başarıyla yüklendi, artık yerel depolamaya kaydet
                saveToLocalFile(context);
            }
        }

        isInitialized = true;
        syncChannelSettings(context);
    }

    private boolean loadFromLocalFile(Context context) {
        try {
            File file = new File(context.getFilesDir(), CHANNELS_FILE_NAME);
            if (!file.exists() || file.length() == 0) return false;

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            fis.close();

            return parseJsonChannelArray(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean loadFromSeedAsset(Context context) {
        try {
            InputStream is = context.getAssets().open(SEED_ASSET_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            is.close();

            return parseJsonChannelArray(sb.toString());
        } catch (Exception ignored) {
            // Seed dosyası yok (Yasal / Tohumsuz sürüm)
            return false;
        }
    }

    private synchronized boolean parseJsonChannelArray(String jsonStr) {
        try {
            if (jsonStr == null || jsonStr.trim().isEmpty()) return false;
            JSONArray arr;
            String trimmed = jsonStr.trim();
            if (trimmed.startsWith("{")) {
                JSONObject obj = new JSONObject(trimmed);
                arr = obj.optJSONArray("channels");
                if (arr == null) return false;
            } else if (trimmed.startsWith("[")) {
                arr = new JSONArray(trimmed);
            } else {
                return false;
            }

            List<Channel> newChannels = new ArrayList<>();
            String defaultUa = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                int id = item.optInt("id", i + 1);
                String name = item.optString("name", "Kanal " + id);
                String category = item.optString("category", "Genel");
                String url = item.optString("url", "");
                String ua = item.optString("userAgent", defaultUa);
                String referer = item.has("referer") && !item.isNull("referer") ? item.optString("referer", null) : null;

                if (!url.isEmpty()) {
                    newChannels.add(new Channel(id, name, category, url, ua, referer));
                }
            }

            if (!newChannels.isEmpty()) {
                channels.clear();
                channels.addAll(newChannels);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean saveToLocalFile(Context context) {
        if (context == null) return false;
        try {
            String jsonStr = exportToJson();
            File file = new File(context.getFilesDir(), CHANNELS_FILE_NAME);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonStr.getBytes("UTF-8"));
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean importFromJson(String jsonContent, Context context) {
        boolean ok = parseJsonChannelArray(jsonContent);
        if (ok) {
            saveToLocalFile(context);
            syncChannelSettings(context);
        }
        return ok;
    }

    public synchronized boolean importFromM3u(String m3uContent, Context context) {
        if (m3uContent == null || m3uContent.trim().isEmpty()) return false;
        try {
            String[] lines = m3uContent.split("\\r?\\n");
            List<Channel> parsed = new ArrayList<>();
            String curName = null;
            String curCat = "Genel";
            String defaultUa = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

            Pattern groupPattern = Pattern.compile("group-title=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.isEmpty()) continue;

                if (line.toUpperCase().startsWith("#EXTINF")) {
                    Matcher m = groupPattern.matcher(line);
                    if (m.find()) {
                        curCat = m.group(1).trim();
                    } else {
                        curCat = "Genel";
                    }

                    int commaIdx = line.lastIndexOf(',');
                    if (commaIdx != -1 && commaIdx < line.length() - 1) {
                        curName = line.substring(commaIdx + 1).trim();
                    } else {
                        curName = "Kanal " + (parsed.size() + 1);
                    }
                } else if (!line.startsWith("#")) {
                    if (line.startsWith("http://") || line.startsWith("https://")) {
                        String chName = curName != null ? curName : "Kanal " + (parsed.size() + 1);
                        int id = parsed.size() + 1;
                        parsed.add(new Channel(id, chName, curCat, line, defaultUa, null));
                        curName = null;
                        curCat = "Genel";
                    }
                }
            }

            if (!parsed.isEmpty()) {
                channels.clear();
                channels.addAll(parsed);
                saveToLocalFile(context);
                syncChannelSettings(context);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized void clearChannels(Context context) {
        channels.clear();
        if (context != null) {
            try {
                File file = new File(context.getFilesDir(), CHANNELS_FILE_NAME);
                if (file.exists()) file.delete();
            } catch (Exception ignored) {}
            setDisabledChannelsSet(new HashSet<String>(), context);
            setFavoritesSet(new HashSet<String>(), context);
        }
    }

    public synchronized String exportToJson() {
        try {
            JSONArray arr = new JSONArray();
            for (Channel c : channels) {
                JSONObject obj = new JSONObject();
                obj.put("id", c.getId());
                obj.put("name", c.getName());
                obj.put("category", c.getCategory());
                obj.put("url", c.getUrl());
                obj.put("userAgent", c.getUserAgent());
                if (c.getReferer() != null) {
                    obj.put("referer", c.getReferer());
                }
                arr.put(obj);
            }
            return arr.toString(2);
        } catch (Exception e) {
            return "[]";
        }
    }

    // ==========================================
    // KANAL YÖNETİMİ & AKTİF / PASİF DURUMU
    // ==========================================
    public void syncChannelSettings(Context context) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        Set<String> disabled = prefs.getStringSet(KEY_DISABLED_CHANNELS, null);
        if (disabled == null) {
            for (Channel c : channels) {
                c.setActive(true);
            }
            return;
        }
        for (Channel c : channels) {
            c.setActive(!disabled.contains(String.valueOf(c.getId())));
        }
    }

    public boolean toggleChannelActive(int channelId, Context context) {
        Channel target = null;
        for (Channel c : channels) {
            if (c.getId() == channelId) {
                target = c;
                break;
            }
        }
        if (target == null) return false;

        boolean newActive = !target.isActive();
        target.setActive(newActive);

        SharedPreferences prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        Set<String> cur = prefs.getStringSet(KEY_DISABLED_CHANNELS, new HashSet<String>());
        Set<String> updated = new HashSet<>(cur);

        String idStr = String.valueOf(channelId);
        if (newActive) {
            updated.remove(idStr);
        } else {
            updated.add(idStr);
        }
        prefs.edit().putStringSet(KEY_DISABLED_CHANNELS, updated).apply();
        return newActive;
    }

    public void resetAllChannelsActive(Context context) {
        for (Channel c : channels) {
            c.setActive(true);
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_DISABLED_CHANNELS).apply();
    }

    public void disableAllChannels(Context context) {
        Set<String> allDisabled = new HashSet<>();
        for (Channel c : channels) {
            c.setActive(false);
            allDisabled.add(String.valueOf(c.getId()));
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        prefs.edit().putStringSet(KEY_DISABLED_CHANNELS, allDisabled).apply();
    }

    public List<Channel> getAllChannels() {
        return channels;
    }

    public List<Channel> getActiveChannels() {
        List<Channel> activeList = new ArrayList<>();
        for (Channel c : channels) {
            if (c.isActive()) {
                activeList.add(c);
            }
        }
        if (activeList.isEmpty() && !channels.isEmpty()) {
            activeList.add(channels.get(0));
        }
        return activeList;
    }

    public List<Channel> getChannelsByCategory(String category, Context context) {
        if (category == null || category.equalsIgnoreCase("Tümü")) {
            return getActiveChannels();
        }
        if (category.equalsIgnoreCase("Favoriler") || category.contains("Favori")) {
            return getFavoriteChannels(context);
        }
        List<Channel> filtered = new ArrayList<>();
        String catLower = category.toLowerCase(new java.util.Locale("tr", "TR"));
        for (Channel c : getActiveChannels()) {
            if (c.getCategory().toLowerCase(new java.util.Locale("tr", "TR")).contains(catLower)) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    public List<Channel> searchChannels(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getActiveChannels();
        }
        String q = query.trim().toLowerCase(new java.util.Locale("tr", "TR"));
        List<Channel> filtered = new ArrayList<>();
        for (Channel c : getActiveChannels()) {
            String name = c.getName().toLowerCase(new java.util.Locale("tr", "TR"));
            String cat = c.getCategory().toLowerCase(new java.util.Locale("tr", "TR"));
            String num = String.valueOf(c.getId());
            if (name.contains(q) || cat.contains(q) || num.equals(q)) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    public Channel getChannelByIndex(int index) {
        List<Channel> active = getActiveChannels();
        if (index >= 0 && index < active.size()) {
            return active.get(index);
        }
        if (!active.isEmpty()) {
            return active.get(0);
        }
        return null;
    }

    public int getChannelCount() {
        return getActiveChannels().size();
    }

    // ==========================================
    // FAVORİ YÖNETİMİ
    // ==========================================
    public boolean isFavorite(int channelId, Context context) {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FAV, Context.MODE_PRIVATE);
        Set<String> favs = prefs.getStringSet(KEY_FAV_SET, null);
        return favs != null && favs.contains(String.valueOf(channelId));
    }

    public boolean toggleFavorite(int channelId, Context context) {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FAV, Context.MODE_PRIVATE);
        Set<String> curFavs = prefs.getStringSet(KEY_FAV_SET, new HashSet<String>());
        Set<String> newFavs = new HashSet<>(curFavs);

        String idStr = String.valueOf(channelId);
        boolean added;
        if (newFavs.contains(idStr)) {
            newFavs.remove(idStr);
            added = false;
        } else {
            newFavs.add(idStr);
            added = true;
        }
        prefs.edit().putStringSet(KEY_FAV_SET, newFavs).apply();
        return added;
    }

    public List<Channel> getFavoriteChannels(Context context) {
        List<Channel> favList = new ArrayList<>();
        if (context == null) return favList;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FAV, Context.MODE_PRIVATE);
        Set<String> favs = prefs.getStringSet(KEY_FAV_SET, null);
        if (favs == null || favs.isEmpty()) {
            return favList;
        }
        for (Channel c : getActiveChannels()) {
            if (favs.contains(String.valueOf(c.getId()))) {
                favList.add(c);
            }
        }
        return favList;
    }

    public Set<String> getFavoritesSet(Context context) {
        if (context == null) return new HashSet<>();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FAV, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_FAV_SET, null);
        return set != null ? new HashSet<>(set) : new HashSet<String>();
    }

    public void setFavoritesSet(Set<String> favs, Context context) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FAV, Context.MODE_PRIVATE);
        prefs.edit().putStringSet(KEY_FAV_SET, favs != null ? favs : new HashSet<String>()).apply();
    }

    public Set<String> getDisabledChannelsSet(Context context) {
        if (context == null) return new HashSet<>();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_DISABLED_CHANNELS, null);
        return set != null ? new HashSet<>(set) : new HashSet<String>();
    }

    public void setDisabledChannelsSet(Set<String> disabled, Context context) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        prefs.edit().putStringSet(KEY_DISABLED_CHANNELS, disabled != null ? disabled : new HashSet<String>()).apply();
        syncChannelSettings(context);
    }
}
