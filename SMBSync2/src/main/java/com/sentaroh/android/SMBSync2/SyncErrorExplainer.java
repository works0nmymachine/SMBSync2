package com.sentaroh.android.SMBSync2;

import android.content.Context;

import java.util.Locale;

/**
 * Explains common sync/SMB errors without replacing the raw diagnostic message.
 * These are possible causes, not a claim that a specific remote host was inspected.
 */
public final class SyncErrorExplainer {
    private SyncErrorExplainer() {}

    public static String describe(Context context, String rawError) {
        if (rawError == null || rawError.trim().isEmpty()) {
            return context.getString(R.string.fork_error_unknown);
        }
        String error = rawError.toLowerCase(Locale.ROOT);

        // Authentication errors must be checked before the broader access-denied family.
        if (has(error, "nt_status_logon_failure", "status_logon_failure",
                "0xc000006d", "0xc000006a", "logon failure",
                "authentication failed", "invalid credentials", "wrong password",
                "nt_status_account_disabled", "0xc0000072")) {
            return context.getString(R.string.fork_error_authentication);
        }
        if (has(error, "nt_status_access_denied", "status_access_denied",
                "0xc0000022", "access denied", "permission denied",
                "securityexception", "permission failure")) {
            return context.getString(R.string.fork_error_permission);
        }
        if (has(error, "nt_status_bad_network_name", "0xc00000cc",
                "nt_status_object_path_not_found", "0xc000003a",
                "nt_status_object_name_not_found", "0xc0000034",
                "no such file", "path not found", "file not found")) {
            return context.getString(R.string.fork_error_path);
        }
        if (has(error, "no space left", "disk full", "nt_status_disk_full",
                "0xc000007f", "enospc")) {
            return context.getString(R.string.fork_error_storage);
        }
        if (has(error, "nt_status_sharing_violation", "0xc0000043",
                "file is being used", "file is locked")) {
            return context.getString(R.string.fork_error_locked);
        }
        if (has(error, "connection refused", "econnrefused")) {
            return context.getString(R.string.fork_error_refused);
        }
        if (has(error, "unknownhostexception", "unknown host",
                "no route to host", "network is unreachable",
                "host unreachable", "enetunreach", "ehostunreach",
                "name or service not known")) {
            return context.getString(R.string.fork_error_network);
        }
        if (has(error, "timed out", "timeout", "etimedout",
                "nt_status_io_timeout", "0xc00000b5")) {
            return context.getString(R.string.fork_error_timeout);
        }
        if (has(error, "unsupported dialect", "protocol not supported",
                "nt_status_not_supported", "0xc00000bb",
                "smb signing", "negotiation failed", "no compatible protocols")) {
            return context.getString(R.string.fork_error_protocol);
        }
        return context.getString(R.string.fork_error_unknown);
    }

    private static boolean has(String text, String... patterns) {
        for (String pattern : patterns) {
            if (text.contains(pattern)) return true;
        }
        return false;
    }
}
