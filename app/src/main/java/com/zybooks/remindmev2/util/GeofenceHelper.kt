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

    private val geofencingClient = LocationServices.getGeofencingClient(this)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(reminder: Reminder) {
        val geofence = Geofence.Builder()
            .setRequestId(reminder.id.toString())
            .setCircularRegion(reminder.latitude, reminder.longitude, reminder.geofenceRadius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(
                if (reminder.proximityType) Geofence.GEOFENCE_TRANSITION_ENTER else Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
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

