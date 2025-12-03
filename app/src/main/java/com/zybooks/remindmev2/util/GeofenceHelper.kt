package com.zybooks.remindmev2.util

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.zybooks.remindmev2.data.local.database.Reminder

class GeofenceHelper(base: Context) : ContextWrapper(base) {

    companion object {
        // 1.a: Toggle this to TRUE to trigger "On Arrival" reminders immediately if you are ALREADY inside the location.
        // Default is FALSE (do not trigger if already inside).
        const val TEST_TRIGGER_ENTER_IF_ALREADY_INSIDE = true

        // 2.c: Toggle this to TRUE to FORCE a notification immediately when adding/updating a reminder,
        // regardless of your location or the reminder type. Useful for testing notification permission/display.
        const val TEST_FORCE_TRIGGER_NOW = true
    }

    private val geofencingClient = LocationServices.getGeofencingClient(this)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(reminder: Reminder) {
        // 2.c: Force trigger logic
        if (TEST_FORCE_TRIGGER_NOW) {
            NotificationHelper(this).sendNotification(
                reminder.title, 
                (reminder.notes ?: "") + " [TEST FORCED TRIGGER]"
            )
        }

        val geofence = Geofence.Builder()
            .setRequestId(reminder.id.toString())
            .setCircularRegion(reminder.latitude, reminder.longitude, reminder.geofenceRadius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(
                if (reminder.proximityType) Geofence.GEOFENCE_TRANSITION_ENTER else Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .build()

        val initialTrigger = if (reminder.proximityType) {
            // On Arrival
            if (TEST_TRIGGER_ENTER_IF_ALREADY_INSIDE) GeofencingRequest.INITIAL_TRIGGER_ENTER else 0
        } else {
            // On Departure: Do not trigger if already outside (default behavior is 0)
            0
        }

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(initialTrigger)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d("GeofenceHelper", "Geofence added for reminder ${reminder.id}")
            }
            addOnFailureListener {
                Log.e("GeofenceHelper", "Failed to add geofence", it)
            }
        }
    }

    fun removeGeofence(reminder: Reminder) {
        geofencingClient.removeGeofences(listOf(reminder.id.toString())).run {
            addOnSuccessListener {
                Log.d("GeofenceHelper", "Geofence removed for reminder ${reminder.id}")
            }
            addOnFailureListener {
                Log.e("GeofenceHelper", "Failed to remove geofence", it)
            }
        }
    }
}
