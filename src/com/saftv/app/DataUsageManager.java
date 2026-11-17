package com.saftv.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * Saf TV - Veri Sayacı, Kotalı Ağ Yönetimi ve 30 Günlük Veri Takip Motoru.
 * Android TrafficStats kernel soket arayüzünü kullanarak sıfır ek yükle çalışır.
 */
public class DataUsageManager {

    private static final String PREF_NAME = "saftv_data_usage";
    private static final String KEY_DAILY_HISTORY = "daily_history_json";
    private static final String KEY_METERED_POLICY = "metered_policy";
    private static final String KEY_SESSION_QUOTA = "quota_session_bytes";
    private static final String KEY_DAILY_QUOTA = "quota_daily_bytes";
    private static final String KEY_MONTHLY_QUOTA = "quota_monthly_bytes";

    // Kotalı Ağ Politikaları
    public static final int POLICY_ALLOW = 0; // Her Zaman İzin Ver
    public static final int POLICY_WARN = 1;  // Kotalı Ağda Uyar & Onay İste
    public static final int POLICY_BLOCK = 2; // Kotalı Ağda Engelle

    // Kota Aşım Türleri
    public static final int QUOTA_OK = 0;
    public static final int QUOTA_SESSION_EXCEEDED = 1;
    public static final int QUOTA_DAILY_EXCEEDED = 2;
    public static final int QUOTA_MONTHLY_EXCEEDED = 3;

    private static DataUsageManager instance;

    private final Context context;
    private final SharedPreferences prefs;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    // Canlı Ölçüm Değişkenleri
    private long lastKernelRxBytes = -1;
    private long sessionRxBytes = 0;
    private double currentSpeedMbps = 0.0;
    private long lastSpeedCheckTimeMs = 0;
    private long lastSpeedBytes = 0;

    // Oturum İçi Onay / Bastırma Bayrakları
    private boolean sessionQuotaDismissed = false;
    private boolean dailyQuotaDismissed = false;
    private boolean monthlyQuotaDismissed = false;
    private boolean meteredWarningDismissed = false;

    private DataUsageManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        initKernelBytes();
        cleanOldHistory();
    }

    public static synchronized DataUsageManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataUsageManager(context);
        }
        return instance;
    }

    private void initKernelBytes() {
        int uid = Process.myUid();
        long rx = TrafficStats.getUidRxBytes(uid);
        if (rx != TrafficStats.UNSUPPORTED) {
            lastKernelRxBytes = rx;
            lastSpeedBytes = rx;
            lastSpeedCheckTimeMs = System.currentTimeMillis();
        }
    }

    /**
     * Saniyede bir veya bilgi çubuğu yenilendikçe çağrılır.
     * Kernel düzeyindeki bayt farkını alır, oturum ve günlük/30 günlük kovalara ekler.
     */
    public synchronized void updateUsage() {
        int uid = Process.myUid();
        long currentRx = TrafficStats.getUidRxBytes(uid);
        if (currentRx == TrafficStats.UNSUPPORTED) {
            return;
        }

        if (lastKernelRxBytes < 0) {
            lastKernelRxBytes = currentRx;
            lastSpeedBytes = currentRx;
            lastSpeedCheckTimeMs = System.currentTimeMillis();
            return;
        }

        long delta = 0;
        if (currentRx >= lastKernelRxBytes) {
            delta = currentRx - lastKernelRxBytes;
        } else {
            // Cihaz yeniden başlatıldıysa veya sayaç taşması olduysa
            delta = currentRx;
        }
        lastKernelRxBytes = currentRx;

        if (delta > 0) {
            sessionRxBytes += delta;
            addBytesToToday(delta);
        }

        // Anlık Akış Hızı (Mbps) Hesabı
        long now = System.currentTimeMillis();
        long timeDiff = now - lastSpeedCheckTimeMs;
        if (timeDiff >= 1000) {
            long bytesInInterval = currentRx >= lastSpeedBytes ? (currentRx - lastSpeedBytes) : currentRx;
            // Bits per second -> Mbps
            double bits = bytesInInterval * 8.0;
            double seconds = timeDiff / 1000.0;
            currentSpeedMbps = (bits / seconds) / 1000000.0;

            lastSpeedBytes = currentRx;
            lastSpeedCheckTimeMs = now;
        }
    }

    /**
     * Bugünün tarihine ait kovanın üstüne ekler.
     */
    private synchronized void addBytesToToday(long bytes) {
        String today = dateFormat.format(new Date());
        try {
            String jsonStr = prefs.getString(KEY_DAILY_HISTORY, "{}");
            JSONObject json = new JSONObject(jsonStr);
            long currentDayBytes = json.optLong(today, 0L);
            json.put(today, currentDayBytes + bytes);
            prefs.edit().putString(KEY_DAILY_HISTORY, json.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 30 günden eski kayıtları JSON'dan temizler (Bellek ve disk tasarrufu).
     */
    public synchronized void cleanOldHistory() {
        try {
            String jsonStr = prefs.getString(KEY_DAILY_HISTORY, "{}");
            JSONObject json = new JSONObject(jsonStr);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -30);
            Date cutoffDate = cal.getTime();

            JSONObject cleanedJson = new JSONObject();
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String dateKey = keys.next();
                try {
                    Date d = dateFormat.parse(dateKey);
                    if (d != null && !d.before(cutoffDate)) {
                        cleanedJson.put(dateKey, json.getLong(dateKey));
                    }
                } catch (Exception ignored) {}
            }
            prefs.edit().putString(KEY_DAILY_HISTORY, cleanedJson.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- İSTATİSTİK GETTER'LARI ---

    public long getSessionBytes() {
        return sessionRxBytes;
    }

    public synchronized long getTodayBytes() {
        String today = dateFormat.format(new Date());
        try {
            String jsonStr = prefs.getString(KEY_DAILY_HISTORY, "{}");
            JSONObject json = new JSONObject(jsonStr);
            return json.optLong(today, 0L);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * Son 30 günün (Rolling 30 Days) toplam veri tüketimini döndürür.
     */
    public synchronized long getRolling30DaysBytes() {
        try {
            String jsonStr = prefs.getString(KEY_DAILY_HISTORY, "{}");
            JSONObject json = new JSONObject(jsonStr);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -30);
            Date cutoffDate = cal.getTime();

            long total = 0;
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String dateKey = keys.next();
                try {
                    Date d = dateFormat.parse(dateKey);
                    if (d != null && !d.before(cutoffDate)) {
                        total += json.getLong(dateKey);
                    }
                } catch (Exception ignored) {}
            }
            return total;
        } catch (Exception e) {
            return 0L;
        }
    }

    public double getCurrentSpeedMbps() {
        return currentSpeedMbps;
    }

    public synchronized void resetAllStats() {
        sessionRxBytes = 0;
        prefs.edit().putString(KEY_DAILY_HISTORY, "{}").apply();
        initKernelBytes();
    }

    // --- AĞ TÜRÜ VE KOTALILIK TESPİTİ (1-B) ---

    public boolean isNetworkMetered() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        return cm.isActiveNetworkMetered();
    }

    public String getNetworkTypeName() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return "Ağ Yok";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.net.Network activeNet = cm.getActiveNetwork();
            if (activeNet != null) {
                NetworkCapabilities caps = cm.getNetworkCapabilities(activeNet);
                if (caps != null) {
                    if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return "Ethernet";
                    if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return cm.isActiveNetworkMetered() ? "Kotalı Wi-Fi" : "Wi-Fi";
                    }
                    if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return "Mobil Veri";
                }
            }
        } else {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                int type = info.getType();
                if (type == ConnectivityManager.TYPE_ETHERNET) return "Ethernet";
                if (type == ConnectivityManager.TYPE_WIFI) {
                    return cm.isActiveNetworkMetered() ? "Kotalı Wi-Fi" : "Wi-Fi";
                }
                if (type == ConnectivityManager.TYPE_MOBILE) return "Mobil Veri";
            }
        }
        return "Bağlı";
    }

    public String getNetworkIcon() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return "❌";

        boolean metered = cm.isActiveNetworkMetered();
        String type = getNetworkTypeName();
        if (type.contains("Ethernet")) return "🌐";
        if (type.contains("Mobil")) return "📡";
        if (type.contains("Wi-Fi")) {
            return metered ? "⚠️" : "📶";
        }
        return metered ? "⚠️" : "📶";
    }

    // --- KOTALI AĞ POLİTİKASI (2-A) ---

    public int getMeteredPolicy() {
        return prefs.getInt(KEY_METERED_POLICY, POLICY_ALLOW);
    }

    public void setMeteredPolicy(int policy) {
        prefs.edit().putInt(KEY_METERED_POLICY, policy).apply();
    }

    public boolean isMeteredWarningDismissed() {
        return meteredWarningDismissed;
    }

    public void setMeteredWarningDismissed(boolean dismissed) {
        this.meteredWarningDismissed = dismissed;
    }

    // --- KOTA LİMİTLERİ (4-A+) ---

    public long getSessionQuotaBytes() {
        return prefs.getLong(KEY_SESSION_QUOTA, 0L);
    }

    public void setSessionQuotaBytes(long bytes) {
        prefs.edit().putLong(KEY_SESSION_QUOTA, bytes).apply();
        sessionQuotaDismissed = false;
    }

    public long getDailyQuotaBytes() {
        return prefs.getLong(KEY_DAILY_QUOTA, 0L);
    }

    public void setDailyQuotaBytes(long bytes) {
        prefs.edit().putLong(KEY_DAILY_QUOTA, bytes).apply();
        dailyQuotaDismissed = false;
    }

    public long getMonthlyQuotaBytes() {
        return prefs.getLong(KEY_MONTHLY_QUOTA, 0L);
    }

    public void setMonthlyQuotaBytes(long bytes) {
        prefs.edit().putLong(KEY_MONTHLY_QUOTA, bytes).apply();
        monthlyQuotaDismissed = false;
    }

    public void setSessionQuotaDismissed(boolean dismissed) {
        this.sessionQuotaDismissed = dismissed;
    }

    public void setDailyQuotaDismissed(boolean dismissed) {
        this.dailyQuotaDismissed = dismissed;
    }

    public void setMonthlyQuotaDismissed(boolean dismissed) {
        this.monthlyQuotaDismissed = dismissed;
    }

    /**
     * Kota aşımını kontrol eder.
     * Öncelik sırası: Oturum -> Günlük -> Son 30 Günlük
     */
    public int checkQuotaExceeded() {
        long sessionLimit = getSessionQuotaBytes();
        if (sessionLimit > 0 && !sessionQuotaDismissed && sessionRxBytes >= sessionLimit) {
            return QUOTA_SESSION_EXCEEDED;
        }

        long dailyLimit = getDailyQuotaBytes();
        if (dailyLimit > 0 && !dailyQuotaDismissed && getTodayBytes() >= dailyLimit) {
            return QUOTA_DAILY_EXCEEDED;
        }

        long monthlyLimit = getMonthlyQuotaBytes();
        if (monthlyLimit > 0 && !monthlyQuotaDismissed && getRolling30DaysBytes() >= monthlyLimit) {
            return QUOTA_MONTHLY_EXCEEDED;
        }

        return QUOTA_OK;
    }

    // --- FORMATLAMA YARDIMCILARI ---

    public static String formatBytes(long bytes) {
        if (bytes <= 0) return "0 MB";
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;

        if (gb >= 1.0) {
            return String.format(Locale.US, "%.2f GB", gb);
        } else if (mb >= 1.0) {
            return String.format(Locale.US, "%.1f MB", mb);
        } else {
            return String.format(Locale.US, "%.0f KB", kb);
        }
    }

    public static String formatSpeed(double mbps) {
        if (mbps < 0.05) return "0.0 Mbps";
        if (mbps < 1.0) {
            return String.format(Locale.US, "%.0f Kbps", mbps * 1000.0);
        } else {
            return String.format(Locale.US, "%.1f Mbps", mbps);
        }
    }
}
