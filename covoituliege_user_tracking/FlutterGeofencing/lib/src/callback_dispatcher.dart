import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:geofencing/src/timed_location.dart';

/// This function first ensures that the Flutter API used to call
/// Kotlin (or Objective-C) functions from dart code is well initialized.
/// Then, it registers a callback (see below).
/// It finally informs platform-specific code that the callback dispatcher is
/// initialized.
void callbackDispatcher() {
  const MethodChannel _backgroundChannel =
  MethodChannel('plugins.flutter.io/loc_listener_plugin_background');
  WidgetsFlutterBinding.ensureInitialized();

  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    // This function is called periodically by the platform specific modules,
    // it receives a raw batch location update and turn it into a list of TimedLocation,
    // and call the callback registered before (see timed_location) with this
    // list as argument.
    final List<dynamic> args = call.arguments;
    final Function callback = PluginUtilities.getCallbackFromHandle(
        CallbackHandle.fromRawHandle(args[0]));
    assert(callback != null);
    List<TimedLocation> locations = List<TimedLocation>();
    args[1].forEach((dynamic e) => locations.add(TimedLocation(e)));
    await callback(locations);
  });

  _backgroundChannel.invokeMethod('LocationListenerService.initialized');
}
