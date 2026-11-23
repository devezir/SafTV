package com.saftv.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

/**
 * Saf TV - Elektronik Program Rehberi (EPG) Motoru
 * Sıkıştırılmış XMLTV (.xml.gz) akışını sıfır harici kütüphane ve minimum bellek yüküyle ayrıştırır.
 */
public class EpgManager {

    public static final String EPG_URL = "https://epgshare01.online/epgshare01/epg_ripper_TR1.xml.gz";
    public static final String CACHE_FILENAME = "epg_tr.xml.gz";
    public static final long CACHE_MAX_AGE_MS = 24 * 60 * 60 * 1000L; // 24 saat

    private static EpgManager instance;
    private final Context context;
    private final AppPreferences prefs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Kanal ID -> Program Listesi
    private final Map<String, List<EpgProgram>> channelPrograms = new ConcurrentHashMap<>();
    private final Map<String, String> aliasMap = new HashMap<>();

    private boolean isUpdating = false;

    public interface OnEpgUpdateListener {
        void onEpgUpdateStarted();
        void onEpgUpdateFinished(boolean success, int channelCount, int programCount);
    }

    private EpgManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = AppPreferences.getInstance(this.context);
        initAliasMap();
    }

    public static synchronized EpgManager getInstance(Context context) {
        if (instance == null) {
            instance = new EpgManager(context);
        }
        return instance;
    }

    private void putAliases(String epgId, String... names) {
        for (String n : names) {
            aliasMap.put(n, epgId);
        }
    }

    private void initAliasMap() {
        putAliases("TRT.1.HD.tr", "trt 1");
        putAliases("ATV.HD.tr", "atv");
        putAliases("KANAL.D.HD.tr", "kanal d");
        putAliases("SHOW.TV.HD.tr", "show tv", "show");
        putAliases("STAR.TV.HD.tr", "star tv", "star");
        putAliases("FOX.HD.tr", "now", "fox");
        putAliases("TV8.HD.tr", "tv8");
        putAliases("KANAL.7.HD.tr", "kanal 7");
        putAliases("BEYAZ.TV.HD.tr", "beyaz tv", "beyaz");
        putAliases("A2.HD.tr", "a2");
        putAliases("TEVE2.HD.tr", "teve2");
        putAliases("360.HD.tr", "360");
        putAliases("TRT.HABER.HD.tr", "trt haber");
        putAliases("NTV.HD.tr", "ntv");
        putAliases("CNN.TÜRK.HD.tr", "cnn türk", "cnn turk");
        putAliases("HABERTÜRK.tr", "habertürk", "haberturk");
        putAliases("A.HABER.HD.tr", "a haber");
        putAliases("24.TV.tr", "24 tv", "kanal 24");
        putAliases("TGRT.HABER.tr", "tgrt haber", "tgrt");
        putAliases("BLOOMBERG.HT.HD.tr", "bloomberg ht", "bloomberg");
        putAliases("TRT.TÜRK.tr", "trt türk", "trt turk");
        putAliases("TRT.AVAZ.HD.tr", "trt avaz");
        putAliases("TRT.WORLD.HD.tr", "trt world");
        putAliases("TRT.KURDİ.tr", "trt kurdi", "trt kurdî");
        putAliases("TRT.BELGESEL.HD.tr", "trt belgesel");
        putAliases("TRT.SPOR.HD.tr", "trt spor");
        putAliases("A.SPOR.tr", "a spor");
        putAliases("beIN.SPORTS.HABER.HD.tr", "bein sports haber", "beın sports haber");
        putAliases("TLC.HD.tr", "tlc");
        putAliases("DMAX.HD.tr", "dmax");
        putAliases("NATIONAL.GEOGRAPHIC.WILD.tr", "nat geo wild");
        putAliases("NATIONAL.GEOGRAPHIC.HD.tr", "national geographic", "nat geo");
        putAliases("TRT.ÇOCUK.tr", "trt çocuk", "trt cocuk");
        putAliases("MİNİKA.ÇOCUK.tr", "minika çocuk", "minika cocuk");
        putAliases("MİNİKA.GO.tr", "minika go");
        putAliases("TRT.MÜZİK.tr", "trt müzik", "trt muzik");
        putAliases("POWER.TV.HD.tr", "power tv");
        putAliases("DREAM.TV.tr", "dream tv", "dream türk");
        putAliases("FENERBAHÇE.TV.tr", "fenerbahçe tv", "fb tv");
        putAliases("SPORTS.TV.tr", "sports tv");
        putAliases("ÜLKE.TV.HD.tr", "ülke tv", "ulke tv");
        putAliases("TVNET.HD.tr", "tvnet");
        putAliases("AKİT.TV.tr", "akit tv");
        putAliases("A.PARA.tr", "a para");
        putAliases("EKOTÜRK.tr", "ekotürk");
    }

    /**
     * Kanal nesnesinden EPG ID'sini çözer.
     */
    public String resolveEpgId(Channel channel) {
        if (channel == null) return null;
        if (channel.getEpgId() != null && !channel.getEpgId().trim().isEmpty()) {
            return channel.getEpgId().trim();
        }

        String rawName = channel.getName();
        if (rawName == null) return null;

        String norm = rawName.toLowerCase(Locale.US)
                .replace("hd", "")
                .replace("canlı", "")
                .replace("fhd", "")
                .replace("uhd", "")
                .replace("4k", "")
                .trim();

        if (aliasMap.containsKey(norm)) {
            return aliasMap.get(norm);
        }

        for (Map.Entry<String, String> entry : aliasMap.entrySet()) {
            if (norm.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Uygulama açılışında arka planda önbelleği kontrol eder veya günceller.
     */
    public void initializeAsync() {
        initializeAsync(null);
    }

    public void initializeAsync(final OnEpgUpdateListener listener) {
        if (!prefs.isEpgEnabled()) return;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                File cacheFile = new File(context.getCacheDir(), CACHE_FILENAME);
                boolean cacheValid = cacheFile.exists() && cacheFile.length() > 0
                        && (System.currentTimeMillis() - cacheFile.lastModified()) < CACHE_MAX_AGE_MS;

                if (cacheValid) {
                    parseGzFile(cacheFile);
                    if (listener != null) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                int chCount = channelPrograms.size();
                                int prgCount = 0;
                                for (List<EpgProgram> list : channelPrograms.values()) {
                                    prgCount += list.size();
                                }
                                listener.onEpgUpdateFinished(true, chCount, prgCount);
                            }
                        });
                    }
                } else {
                    refreshAsync(false, listener);
                }
            }
        });
    }

    /**
     * EPG verilerini sunucudan indirir ve önbelleği yeniler.
     */
    public synchronized void refreshAsync(final boolean force, final OnEpgUpdateListener listener) {
        if (!prefs.isEpgEnabled()) return;
        if (isUpdating) return;
        isUpdating = true;

        if (listener != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onEpgUpdateStarted();
                }
            });
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                boolean ok = false;
                int chCount = 0;
                int prgCount = 0;

                try {
                    // Kotalı Ağ Koruması Denetimi
                    DataUsageManager dm = DataUsageManager.getInstance(context);
                    if (prefs.isEpgMeteredBlockEnabled() && dm.isNetworkMetered()) {
                        File cache = new File(context.getCacheDir(), CACHE_FILENAME);
                        if (cache.exists() && cache.length() > 0) {
                            parseGzFile(cache);
                            ok = true;
                        }
                        return;
                    }

                    File cacheFile = new File(context.getCacheDir(), CACHE_FILENAME);
                    boolean downloaded = downloadEpgFile(cacheFile);
                    if (downloaded) {
                        parseGzFile(cacheFile);
                        prefs.setEpgLastUpdateTime(System.currentTimeMillis());
                        ok = true;
                    } else if (cacheFile.exists() && cacheFile.length() > 0) {
                        parseGzFile(cacheFile);
                        ok = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isUpdating = false;
                    chCount = channelPrograms.size();
                    for (List<EpgProgram> list : channelPrograms.values()) {
                        prgCount += list.size();
                    }

                    final boolean finalOk = ok;
                    final int finalCh = chCount;
                    final int finalPrg = prgCount;

                    if (listener != null) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onEpgUpdateFinished(finalOk, finalCh, finalPrg);
                            }
                        });
                    }
                }
            }
        });
    }

    private boolean downloadEpgFile(File destFile) {
        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(EPG_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(12000);
            conn.setRequestProperty("User-Agent", "SafTV/2.14");

            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                fos = new FileOutputStream(destFile);
                byte[] buf = new byte[8192];
                int read;
                while ((read = is.read(buf)) != -1) {
                    fos.write(buf, 0, read);
                }
                fos.flush();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(is);
            IoUtils.closeQuietly(fos);
            if (conn != null) conn.disconnect();
        }
        return false;
    }

    private void parseGzFile(File gzFile) {
        if (gzFile == null || !gzFile.exists() || gzFile.length() == 0) return;

        FileInputStream fis = null;
        GZIPInputStream gzis = null;
        try {
            fis = new FileInputStream(gzFile);
            gzis = new GZIPInputStream(fis);

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(gzis, "UTF-8");

            SimpleDateFormat sdfWithTz = new SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US);
            SimpleDateFormat sdfNoTz = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);

            long now = System.currentTimeMillis();
            long minKeepTime = now - (4 * 60 * 60 * 1000L); // 4 saat öncesinden eskileri tutma
            long maxKeepTime = now + (36 * 60 * 60 * 1000L); // 36 saat sonrasından ilerisini tutma

            Map<String, List<EpgProgram>> tempMap = new HashMap<>();

            int eventType = parser.getEventType();
            String curChannel = null;
            String curStart = null;
            String curStop = null;
            String curTitle = null;
            String curDesc = null;
            String curTag = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    curTag = parser.getName();
                    if ("programme".equalsIgnoreCase(curTag)) {
                        curChannel = parser.getAttributeValue(null, "channel");
                        curStart = parser.getAttributeValue(null, "start");
                        curStop = parser.getAttributeValue(null, "stop");
                        curTitle = null;
                        curDesc = null;
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    if (curTag != null) {
                        String text = parser.getText();
                        if (text != null && !text.trim().isEmpty()) {
                            if ("title".equalsIgnoreCase(curTag)) {
                                curTitle = text.trim();
                            } else if ("desc".equalsIgnoreCase(curTag)) {
                                curDesc = text.trim();
                            }
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    String tag = parser.getName();
                    if ("programme".equalsIgnoreCase(tag)) {
                        if (curChannel != null && curStart != null && curStop != null && curTitle != null) {
                            long st = parseXmltvTime(curStart, sdfWithTz, sdfNoTz);
                            long sp = parseXmltvTime(curStop, sdfWithTz, sdfNoTz);

                            if (st > 0 && sp > st && sp >= minKeepTime && st <= maxKeepTime) {
                                List<EpgProgram> list = tempMap.get(curChannel);
                                if (list == null) {
                                    list = new ArrayList<>();
                                    tempMap.put(curChannel, list);
                                }
                                list.add(new EpgProgram(curChannel, st, sp, curTitle, curDesc));
                            }
                        }
                        curChannel = null;
                        curStart = null;
                        curStop = null;
                        curTitle = null;
                        curDesc = null;
                    }
                    curTag = null;
                }
                eventType = parser.next();
            }

            // Sıralama
            for (List<EpgProgram> list : tempMap.values()) {
                Collections.sort(list, new Comparator<EpgProgram>() {
                    @Override
                    public int compare(EpgProgram o1, EpgProgram o2) {
                        return Long.compare(o1.getStartMs(), o2.getStartMs());
                    }
                });
            }

            channelPrograms.clear();
            channelPrograms.putAll(tempMap);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(gzis);
            IoUtils.closeQuietly(fis);
        }
    }

    private long parseXmltvTime(String raw, SimpleDateFormat sdfTz, SimpleDateFormat sdfNoTz) {
        if (raw == null) return 0L;
        raw = raw.trim();
        try {
            if (raw.length() >= 19 && raw.contains(" ")) {
                Date d = sdfTz.parse(raw);
                return d != null ? d.getTime() : 0L;
            } else if (raw.length() >= 14) {
                Date d = sdfNoTz.parse(raw.substring(0, 14));
                return d != null ? d.getTime() : 0L;
            }
        } catch (Exception ignored) {}
        return 0L;
    }

    // --- PROGRAM SORGULAMA METOTLARI ---

    /**
     * Verilen kanalda ŞU AN yayında olan programı döndürür.
     */
    public EpgProgram getCurrentProgram(Channel channel) {
        if (!prefs.isEpgEnabled() || channel == null) return null;
        String epgId = resolveEpgId(channel);
        if (epgId == null) return null;

        List<EpgProgram> list = channelPrograms.get(epgId);
        if (list == null || list.isEmpty()) return null;

        long now = System.currentTimeMillis();
        for (EpgProgram p : list) {
            if (p.isCurrentlyAir(now)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Verilen kanalda SIRADAKİ (bir sonraki) programı döndürür.
     */
    public EpgProgram getNextProgram(Channel channel) {
        if (!prefs.isEpgEnabled() || channel == null) return null;
        String epgId = resolveEpgId(channel);
        if (epgId == null) return null;

        List<EpgProgram> list = channelPrograms.get(epgId);
        if (list == null || list.isEmpty()) return null;

        long now = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++) {
            EpgProgram p = list.get(i);
            if (p.isCurrentlyAir(now)) {
                if (i + 1 < list.size()) {
                    return list.get(i + 1);
                }
                break;
            } else if (p.isFuture(now)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Verilen kanala ait kayıtlı tüm günün program listesini döndürür.
     */
    public List<EpgProgram> getProgramsForChannel(Channel channel) {
        if (!prefs.isEpgEnabled() || channel == null) return Collections.emptyList();
        String epgId = resolveEpgId(channel);
        if (epgId == null) return Collections.emptyList();

        List<EpgProgram> list = channelPrograms.get(epgId);
        return list != null ? Collections.unmodifiableList(list) : Collections.<EpgProgram>emptyList();
    }

    public boolean hasPrograms(Channel channel) {
        String epgId = resolveEpgId(channel);
        return epgId != null && channelPrograms.containsKey(epgId);
    }

    public boolean isUpdating() {
        return isUpdating;
    }

    public int getLoadedChannelCount() {
        return channelPrograms.size();
    }
}
