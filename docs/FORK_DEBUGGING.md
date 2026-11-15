# SMBSync2 fork: diagnosing sync errors

This fork keeps the original raw log messages, and adds a plain-language
**possible cause** above errors in the **Messages** tab.

## Reading errors

1. Run the sync task and open **Messages**.
2. An error now shows **Possible cause** and **Technical details**.
3. Tap any message for details and a **Copy message** button.
4. Long-press a message to copy it immediately.
5. Tap **Copy all logs** to copy the complete visible Messages history,
   with dates, times, task names, paths and raw technical messages.

The descriptions are hints based on common SMB/Android error codes.
They are not definitive remote-server diagnoses. When seeking help, include
the unmodified technical details; remove private addresses and file paths
before sharing publicly. Copying Messages does not include Android logcat
or every file from the separate debug log manager.

## Branch workflow

- `2.55`: original default branch; only the GitHub Actions entrypoint
  was added here so the **Run workflow** button is available.
- `dev`: development branch for application changes.
- `prd`: production branch; never changed automatically.

To promote a tested version:

1. Open **Actions** > **Promote dev to prd**.
2. Select **Run workflow** and enter `PROMOTE`.
3. The action checks that `prd` is an ancestor of `dev`.
4. If safe, it fast-forwards `prd` to `dev`, without force-pushing.
   If the branches diverged, it fails rather than overwriting changes.

No release APK is published automatically. The promotion workflow moves
Git commits only; it does not build or install an app.
