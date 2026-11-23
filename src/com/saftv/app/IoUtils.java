package com.saftv.app;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * SafTV Giriş/Çıkış Yardımcı Sınıfı
 * Tekrarlayan Stream/BufferedReader okuma döngülerini ve kapatma işlemlerini yönetir (DRY).
 */
public final class IoUtils {
    private IoUtils() {}

    /**
     * Verilen InputStream'i UTF-8 olarak okuyup String olarak döndürür ve akışı kapatır.
     */
    public static String readStreamToString(InputStream is) {
        if (is == null) return "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        } finally {
            closeQuietly(reader);
            closeQuietly(is);
        }
    }

    /**
     * Belirtilen dosyayı okuyup String olarak döndürür.
     */
    public static String readFileToString(File file) {
        if (file == null || !file.exists() || file.length() == 0) return "";
        try {
            return readStreamToString(new FileInputStream(file));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Akışı sessizce ve güvenle kapatır.
     */
    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception ignored) {}
        }
    }
}
