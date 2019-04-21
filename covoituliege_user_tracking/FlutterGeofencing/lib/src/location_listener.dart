import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:geofencing/src/timed_location.dart';

void removeLocListener(void Function(List<TimedLocation> locations) callback) {
  final List<dynamic> args = <dynamic>[
    PluginUtilities.getCallbackHandle(callback).toRawHandle()
  ];
  MethodChannel('plugins.flutter.io/loc_listener_plugin')
      .invokeMethod('LocationListenerPlugin.removeLocationListener', args);
}

void registerLocListener(
    void Function(List<TimedLocation> locations) callback,
    int timeIntervalBetweenPoints,
    int maxWaitTimeForUpdates,
    double minDistanceBetweenPoints) {
  initialize();
  final List<dynamic> args = <dynamic>[
    PluginUtilities.getCallbackHandle(callback).toRawHandle()
  ];
  args.add(timeIntervalBetweenPoints);
  args.add(maxWaitTimeForUpdates);
  args.add(minDistanceBetweenPoints);
  MethodChannel('plugins.flutter.io/loc_listener_plugin')
      .invokeMethod('LocationListenerPlugin.registerLocationListener', args);
}

void initialize() {
  final List<dynamic> args = <dynamic>[
    PluginUtilities.getCallbackHandle(callbackDispatcher).toRawHandle()
  ];
  MethodChannel('plugins.flutter.io/loc_listener_plugin')
      .invokeMethod('LocationListenerPlugin.initializeService', args);
}

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
