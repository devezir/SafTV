package com.saftv.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Saf TV - EPG Program Veri Modeli
 * Belirli bir kanala ait tek bir program/yayın kaydını ve canlı zaman hesaplarını tutar.
 */
public class EpgProgram {
    private final String channelId;
    private final long startMs;
    private final long stopMs;
    private final String title;
    private final String description;

    private static final SimpleDateFormat TIME_FMT = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public EpgProgram(String channelId, long startMs, long stopMs, String title, String description) {
        this.channelId = channelId != null ? channelId : "";
        this.startMs = startMs;
        this.stopMs = stopMs;
        this.title = title != null ? title.trim() : "";
        this.description = description != null ? description.trim() : "";
    }

    public String getChannelId() {
        return channelId;
    }

    public long getStartMs() {
        return startMs;
    }

    public long getStopMs() {
        return stopMs;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCurrentlyAir(long nowMs) {
        return nowMs >= startMs && nowMs < stopMs;
    }

    public boolean isCurrentlyAir() {
        return isCurrentlyAir(System.currentTimeMillis());
    }

    public boolean isCurrentlyAiring() {
        return isCurrentlyAir(System.currentTimeMillis());
    }

    public boolean isFuture(long nowMs) {
        return startMs > nowMs;
    }

    public boolean isFuture() {
        return isFuture(System.currentTimeMillis());
    }

    public int getProgressPercentage(long nowMs) {
        if (nowMs < startMs) return 0;
        if (nowMs >= stopMs) return 100;
        long total = stopMs - startMs;
        if (total <= 0) return 0;
        return (int) Math.min(100, Math.max(0, ((nowMs - startMs) * 100) / total));
    }

    public int getProgressPercentage() {
        return getProgressPercentage(System.currentTimeMillis());
    }

    public int getRemainingMinutes(long nowMs) {
        if (nowMs >= stopMs) return 0;
        long diff = stopMs - nowMs;
        return (int) Math.max(0, diff / 60000L);
    }

    public int getRemainingMinutes() {
        return getRemainingMinutes(System.currentTimeMillis());
    }

    public synchronized String getFormattedTimeRange() {
        return TIME_FMT.format(new Date(startMs)) + " - " + TIME_FMT.format(new Date(stopMs));
    }

    public String getTimeRangeStr() {
        return getFormattedTimeRange();
    }

    public synchronized String getFormattedStartTime() {
        return TIME_FMT.format(new Date(startMs));
    }

    public String getStartTimeStr() {
        return getFormattedStartTime();
    }

    public synchronized String getFormattedStopTime() {
        return TIME_FMT.format(new Date(stopMs));
    }

    public String getStopTimeStr() {
        return getFormattedStopTime();
    }

    @Override
    public String toString() {
        return getFormattedTimeRange() + " | " + title;
    }
}
