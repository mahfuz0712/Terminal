package com.mux.android.terminal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TABS = listOf("TERM", "SAVED", "SET", "SYS")

@Composable
fun TerminalRoot() {
    val ctx = LocalContext.current
    MuxTheme(if (Prefs.amber) AmberPalette else CyanPalette) {
        val c = Hud.colors
        var tab by rememberSaveable { mutableIntStateOf(0) }
        var update by remember { mutableStateOf<UpdateInfo?>(null) }
        val imeUp = WindowInsets.ime.getBottom(LocalDensity.current) > 0

        LaunchedEffect(Unit) { if (isOnline(ctx)) update = checkUpdate(ctx).second }

        update?.let { u ->
            UpdateDialog(
                u, appVersion(ctx),
                onUpdate = { Util.open(ctx, u.apkUrl); update = null },
                onCancel = { update = null }
            )
        }

        HudBackground(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().safeDrawingPadding().padding(8.dp)) {
                if (!(imeUp && tab == 0)) {
                    Column(Modifier.fillMaxWidth().height(40.dp)) {
                        HudText("◈ TERMINAL", color = c.primary, size = 16.sp, bold = true)
                        HudText(TABS[tab], color = c.dim, size = 10.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                }
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    when (tab) {
                        0 -> TermScreen()
                        1 -> SnippetsScreen { cmd -> Sessions.active.runCommand(cmd); tab = 0 }
                        2 -> SettingsScreen()
                        else -> SystemScreen(false) { update = it }
                    }
                }
                if (!(imeUp && tab == 0)) {
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth().height(46.dp)) {
                        TABS.forEachIndexed { i, t ->
                            HudButton(
                                label = t,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                kind = KeyKind.OP,
                                selected = i == tab,
                                fontSize = 11.sp
                            ) { tab = i }
                        }
                    }
                }
            }
        }
    }
}
