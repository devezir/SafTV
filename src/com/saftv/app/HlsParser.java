package com.saftv.app;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HlsParser {

    public interface QualityCallback {
        void onResolved(String targetUrl, String resolvedQuality);
    }

    public static class StreamTrack {
        public int bandwidth;
        public int height; // e.g. 1080, 720, 480, 360
        public String url;

        public StreamTrack(int bandwidth, int height, String url) {
            this.bandwidth = bandwidth;
            this.height = height;
            this.url = url;
        }
    }

    public static void resolveQualityUrl(final String masterUrl,
                                         final String qualityPref, // "auto", "1080p", "720p", "480p", "360p", "audio_only"
                                         final Map<String, String> headers,
                                         final QualityCallback callback) {

        final Handler mainHandler = new Handler(Looper.getMainLooper());

        if (qualityPref == null || qualityPref.equalsIgnoreCase("auto") || qualityPref.equalsIgnoreCase("otomatik")) {
            callback.onResolved(masterUrl, "Otomatik");
            return;
        }

        final boolean isAudioOnly = "audio_only".equalsIgnoreCase(qualityPref)
                || "audio".equalsIgnoreCase(qualityPref)
                || "ses".equalsIgnoreCase(qualityPref);

        final int targetHeight;
        if (isAudioOnly) {
            targetHeight = 0;
        } else if (qualityPref.contains("1080")) {
            targetHeight = 1080;
        } else if (qualityPref.contains("720")) {
            targetHeight = 720;
        } else if (qualityPref.contains("480")) {
            targetHeight = 480;
        } else if (qualityPref.contains("360")) {
            targetHeight = 360;
        } else {
            callback.onResolved(masterUrl, "Otomatik");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try {
                    URL u = new URL(masterUrl);
                    conn = (HttpURLConnection) u.openConnection();
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                    if (headers != null) {
                        for (Map.Entry<String, String> entry : headers.entrySet()) {
                            conn.setRequestProperty(entry.getKey(), entry.getValue());
                        }
                    }

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    List<StreamTrack> tracks = new ArrayList<>();
                    List<String> audioTrackUris = new ArrayList<>();
                    int curBandwidth = 0;
                    int curHeight = 0;

                    Pattern resPattern = Pattern.compile("RESOLUTION=\\d+x(\\d+)");
                    Pattern bwPattern = Pattern.compile("BANDWIDTH=(\\d+)");
                    Pattern audioUriPattern = Pattern.compile("TYPE=AUDIO.*?URI=[\"']([^\"']+)[\"']");

                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("#EXT-X-MEDIA") && line.contains("TYPE=AUDIO")) {
                            Matcher mAudio = audioUriPattern.matcher(line);
                            if (mAudio.find()) {
                                String aUri = mAudio.group(1);
                                if (!aUri.startsWith("http://") && !aUri.startsWith("https://")) {
                                    String baseUrl = masterUrl.substring(0, masterUrl.lastIndexOf('/') + 1);
                                    aUri = baseUrl + aUri;
                                }
                                audioTrackUris.add(aUri);
                            }
                        } else if (line.startsWith("#EXT-X-STREAM-INF")) {
                            Matcher mRes = resPattern.matcher(line);
                            if (mRes.find()) {
                                curHeight = Integer.parseInt(mRes.group(1));
                            }
                            Matcher mBw = bwPattern.matcher(line);
                            if (mBw.find()) {
                                curBandwidth = Integer.parseInt(mBw.group(1));
                            }
                        } else if (line.startsWith("http://") || line.startsWith("https://")) {
                            if (curHeight > 0 || curBandwidth > 0) {
                                tracks.add(new StreamTrack(curBandwidth, curHeight, line));
                            }
                            curHeight = 0;
                            curBandwidth = 0;
                        } else if (!line.startsWith("#") && !line.isEmpty()) {
                            // Relative URL
                            String baseUrl = masterUrl.substring(0, masterUrl.lastIndexOf('/') + 1);
                            String fullUrl = baseUrl + line;
                            if (curHeight > 0 || curBandwidth > 0) {
                                tracks.add(new StreamTrack(curBandwidth, curHeight, fullUrl));
                            }
                            curHeight = 0;
                            curBandwidth = 0;
                        }
                    }

                    if (isAudioOnly) {
                        if (!audioTrackUris.isEmpty()) {
                            postResult(mainHandler, callback, audioTrackUris.get(0), "Sadece Ses");
                            return;
                        }
                        if (!tracks.isEmpty()) {
                            // En düşük bant genişliğine / çözünürlüğe sahip akışı seç
                            StreamTrack minTrack = tracks.get(0);
                            for (StreamTrack t : tracks) {
                                int currentScore = t.bandwidth > 0 ? t.bandwidth : (t.height > 0 ? t.height * 1000 : Integer.MAX_VALUE);
                                int minScore = minTrack.bandwidth > 0 ? minTrack.bandwidth : (minTrack.height > 0 ? minTrack.height * 1000 : Integer.MAX_VALUE);
                                if (currentScore < minScore) {
                                    minTrack = t;
                                }
                            }
                            postResult(mainHandler, callback, minTrack.url, "Sadece Ses");
                            return;
                        }
                        postResult(mainHandler, callback, masterUrl, "Sadece Ses");
                        return;
                    }

                    if (tracks.isEmpty()) {
                        postResult(mainHandler, callback, masterUrl, "Otomatik");
                        return;
                    }

                    // En yakın yüksekliği bul
                    StreamTrack bestMatch = null;
                    int minDiff = Integer.MAX_VALUE;
                    for (StreamTrack t : tracks) {
                        int diff = Math.abs(t.height - targetHeight);
                        if (diff < minDiff) {
                            minDiff = diff;
                            bestMatch = t;
                        }
                    }

                    if (bestMatch != null) {
                        String label = (bestMatch.height > 0) ? (bestMatch.height + "p") : qualityPref;
                        postResult(mainHandler, callback, bestMatch.url, label);
                    } else {
                        postResult(mainHandler, callback, masterUrl, "Otomatik");
                    }

                } catch (Exception e) {
                    // Ayrıştırma başarısız olursa master URL ile devam et
                    postResult(mainHandler, callback, masterUrl, isAudioOnly ? "Sadece Ses" : "Otomatik");
                } finally {
                    if (reader != null) {
                        try { reader.close(); } catch (Exception ignored) {}
                    }
                    if (conn != null) {
                        try { conn.disconnect(); } catch (Exception ignored) {}
                    }
                }
            }
        }).start();
    }

    private static void postResult(Handler handler, final QualityCallback callback, final String url, final String label) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResolved(url, label);
            }
        });
    }
}
