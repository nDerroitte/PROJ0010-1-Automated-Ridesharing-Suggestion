import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:geofencing/src/timed_location.dart';

void callbackDispatcher() {
  const MethodChannel _backgroundChannel =
  MethodChannel('plugins.flutter.io/loc_listener_plugin_background');
  WidgetsFlutterBinding.ensureInitialized();

  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
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
