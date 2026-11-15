package com.sentaroh.android.SMBSync2;

import android.media.ExifInterface;

import com.sentaroh.jcifs.JcifsException;
import com.sentaroh.jcifs.JcifsFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Optional local-to-SMB copy cutoff. Never deletes or moves any files.
 *
 * A target watermark uses the largest last-modified time found on the SMB
 * destination, including subdirectories. Dates on the source use EXIF
 * DateTimeOriginal when present, a camera filename when recognizable, or
 * lastModified() otherwise. Neither approach proves a file's creation date.
 */
final class IncrementalSmbCopy {
    private static final int MAX_DIRECTORY_DEPTH = 64;
    private static final long TIMESTAMP_ROUNDING_MARGIN_MS = 2000L;

    private static final Pattern CAMERA_FILENAME =
            Pattern.compile("^(?:IMG|VID|PXL|MVIMG|IMG_E|DSC)_?(\\d{8})[_-](\\d{6}).*",
                    Pattern.CASE_INSENSITIVE);

    private IncrementalSmbCopy() {}

    static long newestTargetTime(JcifsFile folder) throws IOException, JcifsException {
        if (!folder.exists()) return 0L; // Empty/new destination: initial full copy.
        if (!folder.isDirectory()) throw new IOException("SMB destination is not a directory");
        return maxLastModified(folder, 0);
    }

    private static long maxLastModified(JcifsFile folder, int depth)
            throws IOException, JcifsException {
        if (depth > MAX_DIRECTORY_DEPTH)
            throw new IOException("SMB destination contains directories nested too deeply");

        JcifsFile[] children = folder.listFiles();
        if (children == null)
            throw new IOException("SMB destination listing returned null: " + folder.getPath());

        long newest = 0L;
        for (JcifsFile child : children) {
            if (child.isDirectory()) {
                long nested = maxLastModified(child, depth + 1);
                if (nested > newest) newest = nested;
            } else if (child.isFile()) {
                long time = child.getLastModified();
                if (time > newest) newest = time;
            }
        }
        return newest;
    }

    static boolean isOlderThanTarget(File file, long watermark) {
        if (watermark <= 0) return false;
        long date = originalDateOrModifiedTime(file);
        return date > 0 && date < watermark - TIMESTAMP_ROUNDING_MARGIN_MS;
    }

    static long originalDateOrModifiedTime(File file) {
        String lower = file.getName().toLowerCase(Locale.ROOT);
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".heic") || lower.endsWith(".heif")) {
            try {
                ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                String original = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
                long timestamp = parseDate(original, "yyyy:MM:dd HH:mm:ss");
                if (timestamp > 0) return timestamp;
            } catch (Exception ignored) {
                // A missing/unreadable EXIF date should not stop ordinary sync.
            }
        }

        Matcher matcher = CAMERA_FILENAME.matcher(file.getName());
        if (matcher.matches()) {
            long timestamp = parseDate(matcher.group(1) + matcher.group(2), "yyyyMMddHHmmss");
            if (timestamp > 0) return timestamp;
        }
        return file.lastModified();
    }

    private static long parseDate(String value, String pattern) {
        if (value == null || value.isEmpty()) return 0L;
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
            format.setLenient(false);
            return format.parse(value).getTime();
        } catch (ParseException ignored) {
            return 0L;
        }
    }
}
