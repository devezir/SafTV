package com.saftv.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChannelManager {
    private static final String PREFS_FAV = "saf_tv_favs";
    private static final String KEY_FAV_SET = "favorite_channel_ids";

    private static final String PREFS_SETTINGS = "saf_tv_channel_settings";
    private static final String KEY_DISABLED_CHANNELS = "disabled_channels";

    private static ChannelManager instance;
    private final List<Channel> channels;

    private ChannelManager() {
        channels = new ArrayList<>();
        initChannels();
    }

    public static synchronized ChannelManager getInstance() {
        if (instance == null) {
            instance = new ChannelManager();
        }
        return instance;
    }

    private void initChannels() {
        String defaultUa = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

        // 1-14: ULUSAL & GENEL YAYINLAR
        channels.add(new Channel(1, "TRT 1", "Ulusal",
                "https://tv-trt1.medya.trt.com.tr/master.m3u8", defaultUa, null));

        channels.add(new Channel(2, "Kanal D", "Ulusal",
                "https://demiroren.daioncdn.net/kanald/kanald.m3u8?app=kanald_web&ce=3", defaultUa, "https://www.kanald.com.tr/"));

        channels.add(new Channel(3, "ATV", "Ulusal",
                "https://rnttwmjcin.turknet.ercdn.net/lcpmvefbyo/atv/atv_720p.m3u8", defaultUa, "https://www.atv.com.tr/"));

        channels.add(new Channel(4, "Show TV", "Ulusal",
                "https://ciner.daioncdn.net/showtv/showtv.m3u8?app=showtv_web", defaultUa, "https://www.showtv.com.tr/"));

        channels.add(new Channel(5, "Star TV", "Ulusal",
                "https://dogus.daioncdn.net/startv/startv_720p.m3u8?app=a20ac41e-bdc3-4aa1-934d-26b484480ac9&ce=3&sid=8l4w3lst4co5", defaultUa, "https://www.startv.com.tr/"));

        channels.add(new Channel(6, "NOW TV", "Ulusal",
                "https://uycyyuuzyh.turknet.ercdn.net/nphindgytw/nowtv/nowtv.m3u8", defaultUa, "https://www.nowtv.com.tr/"));

        channels.add(new Channel(7, "TV8", "Ulusal",
                "https://tv8.daioncdn.net/tv8/tv8.m3u8?app=7ddc255a-ef47-4e81-ab14-c0e5f2949788&ce=3", defaultUa, "https://www.tv8.com.tr/"));

        channels.add(new Channel(8, "Kanal 7", "Ulusal",
                "https://kanal7-live.daioncdn.net/kanal7/kanal7.m3u8", defaultUa, "https://www.kanal7.com/"));

        channels.add(new Channel(9, "A2 TV", "Ulusal",
                "https://rnttwmjcin.turknet.ercdn.net/lcpmvefbyo/a2tv/a2tv.m3u8", defaultUa, "https://www.a2tv.com.tr/"));

        channels.add(new Channel(10, "Teve2", "Ulusal",
                "https://demiroren.daioncdn.net/teve2/teve2.m3u8?ce=3&app=6aab838a-437e-4a1b-bbd0-e30f79cdbbbd", defaultUa, "https://www.teve2.com.tr/"));

        channels.add(new Channel(11, "Beyaz TV", "Ulusal",
                "https://beyaztv-live.daioncdn.net/beyaztv/beyaztv.m3u8", defaultUa, "https://www.beyaztv.com.tr/"));

        channels.add(new Channel(12, "360 TV", "Ulusal",
                "https://turkmedya-live.ercdn.net/tv360/tv360.m3u8", defaultUa, "https://www.tv360.com.tr/"));

        channels.add(new Channel(13, "TV4", "Ulusal",
                "https://turkmedya-live.ercdn.net/tv4/tv4.m3u8", defaultUa, "https://www.tv4.com.tr/"));

        channels.add(new Channel(14, "TRT Türk", "Ulusal",
                "https://tv-trtturk.medya.trt.com.tr/master.m3u8", defaultUa, null));

        // 15-28: HABER
        channels.add(new Channel(15, "TRT Haber", "Haber",
                "https://tv-trthaber.medya.trt.com.tr/master.m3u8", defaultUa, null));

        channels.add(new Channel(16, "NTV", "Haber",
                "https://dogus.daioncdn.net/ntv/ntv.m3u8?app=ntv_web", defaultUa, "https://www.ntv.com.tr/"));

        channels.add(new Channel(17, "HaberTürk", "Haber",
                "https://tv.ensonhaber.com/haberturk/haberturk.m3u8", defaultUa, null));

        channels.add(new Channel(18, "A Haber", "Haber",
                "https://rnttwmjcin.turknet.ercdn.net/lcpmvefbyo/ahaber/ahaber_720p.m3u8", defaultUa, "https://www.ahaber.com.tr/"));

        channels.add(new Channel(19, "Sözcü TV", "Haber",
                "https://raw.githubusercontent.com/yaskar661-lgtm/stream/refs/heads/main/filbox/szchd.m3u8", defaultUa, null));

        channels.add(new Channel(20, "Haber Global", "Haber",
                "https://tv.ensonhaber.com/haberglobal/haberglobal.m3u8", defaultUa, null));

        channels.add(new Channel(21, "Halk TV", "Haber",
                "https://halktv-live.daioncdn.net/halktv/halktv.m3u8", defaultUa, "https://halktv.com.tr/"));

        channels.add(new Channel(22, "24 TV", "Haber",
                "https://turkmedya-live.ercdn.net/tv24/tv24.m3u8", defaultUa, "https://www.yirmidort.tv/"));

        channels.add(new Channel(23, "TGRT Haber", "Haber",
                "https://canli.tgrthaber.com/tgrt.m3u8", defaultUa, "https://www.tgrthaber.com/"));

        channels.add(new Channel(24, "Bloomberg HT", "Haber",
                "https://tv.ensonhaber.com/bloomberght/bloomberght.m3u8", defaultUa, null));

        channels.add(new Channel(25, "Tele1", "Haber",
                "https://tele1-live.ercdn.net/tele1/tele1.m3u8", defaultUa, "https://tele1.com.tr/"));

        channels.add(new Channel(26, "TVNET", "Haber",
                "https://tvnet-live.lg.mncdn.com/tvnet-live/tvnet/playlist.m3u8", defaultUa, "https://www.tvnet.com.tr/"));

        channels.add(new Channel(27, "Flash Haber", "Haber",
                "https://b01c02nl.mediatriple.net/videoonlylive/mtyycglqauzjhlive/broadcast_67c053c48829f.smil/playlist.m3u8", defaultUa, "https://www.flashhabertv.com/"));

        channels.add(new Channel(28, "TRT 3 / TBMM TV", "Haber",
                "https://tv-trt3.live.trt.com.tr/master.m3u8", defaultUa, null));

        // 29-34: SPOR
        channels.add(new Channel(29, "TRT Spor", "Spor",
                "https://trt.daioncdn.net/trtspor/master.m3u8?app=web&platform=trtspor", defaultUa, "https://www.trtspor.com.tr/"));

        channels.add(new Channel(30, "TRT Spor Yıldız", "Spor",
                "https://trt.daioncdn.net/trtspor-yildiz/master.m3u8?app=web&platform=trtspor", defaultUa, "https://www.trtspor.com.tr/"));

        channels.add(new Channel(31, "A Spor", "Spor",
                "https://rnttwmjcin.turknet.ercdn.net/lcpmvefbyo/aspor/aspor_1080p.m3u8", defaultUa, "https://www.aspor.com.tr/"));

        channels.add(new Channel(32, "TV8.5", "Spor",
                "https://tv8.daioncdn.net/tv8bucuk/tv8bucuk.m3u8?app=tv8bucuk_web", defaultUa, "https://www.tv8bucuk.com/"));

        channels.add(new Channel(33, "HT Spor", "Spor",
                "https://ciner.daioncdn.net/ht-spor/ht-spor.m3u8?app=web", defaultUa, "https://www.htspor.com/"));

        channels.add(new Channel(34, "FB TV", "Spor",
                "http://1hskrdto.rocketcdn.com/fenerbahcetv.smil/playlist.m3u8", defaultUa, "https://www.fenerbahce.org/"));

        // 35-37: BELGESEL
        channels.add(new Channel(35, "TRT Belgesel", "Belgesel",
                "https://tv-trtbelgesel.medya.trt.com.tr/master.m3u8", defaultUa, null));

        channels.add(new Channel(36, "DMAX", "Belgesel",
                "https://dogus.daioncdn.net/dmax/dmax.m3u8?app=dmax_web", defaultUa, "https://www.dmax.com.tr/"));

        channels.add(new Channel(37, "TLC", "Belgesel",
                "https://dogus.daioncdn.net/tlc/tlc.m3u8?app=tlc_web", defaultUa, "https://www.tlctv.com.tr/"));

        // 38-41: ÇOCUK
        channels.add(new Channel(38, "TRT Çocuk", "Çocuk",
                "https://tv-trtcocuk.medya.trt.com.tr/master.m3u8", defaultUa, null));

        channels.add(new Channel(39, "Minika Çocuk", "Çocuk",
                "https://rnttwmjcin.turknet.ercdn.net/lcpmvefbyo/minikago_cocuk/minikago_cocuk.m3u8", defaultUa, "https://www.minikacocuk.com.tr/"));

        channels.add(new Channel(40, "Minika GO", "Çocuk",
                "https://rnttwmjcin.turknet.ercdn.net/lcpmvefbyo/minikago/minikago.m3u8", defaultUa, "https://www.minikago.com.tr/"));

        channels.add(new Channel(41, "TRT Diyanet Çocuk", "Çocuk",
                "https://tv-trtdiyanetcocuk.medya.trt.com.tr/master.m3u8", defaultUa, null));

        // 42-45: MÜZİK
        channels.add(new Channel(42, "TRT Müzik", "Müzik",
                "https://tv-trtmuzik.medya.trt.com.tr/master.m3u8", defaultUa, null));

        channels.add(new Channel(43, "Power TV", "Müzik",
                "https://livetv.powerapp.com.tr/powerTV/powerhd.smil/playlist.m3u8", defaultUa, "https://www.powerapp.com.tr/"));

        channels.add(new Channel(44, "Dream Türk", "Müzik",
                "https://live.duhnet.tv/S2/HLS_LIVE/dreamturknp/playlist.m3u8", defaultUa, "https://www.dreamturk.com.tr/"));

        channels.add(new Channel(45, "Kral Pop TV", "Müzik",
                "https://dogus-live.daioncdn.net/kralpoptv/playlist.m3u8", defaultUa, "https://www.kralmuzik.com.tr/"));

        // 46-51: KÜLTÜR
        channels.add(new Channel(46, "TRT 2", "Kültür",
                "https://tv-trt2.medya.trt.com.tr/master.m3u8", defaultUa, null));

        channels.add(new Channel(47, "Diyanet TV", "Kültür",
                "https://eustr73.mediatriple.net/videoonlylive/mtikoimxnztxlive/broadcast_5e3bf95a47e07.smil/playlist.m3u8", defaultUa, "https://www.diyanet.tv/"));

        channels.add(new Channel(48, "TRT World", "Kültür",
                "https://tv-trtworld.medya.trt.com.tr/master.m3u8", defaultUa, null));

        channels.add(new Channel(49, "TRT Avaz", "Kültür",
                "https://tv-trtavaz.medya.trt.com.tr/master.m3u8", defaultUa, null));

        channels.add(new Channel(50, "TRT Arabi", "Kültür",
                "https://tv-trtarabi.medya.trt.com.tr/master.m3u8", defaultUa, null));

        channels.add(new Channel(51, "TRT Kurdî", "Kültür",
                "https://tv-trtkurdi.medya.trt.com.tr/master.m3u8", defaultUa, null));

        // 52-53: KÜLTÜR & DİNİ
        channels.add(new Channel(52, "Lalegül TV", "Kültür",
                "https://lbl.netmedya.net/hls/lalegultv.m3u8", defaultUa, "https://lalegultv.com.tr/"));

        channels.add(new Channel(53, "Semerkand TV", "Kültür",
                "https://b01c02nl.mediatriple.net/videoonlylive/mtisvwurbfcyslive/broadcast_58d915bd40efc.smil/playlist.m3u8", defaultUa, "https://www.semerkandtv.com.tr/"));

        // 54-55: HABER
        channels.add(new Channel(54, "CNN Türk", "Haber",
                "http://85.117.61.84/cnnturk/index.m3u8", defaultUa, "https://www.cnnturk.com/"));

        channels.add(new Channel(55, "DHA Canlı", "Haber",
                "https://603c568fccdf5.streamlock.net/live/dhaweb1_C5efC/playlist.m3u8", defaultUa, "https://www.dha.com.tr/"));

        // 56-58: BELGESEL & YAŞAM
        channels.add(new Channel(56, "TGRT Belgesel", "Belgesel",
                "https://b01c02nl.mediatriple.net/videoonlylive/mtsxxkzwwuqtglive/broadcast_5fe462afc6a0e.smil/playlist.m3u8", defaultUa, "https://www.tgrtbelgesel.com.tr/"));

        channels.add(new Channel(57, "National Geographic Wild", "Belgesel",
                "https://saran-live.ercdn.net/natgeowild/index.m3u8", defaultUa, "https://www.natgeotv.com/"));

        channels.add(new Channel(58, "Çiftçi TV", "Belgesel",
                "https://live.artidijitalmedya.com/artidijital_ciftcitv/ciftcitv/playlist.m3u8", defaultUa, "https://ciftcitv.com/"));

        // 59-61: ÇOCUK & EĞİTİM
        channels.add(new Channel(59, "TRT EBA İlkokul", "Çocuk",
                "https://tv-e-okul00.medya.trt.com.tr/master.m3u8", defaultUa, null));

        channels.add(new Channel(60, "TRT EBA Ortaokul", "Çocuk",
                "https://tv-e-okul01.medya.trt.com.tr/master.m3u8", defaultUa, null));

        channels.add(new Channel(61, "TRT EBA Lise", "Çocuk",
                "https://tv-e-okul02.medya.trt.com.tr/master.m3u8", defaultUa, null));
    }

    public void syncChannelSettings(Context context) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        Set<String> disabled = prefs.getStringSet(KEY_DISABLED_CHANNELS, null);
        if (disabled == null || disabled.isEmpty()) {
            // İlk kuruluşta tüm kanallar varsayılan olarak AKTİFTİR
            for (Channel c : channels) {
                c.setActive(true);
            }
        } else {
            for (Channel c : channels) {
                c.setActive(!disabled.contains(String.valueOf(c.getId())));
            }
        }
    }

    public boolean toggleChannelActive(int channelId, Context context) {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        Set<String> curDisabled = prefs.getStringSet(KEY_DISABLED_CHANNELS, new HashSet<String>());
        Set<String> newDisabled = new HashSet<>(curDisabled);

        String idStr = String.valueOf(channelId);
        boolean nowActive;
        if (newDisabled.contains(idStr)) {
            newDisabled.remove(idStr);
            nowActive = true;
        } else {
            newDisabled.add(idStr);
            nowActive = false;
        }
        prefs.edit().putStringSet(KEY_DISABLED_CHANNELS, newDisabled).apply();

        for (Channel c : channels) {
            if (c.getId() == channelId) {
                c.setActive(nowActive);
                break;
            }
        }
        return nowActive;
    }

    public void resetAllChannelsActive(Context context) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_DISABLED_CHANNELS).apply();
        for (Channel c : channels) {
            c.setActive(true);
        }
    }

    public void disableAllChannels(Context context) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        Set<String> allDisabled = new HashSet<>();
        for (Channel c : channels) {
            allDisabled.add(String.valueOf(c.getId()));
            c.setActive(false);
        }
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
        // Eğer tüm kanallar pasif yapılmışsa güvenli fallback: ilk kanal aktif döner
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
        for (Channel c : getActiveChannels()) {
            if (c.getCategory().equalsIgnoreCase(category)) {
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
        return active.get(0);
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
