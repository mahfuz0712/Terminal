package com.mux.android.terminal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** SAVED tab: one-tap commands. */
@Composable
fun SnippetsScreen(onRun: (String) -> Unit) {
    val c = Hud.colors
    var name by remember { mutableStateOf("") }
    var cmd by remember { mutableStateOf("") }
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            HudPanel(Modifier.fillMaxWidth(), "SAVE A COMMAND") {
                HudField(name, "name (optional)", Modifier.fillMaxWidth().height(46.dp)) { name = it }
                Spacer(Modifier.height(4.dp))
                HudField(cmd, "command, e.g. ls -la /sdcard", Modifier.fillMaxWidth().height(46.dp)) { cmd = it }
                Spacer(Modifier.height(4.dp))
                HudButton("SAVE", Modifier.fillMaxWidth().height(44.dp), KeyKind.ACCENT, enabled = cmd.isNotBlank(), fontSize = 13.sp) {
                    val n = name.trim().ifEmpty { cmd.trim().take(20) }.uppercase()
                    Prefs.snippets = Prefs.snippets + Snippet(n, cmd.trim())
                    Prefs.commit()
                    name = ""; cmd = ""
                }
            }
            Spacer(Modifier.height(8.dp))
            HudText("TAP RUN TO SEND THE COMMAND TO THE CURRENT TAB", color = c.dim, size = 9.sp)
            Spacer(Modifier.height(4.dp))
        }
        if (Prefs.snippets.isEmpty()) item { EmptyNote("NO SAVED COMMANDS") }
        itemsIndexed(Prefs.snippets) { i, sn ->
            HudItem(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        HudText(sn.name, color = c.primary, size = 12.sp, bold = true, maxLines = 1)
                        HudText(sn.cmd, color = c.text, size = 11.sp, maxLines = 2)
                    }
                    HudButton("RUN", Modifier.width(60.dp).height(40.dp), KeyKind.GOOD, fontSize = 12.sp) { onRun(sn.cmd) }
                    HudButton("✕", Modifier.width(44.dp).height(40.dp), KeyKind.DANGER, fontSize = 13.sp) {
                        Prefs.snippets = Prefs.snippets.filterIndexed { j, _ -> j != i }
                        Prefs.commit()
                    }
                }
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}
