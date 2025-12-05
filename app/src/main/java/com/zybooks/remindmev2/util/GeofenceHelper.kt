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
        // Toggle this to so "On Arrival" alerts immediately if already inside the location (dont want this behavior normally)
        const val TEST_TRIGGER_ENTER_IF_ALREADY_INSIDE = false

        // Toggle this to TRUE to FORCE a notification immediately regardless of where you are (used to test "on departure")
        const val TEST_FORCE_TRIGGER_NOW = true
    }

    private val geofencingClient = LocationServices.getGeofencingClient(this)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(reminder: Reminder) {
        // Trigger ONLY if we are currently OUTSIDE the geofence and proximityType is FALSE (On Departure)
        if (TEST_FORCE_TRIGGER_NOW && !reminder.proximityType) {
             LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { location ->
                 if (location != null) {
                     val results = FloatArray(1)
                     android.location.Location.distanceBetween(
                         location.latitude, location.longitude,
                         reminder.latitude, reminder.longitude,
                         results
                     )
                     // If OUTSIDE radius
                     if (results[0] > reminder.geofenceRadius) {
                         NotificationHelper(this).sendNotification(
                             reminder.title, 
                             (reminder.notes ?: "") + " [Already Outside/Departed Test]"
                         )
                     }
                 }
             }
        }

        // Trigger ONLY if we are currently INSIDE the geofence and proximityType is TRUE (On Arrival)
        if (TEST_TRIGGER_ENTER_IF_ALREADY_INSIDE && reminder.proximityType) {
             LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { location ->
                 if (location != null) {
                     val results = FloatArray(1)
                     android.location.Location.distanceBetween(
                         location.latitude, location.longitude,
                         reminder.latitude, reminder.longitude,
                         results
                     )
                     // If inside radius
                     if (results[0] <= reminder.geofenceRadius) {
                         NotificationHelper(this).sendNotification(
                             reminder.title, 
                             reminder.notes ?: "You have arrived! [Already Inside Test]"
                         )
                     }
                 }
             }
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
