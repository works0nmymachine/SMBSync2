# Incremental Copy — newest SMB target file (experimental)

This feature is **opt-in**. It currently supports only **Copy** tasks from
Android **internal storage** to a fixed SMB target folder. It cannot be used
with Mirror, Move, Archive, SD card, ZIP, or target-folder templates that
change by date. The execution engine rejects unsupported tasks rather than
silently skipping files or applying unintended deletions.

## What it does

1. Scan the entire destination folder recursively on the NAS **once** to find
   the newest target **last-modified timestamp**. No destination files are
   changed by this scan.
2. Compare each candidate source file to this watermark using the best
   available original date: EXIF `DateTimeOriginal` for supported photos,
   a recognized date/time camera filename (for example
   `IMG_20261115_081500.jpg`), or the source file's last-modified timestamp
   otherwise. A 2-second tolerance handles timestamp rounding.
3. If a source file is **newer** than the watermark, run normal Copy behavior:
   copy missing files and update changed files according to the task's existing
   overwrite/change-detection settings.
4. If a source file is **older** than the watermark:
   - **Older changes disabled:** skip without querying that target file.
   - **Older changes enabled:** query the matching target path and check for
     changes **only if the target file already exists**; do **not** backfill
     older files missing from the target.
5. If no target files are found, run the normal first complete Copy.

The cutoff is a **snapshot** from the start of the sync; copying one file does
not move the cutoff during that run. The app logs the cutoff timestamp.

## Important limitations

- This is an *optimization*, not a complete backup. It intentionally skips
  older source files missing from the destination, including gaps in an
  earlier failed backup. Run normal Copy periodically to reconcile gaps.
- An old file edited on the NAS can have a very recent modification timestamp
  and move the cutoff forward, causing otherwise-new source files to be skipped.
- EXIF data is often missing or inaccurate after editing. Video and other
  files may depend on filename/last-modified date; there is no guaranteed
  original creation date on arbitrary Android filesystems.
- `Include older changes` honors the normal **overwrite existing files**
  and **detect changes by date/size** task settings. If overwriting is turned
  off, older changed files are *not* replaced.
- A full destination scan is still necessary at the beginning, and local
  source folders are traversed. Enabling older-change checks adds more SMB
  lookups. A large NAS or very deep folder structure may perform worse.
- This feature is limited to a folder with stable file paths (no date-based
  target remapping). Never enable on a folder you do not control.

## Cases to verify before relying on it

| Target | Source | Older changes | Expected |
|---|---|---|---|
| Empty | Any files | Off or on | First normal full Copy |
| Has latest 10 Nov | New photo taken 12 Nov | Off or on | Copy |
| Has latest 10 Nov | Old photo from 3 Nov, missing on target | Off or on | Skip |
| Has latest 10 Nov | Photo from 3 Nov, edited 12 Nov, already on target | Off | Skip |
| Has latest 10 Nov | Photo from 3 Nov, edited 12 Nov, already on target | On | Re-evaluate normal overwrite rules |
| Has latest 10 Nov | Photo from 3 Nov, no EXIF and unrecognized filename, modified 12 Nov | Off | Treated as recent (fallback limitation) |
| Target unreadable | Any file | Either | Error; do not perform incremental Copy |
| Mirror or Move | Any file | Either | Refuse task if the incremental flag remains enabled |

To debug behavior, use **Messages** > **Copy all logs**. Never send logs
containing internal network addresses or personal file paths publicly.
