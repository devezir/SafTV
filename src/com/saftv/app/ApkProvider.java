package com.saftv.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Ultra-lightweight native ContentProvider for secure APK updates without AndroidX.
 * Supplies the downloaded APK to Android's PackageInstaller via content:// URI.
 */
public class ApkProvider extends ContentProvider {
    public static final String AUTHORITY = "com.saftv.app.apkprovider";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return "application/vnd.android.package-archive";
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (getContext() == null) {
            throw new FileNotFoundException("Context null");
        }
        String fileName = uri.getLastPathSegment();
        if (fileName == null || fileName.contains("..") || fileName.contains("/")) {
            throw new FileNotFoundException("Geçersiz dosya adı");
        }

        File updateDir = new File(getContext().getCacheDir(), "updates");
        File apkFile = new File(updateDir, fileName);

        if (!apkFile.exists()) {
            throw new FileNotFoundException("APK dosyası bulunamadı: " + apkFile.getAbsolutePath());
        }

        return ParcelFileDescriptor.open(apkFile, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (getContext() == null) return null;
        String fileName = uri.getLastPathSegment();
        if (fileName == null) return null;

        File updateDir = new File(getContext().getCacheDir(), "updates");
        File file = new File(updateDir, fileName);

        String[] cols = projection != null ? projection : new String[] {
            OpenableColumns.DISPLAY_NAME,
            OpenableColumns.SIZE
        };

        MatrixCursor cursor = new MatrixCursor(cols);
        if (file.exists()) {
            MatrixCursor.RowBuilder row = cursor.newRow();
            for (String col : cols) {
                if (OpenableColumns.DISPLAY_NAME.equals(col)) {
                    row.add(file.getName());
                } else if (OpenableColumns.SIZE.equals(col)) {
                    row.add(file.length());
                } else {
                    row.add(null);
                }
            }
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
