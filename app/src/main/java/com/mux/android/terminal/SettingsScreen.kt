package com.mux.android.terminal

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private fun hasAllFiles(): Boolean =
    if (Build.VERSION.SDK_INT >= 30) Environment.isExternalStorageManager() else true

@Composable
fun SettingsScreen() {
    val c = Hud.colors
    val ctx = LocalContext.current
    val legacy = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        HudPanel(Modifier.fillMaxWidth(), "APPEARANCE") {
            SettingRow("COLOUR", "cyan or amber HUD", if (Prefs.amber) "AMBER" else "CYAN") {
                Prefs.amber = !Prefs.amber; Prefs.commit()
            }
            val sizes = listOf(10, 11, 12, 13, 14, 16, 18, 20)
            SettingRow("TEXT SIZE", "output and input text", "${Prefs.fontSize} SP") {
                val i = sizes.indexOf(Prefs.fontSize)
                Prefs.fontSize = sizes[(i + 1) % sizes.size]; Prefs.commit()
            }
            SettingRow("EXTRA KEYS", "TAB, arrows, ^C and symbols row", if (Prefs.extraKeys) "ON" else "OFF") {
                Prefs.extraKeys = !Prefs.extraKeys; Prefs.commit()
            }
        }
        Spacer(Modifier.height(8.dp))
        HudPanel(Modifier.fillMaxWidth(), "BEHAVIOUR") {
            SettingRow("KEEP SCREEN ON", "while the app is open", if (Prefs.keepAwake) "ON" else "OFF") {
                Prefs.keepAwake = !Prefs.keepAwake; Prefs.commit()
            }
            SettingRow("NEW TAB STARTS IN", "folder for new tabs", if (Prefs.startSd) "SDCARD" else "HOME") {
                Prefs.startSd = !Prefs.startSd; Prefs.commit()
            }
            val lim = listOf(1000, 3000, 10000)
            SettingRow("SCROLLBACK", "lines kept per tab", "${Prefs.maxLines}") {
                Prefs.maxLines = lim[(lim.indexOf(Prefs.maxLines) + 1) % lim.size]; Prefs.commit()
            }
        }
        Spacer(Modifier.height(8.dp))
        HudPanel(Modifier.fillMaxWidth(), "STORAGE ACCESS") {
            HudText(
                "HOME is the app's private folder. To use /sdcard (cd sdcard) the phone must allow all-files access.",
                color = c.text, size = 12.sp
            )
            HudText(
                if (hasAllFiles()) "ALL FILES ACCESS: GRANTED" else "ALL FILES ACCESS: NOT GRANTED",
                color = if (hasAllFiles()) c.good else c.accent, size = 11.sp, bold = true
            )
            Spacer(Modifier.height(6.dp))
            HudButton("GRANT ALL FILES ACCESS", Modifier.fillMaxWidth().height(44.dp), KeyKind.ACCENT, fontSize = 12.sp) {
                if (Build.VERSION.SDK_INT >= 30) {
                    try {
                        ctx.startActivity(
                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:${ctx.packageName}"))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    } catch (e: Exception) {
                        try {
                            ctx.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        } catch (e2: Exception) {
                            Util.openAppSettings(ctx)
                        }
                    }
                } else {
                    legacy.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        HudPanel(Modifier.fillMaxWidth(), "DATA") {
            SettingRow("COMMAND HISTORY", "${Prefs.history.size} saved", "CLEAR", KeyKind.DANGER) {
                Prefs.clearHistory(); Util.toast(ctx, "HISTORY CLEARED")
            }
            SettingRow("ALIASES", "${Prefs.aliases.size} saved (alias, unalias)", "RESET", KeyKind.DANGER) {
                Prefs.aliases = mapOf("ll" to "ls -la", "la" to "ls -a", ".." to "cd ..", "sdcard" to "cd /storage/emulated/0")
                Prefs.commit(); Util.toast(ctx, "ALIASES RESET")
            }
            SettingRow("SAVED VARIABLES", "${Prefs.userEnv.size} saved (export)", "CLEAR", KeyKind.DANGER) {
                Prefs.userEnv = emptyMap(); Prefs.commit(); Util.toast(ctx, "VARIABLES CLEARED")
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
