package com.zybooks.remindmev2.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.zybooks.remindmev2.RemindMeApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            return
        }
        
        if (geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Geofencing error: ${geofencingEvent.errorCode}")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            
            val repository = (context.applicationContext as RemindMeApplication).repository
            val notificationHelper = NotificationHelper(context)
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

            triggeringGeofences?.forEach { geofence ->
                val reminderId = geofence.requestId.toLongOrNull()
                if (reminderId != null) {
                    scope.launch {
                        val reminderWithTags = repository.getReminder(reminderId)
                        if (reminderWithTags != null) {
                            val title = reminderWithTags.reminder.title
                            val transitionType = geofenceTransition
                            val baseMessage = reminderWithTags.reminder.notes ?: ""
                            
                            val message = when (transitionType) {
                                Geofence.GEOFENCE_TRANSITION_ENTER -> "Arrived: $baseMessage"
                                Geofence.GEOFENCE_TRANSITION_EXIT -> "Departed: $baseMessage"
                                else -> baseMessage
                            }
                            
                            notificationHelper.sendNotification(title, message)
                        }
                    }
                }
            }
        }
    }
}

