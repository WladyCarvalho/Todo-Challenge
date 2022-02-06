package com.expressive.todo_challenge.dado.dao

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "PreferenceManager"

enum class SortOrder
{
    POR_NOME,POR_DATA
}

data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted:Boolean)

@Singleton
class Preferencias @Inject constructor(@ApplicationContext context: Context){
    private val datastore = context.createDataStore("user_preferences")
    val preferencesFlow = datastore.data
        .catch {
                exception ->
            if (exception is IOException){

                Log.e(TAG, ":Erro lendo as preferencias ",exception )
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map { preference ->
            val sortOrder = SortOrder.valueOf(
                preference[PreferencesKeys.SORT_ORDER] ?: SortOrder.POR_DATA.name
            )

            val hideCompleted = preference[PreferencesKeys.HIDE_COMPLETED]?:false
            FilterPreferences(sortOrder,hideCompleted)

        }

    suspend fun updateSortOrder(sortOrder:SortOrder)
    {
        datastore.edit {
                preferences->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }
    suspend fun updateHideCompleted(hideCompleted:Boolean)
    {
        datastore.edit { preferences->
            preferences[PreferencesKeys.HIDE_COMPLETED]= hideCompleted
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}