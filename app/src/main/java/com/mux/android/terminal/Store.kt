package com.mux.android.terminal

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject

data class Snippet(val name: String, val cmd: String)

/** Settings, aliases, environment, history and saved commands. */
object Prefs {
    private var sp: SharedPreferences? = null
    var amber by mutableStateOf(false)
    var fontSize by mutableIntStateOf(13)
    var keepAwake by mutableStateOf(true)
    var extraKeys by mutableStateOf(true)
    var startSd by mutableStateOf(false)
    var maxLines by mutableIntStateOf(3000)
    var snippets by mutableStateOf<List<Snippet>>(emptyList())
    var aliases by mutableStateOf<Map<String, String>>(emptyMap())
    var userEnv by mutableStateOf<Map<String, String>>(emptyMap())
    val history = ArrayList<String>()

    fun init(ctx: Context) {
        if (sp != null) return
        val p = ctx.applicationContext.getSharedPreferences("mux_terminal", Context.MODE_PRIVATE)
        sp = p
        amber = p.getBoolean("amber", false)
        fontSize = p.getInt("font", 13)
        keepAwake = p.getBoolean("awake", true)
        extraKeys = p.getBoolean("xkeys", true)
        startSd = p.getBoolean("startsd", false)
        maxLines = p.getInt("maxlines", 3000)
        try {
            val a = JSONArray(p.getString("snippets", null) ?: defaultSnippets())
            val l = ArrayList<Snippet>()
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                l.add(Snippet(o.getString("n"), o.getString("c")))
            }
            snippets = l
        } catch (e: Exception) {
        }
        aliases = readMap(p.getString("aliases", null) ?: "{\"ll\":\"ls -la\",\"la\":\"ls -a\",\"..\":\"cd ..\",\"sdcard\":\"cd /storage/emulated/0\"}")
        userEnv = readMap(p.getString("env", "{}") ?: "{}")
        try {
            val h = JSONArray(p.getString("history", "[]"))
            for (i in 0 until h.length()) history.add(h.getString(i))
        } catch (e: Exception) {
        }
    }

    private fun readMap(s: String): Map<String, String> {
        val m = LinkedHashMap<String, String>()
        try {
            val o = JSONObject(s)
            for (k in o.keys()) m[k] = o.getString(k)
        } catch (e: Exception) {
        }
        return m
    }

    private fun defaultSnippets(): String {
        val d = listOf(
            "DEVICE MODEL" to "getprop ro.product.model",
            "ANDROID VERSION" to "getprop ro.build.version.release",
            "KERNEL" to "uname -a",
            "DISK USAGE" to "df -h",
            "PROCESSES" to "ps -A | head -30",
            "NETWORK" to "ip addr",
            "PING GOOGLE" to "ping -c 4 8.8.8.8",
            "CPU INFO" to "cat /proc/cpuinfo | head -24",
            "MEMORY" to "cat /proc/meminfo | head -8"
        )
        val a = JSONArray()
        for ((n, c) in d) a.put(JSONObject().put("n", n).put("c", c))
        return a.toString()
    }

    fun commit() {
        val a = JSONArray()
        for (s in snippets) a.put(JSONObject().put("n", s.name).put("c", s.cmd))
        sp?.edit()
            ?.putBoolean("amber", amber)?.putInt("font", fontSize)?.putBoolean("awake", keepAwake)
            ?.putBoolean("xkeys", extraKeys)?.putBoolean("startsd", startSd)?.putInt("maxlines", maxLines)
            ?.putString("snippets", a.toString())
            ?.putString("aliases", JSONObject(aliases as Map<*, *>).toString())
            ?.putString("env", JSONObject(userEnv as Map<*, *>).toString())
            ?.apply()
    }

    fun addHistory(cmd: String) {
        if (history.lastOrNull() == cmd) return
        history.add(cmd)
        while (history.size > 300) history.removeAt(0)
        saveHistory()
    }

    fun clearHistory() {
        history.clear()
        saveHistory()
    }

    private fun saveHistory() {
        sp?.edit()?.putString("history", JSONArray(history).toString())?.apply()
    }
}
