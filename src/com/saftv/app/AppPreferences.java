package com.saftv.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SafTV Uygulama Ayarları Yöneticisi
 * SharedPreferences okuma ve yazma işlemlerini tek merkezde toplayarak kod tekrarını önler (DRY).
 */
public class AppPreferences {
    public static final String PREFS_NAME = "saftv_prefs";

    public static final String KEY_LAST_CHANNEL = "last_channel_index";
    public static final String KEY_ASPECT_RATIO = "aspect_ratio_mode"; // 0: Stretch, 1: Fit
    public static final String KEY_QUALITY = "stream_quality_pref";     // auto, 1080p, 720p, 480p, 360p, audio_only
    public static final String KEY_UI_SCALE = "pref_ui_scale";          // auto, 60, 80, 100, 125, 150, 175, 200
    public static final String KEY_OSD_TIMEOUT = "pref_osd_timeout";    // 2, 3, 5, 10, 0
    public static final String KEY_BG_AUDIO = "pref_bg_audio";          // boolean

    private static AppPreferences instance;
    private final SharedPreferences prefs;

    private AppPreferences(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AppPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreferences(context);
        }
        return instance;
    }

    public SharedPreferences getRawPreferences() {
        return prefs;
    }

    // --- Son Kanal ---
    public int getLastChannelIndex() {
        return prefs.getInt(KEY_LAST_CHANNEL, 0);
    }

    public void setLastChannelIndex(int index) {
        prefs.edit().putInt(KEY_LAST_CHANNEL, index).apply();
    }

    // --- En/Boy Oranı (Aspect Ratio) ---
    public int getAspectRatioMode() {
        return prefs.getInt(KEY_ASPECT_RATIO, 0);
    }

    public void setAspectRatioMode(int mode) {
        prefs.edit().putInt(KEY_ASPECT_RATIO, mode).apply();
    }

    // --- Yayın Kalitesi ---
    public String getStreamQuality() {
        return prefs.getString(KEY_QUALITY, "auto");
    }

    public void setStreamQuality(String quality) {
        prefs.edit().putString(KEY_QUALITY, quality).apply();
    }

    // --- Arayüz Ölçeği (UI Scale) ---
    public String getUiScale() {
        return prefs.getString(KEY_UI_SCALE, "auto");
    }

    public void setUiScale(String scale) {
        prefs.edit().putString(KEY_UI_SCALE, scale).apply();
    }

    // --- OSD Kapanma Süresi ---
    public String getOsdTimeout() {
        return prefs.getString(KEY_OSD_TIMEOUT, "3");
    }

    public void setOsdTimeout(String timeout) {
        prefs.edit().putString(KEY_OSD_TIMEOUT, timeout).apply();
    }

    public int getOsdTimeoutMs() {
        try {
            int sec = Integer.parseInt(getOsdTimeout());
            return sec * 1000;
        } catch (Exception e) {
            return 3000;
        }
    }

    // --- Arka Planda Ses Oynatma ---
    public boolean isBackgroundAudioEnabled() {
        return prefs.getBoolean(KEY_BG_AUDIO, false);
    }

    public void setBackgroundAudioEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_BG_AUDIO, enabled).apply();
    }
}
