package com.zybooks.remindmev2.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zybooks.remindmev2.RemindMeApplication
import com.zybooks.remindmev2.ui.viewmodels.AddEditReminderViewModel
import com.zybooks.remindmev2.ui.viewmodels.ReminderListViewModel
import com.zybooks.remindmev2.ui.viewmodels.SettingsViewModel
import com.zybooks.remindmev2.util.GeofenceHelper

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ReminderListViewModel(
                remindMeApplication().repository
            )
        }
        initializer {
            AddEditReminderViewModel(
                this.createSavedStateHandle(),
                remindMeApplication().repository,
                GeofenceHelper(remindMeApplication())
            )
        }
        initializer {
            SettingsViewModel(
                remindMeApplication().settingsRepository
            )
        }
    }
}

fun CreationExtras.remindMeApplication(): RemindMeApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RemindMeApplication)

