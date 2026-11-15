# Functions

SMBSync2 is a tool for synchronizing files via wireless LAN using SMB1,SMB2 or SMB3 protocol between the internal storage of Android terminal, SDCARD/USB-OTG and PC/NAS. Synchronization is a one-way from the master to the target. Mirror, Move, Copy and Archive modes are supported. Many storage combinations are supported (Internal storage, SDCARD, USB-OTG, SMB, ZIP)
Sync can be automatically started by external applications (Tasker, AutoMagic etc) or SMBSync2 schedule. 
Sync occurs between two folder pairs called the Master (source folder) and the Target (destination folder). It is a one direction Sync, from the Master to the Target.

The supported Sync modes are:  
- Mirror  
  The target folder is kept as an exact copy of the master. If a file is different between the master and the target, the file on the master overwrites the file on the target. Folder and files not present on the target are copied from the master. Files and folders that do not exist on the master are also deleted from the target. Only modified files (by size and/or date/time) are updated on the target.  
  
- Move  
  If a file is different between the master and the target, the file on the master overwrites the file on the target. Once copied to the target, files and folders are deleted from the master (like move command).  
  Only modified files (by size and/or date/time) are copied to the target. Identical files, based on the selected compare criteria, are deleted from the master without being copied. Files and folders on the target, not present on the master, are obviously preserved.  
  
- Copy  
  Same as Move, but files are not deleted from the master after being copied.  
  If a file is different between the master and the target, the file on the master overwrites the file on the target. Once copied to the target, files and folders are kept on the master (like a copy command).  
  Only modified files (by size and/or date/time) are copied to the target. Identical files, based on the selected compare criteria, are ignored and not copied again.  
  
- Archive  
  Move photos and videos in the master's directory to the target if they were taken before 7 days or 30 days before the archive execution date. (However, you cannot use zip to target.)  
The following file types are eligible for archiving.  
"gif", "jpg", "jpeg", "jpe", "png", "mp4", "mov".  

**Compare criteria:** 
Files are considered different based on these criteria:  

1. File name exists only on master or target, not on both sides  
2. Files have different sizes  
3. Files have a different time stamp (last modification date and time)  

In Advanced Options, many compare settings can be adjusted.(Here is an example)  

- Time tolerance interval can be set to ignore difference if less than 1, 3, 5 or 10 sec for compatibility with FAT/exFAT medias. 
- Ignore Daylight Saving time is supported. 
- Option to not overwrite target file if it is newer than the master or if it is larger in size.   
# FAQs

Please refer to the link below.

[English](https://sentaroh.github.io/Documents/SMBSync2/SMBSync2_FAQ_EN.htm)
<br>
[日本語](https://sentaroh.github.io/Documents/SMBSync2/SMBSync2_FAQ_JA.htm)

# Documents

Please refer to the link below.

[English](https://sentaroh.github.io/Documents/SMBSync2/SMBSync2_Desc_EN.htm)
<br>
[日本語](https://sentaroh.github.io/Documents/SMBSync2/SMBSync2_Desc_JA.htm)

# Download  

[It is the same thing as the APK published on Google Play.](https://github.com/Sentaroh/SMBSync2/releases)

# How to Build  

1. Clone or download from Github.  
1. Open with AndroidStudio(Open an Existing Project).
1. Build(Build->Rebuild project).
1. Plase refer to "release/SMBSync2_debug.apk"

# License information

- This software includes the work that is distributed in the Apache License 2.0.  
  - [Zip4J 1.3.2](https://mvnrepository.com/artifact/net.lingala.zip4j/zip4j/1.3.2)  
  - [Metadata-extractor](https://github.com/drewnoakes/metadata-extractor)  
- This software includes the work that is distributed in the GNU Lesser General Public License v2.1.  
  - [jcifs-ng](https://github.com/AgNO3/jcifs-ng)  
  - [jcifs-1.3.17](https://jcifs.samba.org/)  
- This software includes the work that is distributed in the MOZILLA PUBLIC LICENSE 1.1.  
  - [juniversalchardet-1.0.3](https://code.google.com/archive/p/juniversalchardet/)  
- This software includes the work that is distributed in the Bouncy Castle LICENSE.
  - [Bouncy Castle Provider](https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on/1.58)
- This software includes the work that is distributed in the BSD 3-Clause "New" or "Revised" License
  - [markdownj](https://github.com/myabc/markdownj)

## Fork improvements (dev)

- The **Messages** tab displays plain-language causes for common SMB sync errors while preserving raw technical logs. Tap a message to inspect/copy it; long-press to copy, or use **Copy all logs**. [Debugging guide](docs/FORK_DEBUGGING.md)
- Sync task network options have contextual **?** explanation buttons for Wi-Fi requirements, IP address lists, and public IP restrictions.
- **Incremental photo backup (experimental):** for a **Copy** task from internal Android storage to a fixed SMB destination, optionally copy only files newer than the destination's latest modification time. An additional toggle checks already-existing older target files for changes. [Detailed behavior, limitations, and test cases](docs/INCREMENTAL_BACKUP.md)
- The **Android dev APK build** GitHub Action builds a debug APK from `dev` and makes it available as a workflow artifact when the build succeeds.
- The **Promote dev to prd** GitHub Action can be run manually to fast-forward `prd` safely; it never force-pushes or promotes automatically.

**Testing warning:** Incremental backup is intentionally opt-in and can skip older missing files. Keep regular full Copy backups until this feature has been tested with a disposable test folder and your own NAS. A debug APK's signature may not match a Play Store install, so export application settings before replacing an installed package.
