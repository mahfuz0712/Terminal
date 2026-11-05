# Terminal

Sci-fi HUD (Jarvis style) terminal for Android 8.0+. Same design language as Dialer, Scanner, Keyboard, Launcher, Messages and Weather.

- Package: `com.mux.android.terminal` - Version: `1.0.0`

## Features
- Real shell: every command runs in Android's `/system/bin/sh` with the phone's toolbox (ls, cat, grep, sed, ps, ping, df, du, find, getprop, ...). Pipes, redirects, `&&`, `;`, `*`, `$VAR` work
- Tabs: several sessions (T1, T2, ...), long-press a tab to close it, `+` for a new one
- `cd` works (also `cd -`, `cd ~`, `cd sdcard`). Prompt shows the current folder
- Colours: ANSI colours (8, 16, 256 and true colour) are shown; progress lines with carriage return update in place
- Input: history (up / down keys), TAB completion (files, folders, commands, aliases), extra key row (TAB, arrows, ^C, ^D, ^L and symbols `/ ~ - _ . | & > < * $ " ' \ ; =`)
- Running commands: type while a command runs to send text to its stdin, `^C` stops it (also stops child processes), `^D` ends input
- Built in: `help`, `clear`, `history [-c]`, `alias`, `unalias`, `export NAME=value` (saved for all later commands), `unset`, `exit`
- SAVED tab: keep your own one-tap commands (some are pre-filled: model, kernel, disk, ping, ...)
- Output tools: COPY, SHARE, CLEAR, jump to end, select text by long-press
- SET tab: cyan / amber, text size, extra keys on/off, keep screen on, start folder (home / sdcard), scrollback, all-files access, clear history / aliases / variables
- SYS tab: app info, update check, author

## Limits (Android, no root)
- There is no PTY, so full-screen programs (vi, nano, htop, less) cannot run. `top -n 1`, `cat`, `head`, `tail` etc. work.
- This is not Termux: no package manager, and Android 10+ does not let apps run downloaded binaries. Shell scripts run with `sh script.sh`.
- Commands run as this app, not as root. HOME is the app's private folder. For `/sdcard` turn on all-files access (SET tab).
- A running command lives only while the app is alive; Android may stop it when the app is in the background for long.

## Build
`gradle assembleDebug` (Gradle 8.9, JDK 17) or the GitHub Action. `app/debug.keystore` is shared with the other apps.
Updates are read from GitHub releases of `mahfuz0712/Terminal` (edit `AppInfo.kt`).
