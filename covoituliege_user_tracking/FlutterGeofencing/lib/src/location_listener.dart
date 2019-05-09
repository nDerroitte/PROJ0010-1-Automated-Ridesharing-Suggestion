import 'dart:ui';

import 'package:flutter/services.dart';

import 'package:geofencing/src/timed_location.dart';
import 'package:geofencing/src/callback_dispatcher.dart';

/// Unregister the service related to the given callback, started sooner with
/// the function below. Unregistering a not registered service is simply a noOp.
void removeLocListener(void Function(List<TimedLocation> locations) callback) {
  final List<dynamic> args = <dynamic>[
    PluginUtilities.getCallbackHandle(callback).toRawHandle()
  ];
  MethodChannel('plugins.flutter.io/loc_listener_plugin')
      .invokeMethod('LocationListenerPlugin.removeLocationListener', args);
}

/// Starts a service that will receive location updates from the OS.
/// When an update is received, a job scheduler is started and at some point,
/// the provided callback will be called with the provided locations, represented
/// by a list of TimedLocations.
/// The two last arguments are used to tune the speed of the updates.
void registerLocListener(
    void Function(List<TimedLocation> locations) callback,
    int timeIntervalBetweenPoints,
    int maxWaitTimeForUpdates) {
  initialize();
  final List<dynamic> args = <dynamic>[
    PluginUtilities.getCallbackHandle(callback).toRawHandle()
  ];
  args.add(timeIntervalBetweenPoints);
  args.add(maxWaitTimeForUpdates);
  MethodChannel('plugins.flutter.io/loc_listener_plugin')
      .invokeMethod('LocationListenerPlugin.registerLocationListener', args);
}

/// Initializes the service used above to register and unregister location
/// services.
void initialize() {
  final List<dynamic> args = <dynamic>[
    PluginUtilities.getCallbackHandle(callbackDispatcher).toRawHandle()
  ];
  MethodChannel('plugins.flutter.io/loc_listener_plugin')
      .invokeMethod('LocationListenerPlugin.initializeService', args);
}
