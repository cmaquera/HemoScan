package com.anemia.detection.storage

import android.content.Context
import com.anemia.detection.models.DetectionResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryRepository(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences("anemia_history_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_HISTORY = "offline_scan_history"
        private val LOCK = Any()
    }

    fun getAllResults(): List<DetectionResult> = synchronized(LOCK) {
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<DetectionResult?>>() {}.type
            val rawList: List<DetectionResult?> = gson.fromJson(json, type) ?: return emptyList()
            val seenIds = HashSet<String>()
            rawList
                .filterNotNull()
                .mapIndexed { index, item ->
                    val normalized = item.normalized(fallbackIndex = index)
                    if (seenIds.add(normalized.id)) {
                        normalized
                    } else {
                        val uniqueId = "${normalized.id}_$index"
                        seenIds.add(uniqueId)
                        normalized.copy(id = uniqueId)
                    }
                }
                .sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun persistResults(list: List<DetectionResult>) = synchronized(LOCK) {
        val json = gson.toJson(list)
        prefs.edit().putString(KEY_HISTORY, json).apply()
    }

    fun saveResult(result: DetectionResult) = synchronized(LOCK) {
        val currentList = getAllResults().toMutableList()
        currentList.add(0, result)
        persistResults(currentList)
    }

    fun deleteResult(id: String) = synchronized(LOCK) {
        val currentList = getAllResults().filter { it.id != id }
        persistResults(currentList)
    }

    fun clearHistory() = synchronized(LOCK) {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    fun getLastResult(): DetectionResult? = synchronized(LOCK) {
        val list = getAllResults()
        return if (list.isNotEmpty()) list.first() else null
    }

    fun markAsSynced(id: String) = synchronized(LOCK) {
        val currentList = getAllResults().map {
            if (it.id == id) it.copy(isSynced = true) else it
        }
        persistResults(currentList)
    }

    fun markAllAsSynced() = synchronized(LOCK) {
        val currentList = getAllResults().map { it.copy(isSynced = true) }
        persistResults(currentList)
    }

    fun getUnsyncedCount(): Int = synchronized(LOCK) {
        return getAllResults().count { !it.isSynced }
    }
}
