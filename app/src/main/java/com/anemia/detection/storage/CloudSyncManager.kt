package com.anemia.detection.storage

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object CloudSyncManager {

    fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun syncPendingRecords(
        context: Context,
        onComplete: (success: Boolean, countSynced: Int, message: String) -> Unit
    ) {
        val repo = HistoryRepository(context)
        val unsynced = repo.getAllResults().filter { !it.isSynced }

        if (unsynced.isEmpty()) {
            onComplete(true, 0, "Todos los registros ya están sincronizados.")
            return
        }

        if (!isNetworkAvailable(context)) {
            onComplete(false, 0, "No hay conexión a Internet estable. Los datos siguen protegidos en tu almacenamiento local.")
            return
        }

        // Simulación de envío a base de datos en la nube (ej. Firebase / Supabase / FHIR Endpoint)
        // con retardo de red controlado y marcado de éxito
        kotlinx.coroutines.delay(1200)

        repo.markAllAsSynced()
        onComplete(true, unsynced.size, "Se sincronizaron con éxito ${unsynced.size} chequeos a la nube.")
    }
}
