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
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChannelManager {
    private static final String PREFS_FAV = "saf_tv_favs";
    private static final String KEY_FAV_SET = "favorite_channel_ids";

    private static final String PREFS_SETTINGS = "saf_tv_channel_settings";
    private static final String KEY_DISABLED_CHANNELS = "disabled_channels";

    private static final String CHANNELS_FILE_NAME = "channels.json";
    private static final String CHANNELS_DEFAULT_FILE = "channels_default.json";
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
                saveToDefaultFile(context);
            }
        }

        isInitialized = true;
        syncChannelSettings(context);
    }

    private boolean loadFromLocalFile(Context context) {
        try {
            File file = new File(context.getFilesDir(), CHANNELS_FILE_NAME);
            if (!file.exists() || file.length() == 0) return false;
            String jsonStr = IoUtils.readFileToString(file);
            return parseJsonChannelArray(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean loadFromSeedAsset(Context context) {
        try {
            InputStream is = context.getAssets().open(SEED_ASSET_NAME);
            String jsonStr = IoUtils.readStreamToString(is);
            return parseJsonChannelArray(jsonStr);
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
                String epgId = item.has("epgId") && !item.isNull("epgId") ? item.optString("epgId", null) : null;

                if (!url.isEmpty()) {
                    Channel ch = new Channel(id, name, category, url, ua, referer);
                    if (epgId != null && !epgId.isEmpty()) ch.setEpgId(epgId);
                    newChannels.add(ch);
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

    private synchronized boolean saveToFile(Context context, String fileName) {
        if (context == null) return false;
        try {
            String jsonStr = exportToJson();
            File file = new File(context.getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonStr.getBytes("UTF-8"));
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized boolean saveToLocalFile(Context context) {
        return saveToFile(context, CHANNELS_FILE_NAME);
    }

    public synchronized boolean importFromJson(String jsonContent, Context context) {
        boolean ok = parseJsonChannelArray(jsonContent);
        if (ok) {
            saveToLocalFile(context);
            saveToDefaultFile(context);
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
            String curEpgId = null;
            String defaultUa = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

            Pattern groupPattern = Pattern.compile("group-title=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
            Pattern epgPattern = Pattern.compile("tvg-id=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

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

                    Matcher me = epgPattern.matcher(line);
                    if (me.find()) {
                        curEpgId = me.group(1).trim();
                    } else {
                        curEpgId = null;
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
                        Channel ch = new Channel(id, chName, curCat, line, defaultUa, null);
                        if (curEpgId != null && !curEpgId.isEmpty()) ch.setEpgId(curEpgId);
                        parsed.add(ch);
                        curName = null;
                        curCat = "Genel";
                        curEpgId = null;
                    }
                }
            }

            if (!parsed.isEmpty()) {
                channels.clear();
                channels.addAll(parsed);
                saveToLocalFile(context);
                saveToDefaultFile(context);
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
                File defFile = new File(context.getFilesDir(), CHANNELS_DEFAULT_FILE);
                if (defFile.exists()) defFile.delete();
            } catch (Exception ignored) {}
            setDisabledChannelsSet(new HashSet<String>(), context);
            setFavoritesSet(new HashSet<String>(), context);
        }
    }

    public synchronized boolean saveToDefaultFile(Context context) {
        return saveToFile(context, CHANNELS_DEFAULT_FILE);
    }

    private static class ChannelStateSnapshot {
        final Set<Channel> favChannels = new HashSet<>();
        final Set<Channel> disabledChannels = new HashSet<>();
    }

    private ChannelStateSnapshot captureChannelStates(Context context) {
        ChannelStateSnapshot snapshot = new ChannelStateSnapshot();
        for (Channel c : channels) {
            if (isFavorite(c.getId(), context)) snapshot.favChannels.add(c);
            if (!c.isActive()) snapshot.disabledChannels.add(c);
        }
        return snapshot;
    }

    public synchronized boolean moveChannel(int fromPos, int toPos, Context context) {
        if (fromPos < 0 || fromPos >= channels.size() || toPos < 0 || toPos >= channels.size()) {
            return false;
        }
        if (fromPos == toPos) return true;

        ChannelStateSnapshot s = captureChannelStates(context);
        Channel item = channels.remove(fromPos);
        channels.add(toPos, item);

        renumberAndSave(s.favChannels, s.disabledChannels, context);
        return true;
    }

    public synchronized boolean swapChannels(int pos1, int pos2, Context context) {
        if (pos1 < 0 || pos1 >= channels.size() || pos2 < 0 || pos2 >= channels.size()) {
            return false;
        }
        if (pos1 == pos2) return true;

        ChannelStateSnapshot s = captureChannelStates(context);
        Collections.swap(channels, pos1, pos2);

        renumberAndSave(s.favChannels, s.disabledChannels, context);
        return true;
    }

    public synchronized void sortAlphabetical(Context context) {
        if (channels.isEmpty()) return;

        ChannelStateSnapshot s = captureChannelStates(context);
        final Collator collator = Collator.getInstance(new Locale("tr", "TR"));
        collator.setStrength(Collator.PRIMARY);

        Collections.sort(channels, new Comparator<Channel>() {
            @Override
            public int compare(Channel c1, Channel c2) {
                return collator.compare(c1.getName(), c2.getName());
            }
        });

        renumberAndSave(s.favChannels, s.disabledChannels, context);
    }

    public synchronized void sortByCategory(Context context) {
        if (channels.isEmpty()) return;

        ChannelStateSnapshot s = captureChannelStates(context);
        final Collator collator = Collator.getInstance(new Locale("tr", "TR"));
        collator.setStrength(Collator.PRIMARY);

        Collections.sort(channels, new Comparator<Channel>() {
            private int getCategoryPriority(String cat) {
                if (cat == null) return 99;
                String c = cat.toLowerCase(new Locale("tr", "TR"));
                if (c.contains("ulusal")) return 1;
                if (c.contains("haber")) return 2;
                if (c.contains("spor")) return 3;
                if (c.contains("belgesel")) return 4;
                if (c.contains("çocuk") || c.contains("cocuk")) return 5;
                if (c.contains("müzik") || c.contains("muzik")) return 6;
                if (c.contains("kültür") || c.contains("kultur")) return 7;
                if (c.contains("eğlence") || c.contains("eglence") || c.contains("sinema")) return 8;
                if (c.contains("kardeş") || c.contains("kardes") || c.contains("kıbrıs") || c.contains("kibris")) return 9;
                if (c.contains("bölgesel") || c.contains("bolgesel")) return 10;
                return 50;
            }

            @Override
            public int compare(Channel c1, Channel c2) {
                int p1 = getCategoryPriority(c1.getCategory());
                int p2 = getCategoryPriority(c2.getCategory());
                if (p1 != p2) return Integer.compare(p1, p2);
                int catComp = collator.compare(c1.getCategory(), c2.getCategory());
                if (catComp != 0) return catComp;
                return collator.compare(c1.getName(), c2.getName());
            }
        });

        renumberAndSave(s.favChannels, s.disabledChannels, context);
    }

    public synchronized boolean resetToOriginalOrder(Context context) {
        if (context == null) return false;
        try {
            File defFile = new File(context.getFilesDir(), CHANNELS_DEFAULT_FILE);
            if (defFile.exists() && defFile.length() > 0) {
                String jsonStr = IoUtils.readFileToString(defFile);
                if (!jsonStr.isEmpty()) {
                    boolean ok = parseJsonChannelArray(jsonStr);
                    if (ok) {
                        saveToLocalFile(context);
                        syncChannelSettings(context);
                        return true;
                    }
                }
            }

            boolean ok = loadFromSeedAsset(context);
            if (ok) {
                saveToLocalFile(context);
                syncChannelSettings(context);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void renumberAndSave(Set<Channel> favChannels, Set<Channel> disabledChannels, Context context) {
        Set<String> newFavs = new HashSet<>();
        Set<String> newDisabled = new HashSet<>();

        for (int i = 0; i < channels.size(); i++) {
            Channel c = channels.get(i);
            int newId = i + 1;
            c.setId(newId);

            if (favChannels != null && favChannels.contains(c)) {
                newFavs.add(String.valueOf(newId));
            }
            if (disabledChannels != null && disabledChannels.contains(c)) {
                newDisabled.add(String.valueOf(newId));
            }
        }

        if (context != null) {
            setFavoritesSet(newFavs, context);
            setDisabledChannelsSet(newDisabled, context);
            saveToLocalFile(context);
            syncChannelSettings(context);
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
                if (c.getEpgId() != null) {
                    obj.put("epgId", c.getEpgId());
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
