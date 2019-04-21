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
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar

class LocationListenerPlugin(context: Context, activity: Activity?) : MethodCallHandler {
    private val mContext = context
    private val mActivity = activity
    private val mLocListenerClient = FusedLocationProviderClient(mContext)

    companion object {
        @JvmStatic
        private val TAG = "LocListenerPlugin"
        @JvmStatic
        val SHARED_PREFERENCES_KEY = "loc_listener_plugin_cache"
        @JvmStatic
        val CALLBACK_DISPATCHER_HANDLE_KEY = "callback_dispatch_handler"
        @JvmStatic
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val plugin = LocationListenerPlugin(registrar.context(), registrar.activity())
            val channel = MethodChannel(registrar.messenger(), "plugins.flutter.io/loc_listener_plugin")
            channel.setMethodCallHandler(plugin)
        }

        @JvmStatic
        private fun initializeService(context: Context, args: ArrayList<*>?) {
            Log.d(TAG, "Initializing location listener service")
            val callbackHandle = args!![0] as Long
            context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(CALLBACK_DISPATCHER_HANDLE_KEY, callbackHandle)
                    .apply()
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
            val pIntent = getLocListenerPendingIntent(context, callbackHandle)
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
        private fun removeLocationListener(context : Context,
                                           fusedLocationProviderClient: FusedLocationProviderClient,
                                           args: ArrayList<*>?,
                                           result: Result) {
            val callbackHandle = args!![0] as Long
            val pIntent = getLocListenerPendingIntent(context, callbackHandle)
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
        private fun getLocListenerPendingIntent(context: Context,
                                                callbackHandle : Long) : PendingIntent {
            val intent = Intent(context, LocationListenerBroadcastReceiver::class.java)
                    .setType(callbackHandle.toString())
            return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        val args = call.arguments() as? ArrayList<*>
        when (call.method) {
            "LocationListenerPlugin.initializeService" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mActivity?.requestPermissions(REQUIRED_PERMISSIONS, 12312)
                }
                initializeService(mContext, args)
                result.success(true)
            }
            "LocationListenerPlugin.registerLocationListener" -> registerLocationListener(mContext,
                    mLocListenerClient,
                    args,
                    result)
            "LocationListenerPlugin.removeLocationListener" -> removeLocationListener(mContext,
                    mLocListenerClient,
                    args,
                    result)
            else -> result.notImplemented()
        }
    }
}
