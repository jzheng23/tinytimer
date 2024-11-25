package com.jzheng.tinytimer.ui.navigation

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SharedPrefsViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()
    private val sharedPreferences =
        application.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)

    // LiveData for UID
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    // LiveData for isTester
    private val _isTester = MutableLiveData<Boolean>()
    val isTester: LiveData<Boolean> = _isTester


    private val _survey1Completed = MutableLiveData<Boolean>()
    private val _survey2Completed = MutableLiveData<Boolean>()
    private val _survey3Completed = MutableLiveData<Boolean>()
    private val _survey4Completed = MutableLiveData<Boolean>()
    private val _tutorialCompleted = MutableLiveData<Boolean>()
    private val _dayCount = MutableLiveData<Int>()
    val survey1Completed: LiveData<Boolean> = _survey1Completed
    val survey2Completed: LiveData<Boolean> = _survey2Completed
    val survey3Completed: LiveData<Boolean> = _survey3Completed
    val survey4Completed: LiveData<Boolean> = _survey4Completed
    val tutorialCompleted: LiveData<Boolean> = _tutorialCompleted
    val dayCount: LiveData<Int> = _dayCount

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                "UID" -> _userId.value = sharedPreferences.getString("UID", "")
                "is_tester" -> _isTester.value = sharedPreferences.getBoolean("is_tester", false)


                "survey1_completed" -> _survey1Completed.value =
                    sharedPreferences.getBoolean("survey1_completed", false)

                "survey2_completed" -> _survey2Completed.value =
                    sharedPreferences.getBoolean("survey2_completed", false)

                "survey3_completed" -> _survey3Completed.value =
                    sharedPreferences.getBoolean("survey3_completed", false)

                "survey4_completed" -> _survey4Completed.value =
                    sharedPreferences.getBoolean("survey4_completed", false)

                "tutorial_completed" -> _tutorialCompleted.value =
                    sharedPreferences.getBoolean("tutorial_completed", false)

                "day_count" -> _dayCount.value = sharedPreferences.getInt("day_count", 1)
            }
        }

    init {
        // Initialize LiveData with current preferences values
        _userId.value = sharedPreferences.getString("UID", "")
        _isTester.value = sharedPreferences.getBoolean("is_tester", false)
        _survey1Completed.value = sharedPreferences.getBoolean("survey1_completed", false)
        _survey2Completed.value = sharedPreferences.getBoolean("survey2_completed", false)
        _survey3Completed.value = sharedPreferences.getBoolean("survey3_completed", false)
        _survey4Completed.value = sharedPreferences.getBoolean("survey4_completed", false)
        _tutorialCompleted.value = sharedPreferences.getBoolean("tutorial_completed", false)
        _dayCount.value = sharedPreferences.getInt("day_count", 1)
        // Register the listener for changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onCleared() {
        super.onCleared()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}


class SharedPrefsViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedPrefsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedPrefsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}