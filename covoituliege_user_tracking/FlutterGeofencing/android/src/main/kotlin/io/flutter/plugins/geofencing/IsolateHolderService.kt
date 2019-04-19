// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.geofencing

import android.app.Service
import android.content.Intent
import android.os.IBinder
import io.flutter.view.FlutterNativeView

class IsolateHolderService : Service() {
    companion object {
        @JvmStatic
        private var sBackgroundFlutterView: FlutterNativeView? = null

        @JvmStatic
        fun setBackgroundFlutterView(view: FlutterNativeView?) {
            sBackgroundFlutterView = view
        }
    }

    override fun onBind(p0: Intent) : IBinder? {
        return null
    }
}
