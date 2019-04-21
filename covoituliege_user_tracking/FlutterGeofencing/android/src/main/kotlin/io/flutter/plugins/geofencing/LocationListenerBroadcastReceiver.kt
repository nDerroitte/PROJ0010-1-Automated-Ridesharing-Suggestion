package io.flutter.plugins.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.flutter.view.FlutterMain


class LocationListenerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        FlutterMain.ensureInitializationComplete(context, null)
        LocationListenerService.enqueueWork(context, intent)
    }
}