package com.anemia.detection.storage

import android.content.Context
import com.anemia.detection.models.DetectionResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryRepository(context: Context) {

    private val prefs = context.getSharedPreferences("anemia_history_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_HISTORY = "offline_scan_history"
    }

    @Synchronized
    fun getAllResults(): List<DetectionResult> {
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<DetectionResult>>() {}.type
            val list: List<DetectionResult> = gson.fromJson(json, type)
            list.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @Synchronized
    private fun persistResults(list: List<DetectionResult>) {
        val json = gson.toJson(list)
        prefs.edit().putString(KEY_HISTORY, json).apply()
    }

    @Synchronized
    fun saveResult(result: DetectionResult) {
        val currentList = getAllResults().toMutableList()
        currentList.add(0, result)
        persistResults(currentList)
    }

    @Synchronized
    fun deleteResult(id: String) {
        val currentList = getAllResults().filter { it.id != id }
        persistResults(currentList)
    }

    @Synchronized
    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    @Synchronized
    fun getLastResult(): DetectionResult? {
        val list = getAllResults()
        return if (list.isNotEmpty()) list.first() else null
    }

    @Synchronized
    fun markAsSynced(id: String) {
        val currentList = getAllResults().map {
            if (it.id == id) it.copy(isSynced = true) else it
        }
        persistResults(currentList)
    }

    @Synchronized
    fun markAllAsSynced() {
        val currentList = getAllResults().map { it.copy(isSynced = true) }
        persistResults(currentList)
    }

    @Synchronized
    fun getUnsyncedCount(): Int {
        return getAllResults().count { !it.isSynced }
    }
}
