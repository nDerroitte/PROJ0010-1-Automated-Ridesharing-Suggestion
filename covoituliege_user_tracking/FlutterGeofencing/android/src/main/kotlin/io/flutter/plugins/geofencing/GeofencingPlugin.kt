// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.geofencing

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar
import org.json.JSONArray
import org.json.JSONObject

class GeofencingPlugin(context: Context, activity: Activity?) : MethodCallHandler {
    private val mContext = context
    private val mActivity = activity
    private val mGeofencingClient = LocationServices.getGeofencingClient(mContext)
    private val mLocListenerClient = FusedLocationProviderClient(mContext)

    companion object {
        @JvmStatic
        private val TAG = "GeofencingPlugin"
        @JvmStatic
        val SHARED_PREFERENCES_KEY = "geofencing_plugin_cache"
        @JvmStatic
        val CALLBACK_HANDLE_GEO_KEY = "callback_handle_geo"
        @JvmStatic
        val CALLBACK_HANDLE_LL_KEY = "callback_handle_ll"
        @JvmStatic
        val CALLBACK_DISPATCHER_HANDLE_KEY = "callback_dispatch_handler"
        @JvmStatic
        val PERSISTENT_GEOFENCES_KEY = "persistent_geofences"
        @JvmStatic
        val PERSISTENT_GEOFENCES_IDS = "persistent_geofences_ids"
        @JvmStatic
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        @JvmStatic
        private val sGeofenceCacheLock = Object()

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val plugin = GeofencingPlugin(registrar.context(), registrar.activity())
            val channel = MethodChannel(registrar.messenger(), "plugins.flutter.io/geofencing_plugin")
            channel.setMethodCallHandler(plugin)
        }

        @JvmStatic
        fun reRegisterAfterReboot(context: Context) {
            synchronized(sGeofenceCacheLock) {
                var p = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                var persistentGeofences = p.getStringSet(PERSISTENT_GEOFENCES_IDS, null)
                if (persistentGeofences == null) {
                    return
                }
                for (id in persistentGeofences) {
                    val gfJson = p.getString(getPersistentGeofenceKey(id), null)
                    if (gfJson == null) {
                        continue
                    }
                    val gfArgs = JSONArray(gfJson)
                    val list = ArrayList<Object>()
                    for (i in 0 until gfArgs.length()) {
                        list.add(gfArgs.get(i) as Object)
                    }
                    val geoClient = LocationServices.getGeofencingClient(context)
                    registerGeofence(context, geoClient, list, null, false)
                }
            }
        }

        @JvmStatic
        private fun registerLocationListener(context: Context,
                                             fusedLocationProviderClient: FusedLocationProviderClient,
                                             args: ArrayList<*>?,
                                             result: Result?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_DENIED)) {
                val msg = "'registerLocationListener' requires the ACCESS_FINE_LOCATION permission."
                Log.w(TAG, msg)
                result?.error(msg, null, null)
                return
            }
            val callbackHandle = args!![0] as Long
            val interval = args[1] as Int
            val maxWaitTime = args[2] as Int
            val smallestDisplacement = args[3] as Double
            val locRequest = LocationRequest().setInterval(interval.toLong()) // 2 min 30
                    .setMaxWaitTime(maxWaitTime.toLong())    //1 hour
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setSmallestDisplacement(smallestDisplacement.toFloat()) // 100 meters
            val intent = Intent(context, GeofencingBroadcastReceiver::class.java)
                    .setType(callbackHandle.toString())
            val pIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            fusedLocationProviderClient.requestLocationUpdates(locRequest, pIntent).run {
                addOnSuccessListener {
                    Log.d(TAG, "successfully added location listener")
                    result?.success(true)
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to add location listener: $it")
                    result?.error(it.toString(), null, null)
                }
            }
        }

        @JvmStatic
        private fun registerGeofence(context: Context,
                                     geofencingClient: GeofencingClient,
                                     args: ArrayList<*>?,
                                     result: Result?,
                                     cache: Boolean) {
            val callbackHandle = args!![0] as Long
            val id = args[1] as String
            val lat = args[2] as Double
            val long = args[3] as Double
            val radius = (args[4] as Number).toFloat()
            val fenceTriggers = args[5] as Int
            val initialTriggers = args[6] as Int
            val expirationDuration = (args[7] as Int).toLong()
            val loiteringDelay = args[8] as Int
            val notificationResponsiveness = args[9] as Int
            val geofence = Geofence.Builder()
                    .setRequestId(id)
                    .setCircularRegion(lat, long, radius)
                    .setTransitionTypes(fenceTriggers)
                    .setLoiteringDelay(loiteringDelay)
                    .setNotificationResponsiveness(notificationResponsiveness)
                    .setExpirationDuration(expirationDuration)
                    .build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_DENIED)) {
                val msg = "'registerGeofence' requires the ACCESS_FINE_LOCATION permission."
                Log.w(TAG, msg)
                result?.error(msg, null, null)
            }
            geofencingClient.addGeofences(getGeofencingRequest(geofence, initialTriggers),
                    getGeofencePendingIndent(context, callbackHandle))?.run {
                addOnSuccessListener {
                    Log.i(TAG, "Successfully added geofence")
                    if (cache) {
                        addGeofenceToCache(context, id, args)
                    }
                    result?.success(true)
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to add geofence: $it")
                    result?.error(it.toString(), null, null)
                }
            }
        }

        @JvmStatic
        private fun addGeofenceToCache(context: Context, id: String, args: ArrayList<*>) {
            synchronized(sGeofenceCacheLock) {
                var p = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                var obj = JSONArray(args)
                var persistentGeofences = p.getStringSet(PERSISTENT_GEOFENCES_IDS, null)
                if (persistentGeofences == null) {
                    persistentGeofences = HashSet<String>()
                }

                persistentGeofences.add(id)
                context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                        .edit()
                        .putStringSet(PERSISTENT_GEOFENCES_IDS, persistentGeofences)
                        .putString(getPersistentGeofenceKey(id), obj.toString())
                        .apply()
            }
        }


        @JvmStatic
        private fun initializeService(context: Context, args: ArrayList<*>?) {
            Log.d(TAG, "Initializing GeofencingService")
            val callbackHandle = args!![0] as Long
            context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(CALLBACK_DISPATCHER_HANDLE_KEY, callbackHandle)
                    .apply()
        }

        @JvmStatic
        private fun getGeofencingRequest(geofence: Geofence, initialTrigger: Int): GeofencingRequest {
            return GeofencingRequest.Builder().apply {
                setInitialTrigger(initialTrigger)
                addGeofence(geofence)
            }.build()
        }

        @JvmStatic
        private fun getGeofencePendingIndent(context: Context, callbackHandle: Long): PendingIntent {
            val intent = Intent(context, GeofencingBroadcastReceiver::class.java)
                    .putExtra(CALLBACK_HANDLE_GEO_KEY, callbackHandle)
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        @JvmStatic
        private fun removeLocationListener(context : Context,
                                           fusedLocationProviderClient: FusedLocationProviderClient,
                                           args: ArrayList<*>?,
                                           result: Result) {
            val callbackHandle = args!![0] as Long
            val intent = Intent(context, GeofencingBroadcastReceiver::class.java)
                    .setType(callbackHandle.toString())
            val pIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            fusedLocationProviderClient.removeLocationUpdates(pIntent).run {
                addOnSuccessListener {
                    result.success(true)
                    Log.d(TAG, "Successfully removed location listener")
                }
                addOnFailureListener {
                    result.error(it.toString(), null, null)
                    Log.e(TAG, "Failed to remove location listener : $it")
                }
            }
        }

        @JvmStatic
        private fun removeGeofence(context: Context,
                                   geofencingClient: GeofencingClient,
                                   args: ArrayList<*>?,
                                   result: Result) {
            val ids = listOf(args!![0] as String)
            geofencingClient.removeGeofences(ids).run {
                addOnSuccessListener {
                    result.success(true)
                    for (id in ids) {
                        removeGeofenceFromCache(context, id)
                    }
                    Log.d(TAG, "Successfully removed geofence")
                }
                addOnFailureListener {
                    result.error(it.toString(), null, null)
                    Log.e(TAG, "Failed to remove geofence : $it")
                }
            }
        }

        @JvmStatic
        private fun removeGeofenceFromCache(context: Context, id: String) {
            synchronized(sGeofenceCacheLock) {
                var p = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                var persistentGeofences = p.getStringSet(PERSISTENT_GEOFENCES_IDS, null)
                if (persistentGeofences == null) {
                    return
                }
                persistentGeofences.remove(id)
                p.edit()
                        .remove(getPersistentGeofenceKey(id))
                        .putStringSet(PERSISTENT_GEOFENCES_IDS, persistentGeofences)
                        .apply()
            }
        }

        @JvmStatic
        private fun getPersistentGeofenceKey(id: String): String {
            return "persistent_geofence/" + id
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        val args = call.arguments() as? ArrayList<*>
        when (call.method) {
            "GeofencingPlugin.initializeService" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mActivity?.requestPermissions(REQUIRED_PERMISSIONS, 12312)
                }
                initializeService(mContext, args)
                result.success(true)
            }
            "GeofencingPlugin.registerLocationListener" -> registerLocationListener(mContext,
                    mLocListenerClient,
                    args,
                    result)
            "GeofencingPlugin.removeLocationListener" -> removeLocationListener(mContext,
                    mLocListenerClient,
                    args,
                    result)
            "GeofencingPlugin.registerGeofence" -> registerGeofence(mContext,
                    mGeofencingClient,
                    args,
                    result,
                    true)
            "GeofencingPlugin.removeGeofence" -> removeGeofence(mContext,
                    mGeofencingClient,
                    args,
                    result)
            else -> result.notImplemented()
        }
    }
}
