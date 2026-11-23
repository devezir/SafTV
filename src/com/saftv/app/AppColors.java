package com.saftv.app;

import android.graphics.Color;

/**
 * SafTV Uygulama Genel Renk Sabitleri
 * Tekrarlayan Color.parseColor çağrılarını ve hex sabitlerini tek merkezde toplar (DRY).
 */
public final class AppColors {
    private AppColors() {}

    // Durum ve Vurgu Renkleri
    public static final int ACTIVE_GREEN = Color.parseColor("#34D399");      // Canlı / Aktif / Seçili yeşil
    public static final int EMERALD_GREEN = 0xFF10B981;                      // Standart zümrüt yeşili
    public static final int ACCENT_BLUE = 0xFF3B82F6;                        // Oynatılıyor mavi
    public static final int ACCENT_BLUE_LIGHT = Color.parseColor("#58A6FF");  // Açık mavi vurgu
    public static final int ACCENT_PURPLE = Color.parseColor("#A371F7");      // Mor ağ vurgusu
    public static final int AMBER_GOLD = Color.parseColor("#FBBF24");       // Taşıma / Uyarı altın sarısı
    public static final int WARNING_AMBER = Color.parseColor("#F59E0B");    // Sadece ses / Uyarı sarısı
    public static final int ERROR_RED = Color.parseColor("#EF4444");        // Hata / Kapalı kırmızı
    public static final int ERROR_RED_ALT = Color.parseColor("#F85149");    // Blok kırmızı

    // Nötr ve Arka Plan Renkleri
    public static final int TEXT_WHITE = 0xFFFFFFFF;
    public static final int TEXT_MUTED = Color.parseColor("#8B949E");       // Soluk gri
    public static final int TEXT_GRAY = Color.parseColor("#64748B");        // Kapalı kanal gri
    public static final int BG_DARK = 0xFF0F172A;                           // Koyu arka plan
    public static final int BG_CARD = Color.parseColor("#1E293B");          // Kart arka planı
    public static final int MOVING_BG = 0xFF78350F;                         // Kanal taşıma koyu amber
}
