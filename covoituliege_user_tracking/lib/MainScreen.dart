import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:connectivity/connectivity.dart';
import 'package:intl/intl.dart';
import 'package:geofencing/geofencing.dart';
import 'package:latlong/latlong.dart';
import 'package:flutter/services.dart';
import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'Cst.dart';
import 'UserInfo.dart';
import 'FileHandler.dart';
import 'serverCommunication.dart';
import 'PrintDataScreen.dart';

/// This class represents the main screen of the application. It allows the user to launch the position capturing,
/// as well as printing and deleting the buffered data.
class MainScreen extends StatefulWidget {
  final UserInfo user;
  final ServerCommunication serverCommunication;
  final bool anonymous;

  MainScreen(this.user, this.serverCommunication, [this.anonymous = false]);

  @override
  _MainScreenState createState() => new _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  Function _pressedOnOff;
  IconData _onOffIcon;
  static UserInfo _user;
  Stream<ConnectivityResult> _onConnectivityChanged;
  StreamSubscription<ConnectivityResult> _connectivitySubscription;
  bool _waitingForWifi = false;
  ServerCommunication _serverCommunication;
  bool _anonymous;

  static Future<void> outOfGeofenceCallback(
      List<String> ids, Location location, GeofenceEvent event) async {
    /// Note : int are 64-bits in Flutter, which means that even if we triggered
    /// 1000 geofences per second, it would take more that 500 millions years to overflow
    DateTime curTime = DateTime.now();
    String calendar = DateFormat(dateFormat).format(DateTime.now());
    String expectedId = await getLastGeofenceId();
    if (ids.contains(expectedId)) {
      String prevCalendar = await getLastCalendar();
      if (prevCalendar != null &&
          curTime
              .subtract(minPauseTimeBetweenJourneys)
              .isAfter(DateFormat(dateFormat).parse(prevCalendar))) {
        writeJourneyFromBufferedPoints(_user);
      }
      storePoint(calendar, location.latitude.toString(),
          location.longitude.toString());

      String newId = (int.parse(expectedId) + 1).toString();
      GeofenceRegion newGeofence = GeofenceRegion(
          newId,
          location.latitude,
          location.longitude,
          distBetweenPoints,
          <GeofenceEvent>[GeofenceEvent.exit],
          androidSettings: androidSettings);

      /// No volatile state can be stored in background because the application
      /// may get killed at anytime (and reopened when needed)
      storeGeofenceById(newId);
      GeofencingManager.registerGeofence(newGeofence, outOfGeofenceCallback);
    }
    for (String id in ids) {
      GeofencingManager.removeGeofenceById(id);
    }
  }

  static Future<void> newPointsBatchCallback(
      List<TimedLocation> locations) async {
    String lastCalendar;
    double lastLat;
    double lastLon;
    List<String> lastTimedLoc = await getLastTimedLoc();
    if (lastTimedLoc == null) {
      if (locations.length > 0) {
        lastCalendar = locations[0].calendar;
        lastLat = double.parse(locations[1].latitude);
        lastLon = double.parse(locations[2].longitude);
        await storePoint(lastCalendar, locations[1].latitude, locations[2].longitude);
      }
    } else {
      lastCalendar = lastTimedLoc[0];
      lastLat = double.parse(lastTimedLoc[1]);
      lastLon = double.parse(lastTimedLoc[2]);
    }
    String newCalendar;
    double distance;

    for (TimedLocation loc in locations) {
      distance = DistanceVincenty().distance(LatLng(lastLat, lastLon),
          LatLng(double.parse(loc.latitude), double.parse(loc.longitude)));
      print("long but not infinite vincenty");
      if (await hasJourneyStarted()) {
        print("journey has started");
        if (DateFormat(dateFormat)
            .parse(lastCalendar)
            .add(minPauseTimeBetweenJourneys)
            .isBefore(DateFormat(dateFormat).parse(loc.calendar))) {
          if (distance < minDistanceNewJourney) {
            newCalendar = DateFormat(dateFormat).format(DateFormat(dateFormat)
                .parse(lastCalendar)
                .add(Duration(minutes: 2, seconds: 30)));
            await storePoint(newCalendar, loc.latitude, loc.longitude);
          }
          await writeJourneyFromBufferedPoints(_user);
          await storeGeofenceCenter(loc.latitude, loc.longitude);
          lastLat = double.parse(loc.latitude);
          lastLon = double.parse(loc.longitude);
        } else if (distance > minDistanceNewJourney) {
          await storePoint(loc.calendar, loc.latitude, loc.longitude);
          lastCalendar = loc.calendar;
          lastLat = double.parse(loc.latitude);
          lastLon = double.parse(loc.longitude);
        }
      } else if (distance > minDistanceNewJourney) {
        print("journey has not started");
        await storePoint(loc.calendar, loc.latitude, loc.longitude);
        lastCalendar = loc.calendar;
        lastLat = double.parse(loc.latitude);
        lastLon = double.parse(loc.longitude);
      }
    }
  }

  /// This function clear the buffer and starts a new capturePos process.
  /// It also updates the button so that it's now a stop button.
  /// It is called when the user taps on the start button.
  /// //TODO rewrite doc
  void _start() async {
    if (Platform.isIOS) {
      //TODO implement location batches on IOs side
      clearBuffer();

      /// Either the user is currently out of this geofence, in which case
      /// it will be triggered directly, giving the process the current location,
      /// either it will be triggered after the user has moved. In both cases,
      /// there is no problem "hard-coding" a 0,0 location.
      GeofenceRegion newGeofence = GeofenceRegion(
          '0', 0, 0, distBetweenPoints, <GeofenceEvent>[GeofenceEvent.exit],
          androidSettings: androidSettings);

      /// No volatile state can be stored in background because the application
      /// may get killed at anytime (and reopened when needed)
      storeGeofenceById('0');

      await GeofencingManager.initialize();
      await GeofencingManager.registerGeofence(
          newGeofence, outOfGeofenceCallback);
    } else {
      final List<dynamic> args = <dynamic>[
        PluginUtilities.getCallbackHandle(newPointsBatchCallback).toRawHandle()
      ];
      args.add(timeIntervalBetweenPoints);
      args.add(maxWaitTimeForUpdates);
      args.add(minDistanceBetweenPoints);
      MethodChannel('plugins.flutter.io/geofencing_plugin')
          .invokeMethod('GeofencingPlugin.registerLocationListener', args);
      await startedLocListener();
    }
    setState(() {
      _pressedOnOff = _stop;
      _onOffIcon = Icons.stop;
    });
  }

  /// This function is called when the user taps on the stop button.
  /// It saves the currently buffered data in a file (but it should send it if possible, this is still to do),
  /// and updates the button so that it becomes a start button.
  _stop() async {
    writeJourneyFromBufferedPoints(_user);

    if (Platform.isIOS) {
      String lastGeofence = await getLastGeofenceId();
      if (lastGeofence != null) {
        GeofencingManager.removeGeofenceById(lastGeofence);
        clearGeofence();
      }
    } else {
      final List<dynamic> args = <dynamic>[
        PluginUtilities.getCallbackHandle(newPointsBatchCallback).toRawHandle()
      ];
      MethodChannel('plugins.flutter.io/geofencing_plugin')
          .invokeMethod('GeofencingPlugin.removeLocationListener', args);
      await stoppedLocListener();
    }

    setState(() {
      _pressedOnOff = _start;
      _onOffIcon = Icons.play_arrow;
    });
  }

  _sendPoints() async {
    ConnectivityResult connectivity = await Connectivity().checkConnectivity();

    /// We try to send the data, if it fails (likely because wifi is not available),
    /// we wait for the state of the connectivity to change and we retry.
    /// If a data unit was collected anonymously, it is filled with the current username.
    if (connectivity == ConnectivityResult.wifi &&
        await _serverCommunication.sendPoints((await readFile()).replaceAll(
            RegExp("\"UserId\":\"\""),
            "\"UserId\":\"" + _user.getId() + "\""))) {
      clearFile();
      _waitingForWifi = false;
    } else {
      _connectivitySubscription =
          _onConnectivityChanged.listen((ConnectivityResult result) {
        _connectivitySubscription.cancel();
        _sendPoints();
      });
    }
  }

  _printDataScreen() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => PrintDataScreen()),
    );
  }

  _playOrStop() async {
    if (Platform.isIOS) { //TODO should be removed soon
      if (await getLastGeofenceId() == null) {
        _pressedOnOff = _start;
        _onOffIcon = Icons.play_arrow;
      } else {
        _pressedOnOff = _stop;
        _onOffIcon = Icons.stop;
      }
    } else {
      if (await isLocListenerStarted()) {
        _pressedOnOff = _stop;
        _onOffIcon = Icons.stop;
      } else {
        _pressedOnOff = _start;
        _onOffIcon = Icons.play_arrow;
      }
    }
    setState(() {});
  }

  @override
  void initState() {
    super.initState();
    _user = widget.user;
    _anonymous = widget.anonymous;
    if (!_anonymous && !_waitingForWifi) {
      _sendPoints();
      _waitingForWifi = true;
    }

    _onConnectivityChanged = Connectivity().onConnectivityChanged.skip(1);
    _waitingForWifi = false;
    _serverCommunication = widget.serverCommunication;
    _playOrStop();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Center(child: Text('Ugo              ')),
        // Please do not delete whitespaces
        flexibleSpace: Container(
          decoration: new BoxDecoration(
            gradient: new LinearGradient(
                colors: [
                  const Color(0xFF3366FF),
                  const Color(0xFF00CCFF),
                ],
                begin: Alignment.topRight,
                end: Alignment.topLeft,
                stops: [0.0, 1.0],
                tileMode: TileMode.clamp),
          ),
        ),
      ),
      body: Container(
        color: Colors.lightBlue[50],
        child: Center(
          child: ListView(
            shrinkWrap: true,
            children: <Widget>[
              IconButton(
                icon: Icon(_onOffIcon),
                color: Colors.lightGreen,
                onPressed: _pressedOnOff,
                iconSize: 120.0,
              ),
              Divider(),
              ListTile(
                  leading: Icon(Icons.settings),
                  title: Text('Effacer les données'),
                  onTap: clearFile),
              ListTile(
                  leading: Icon(Icons.help),
                  title: Text('Afficher les données locales'),
                  onTap: _printDataScreen),
            ],
          ),
        ),
      ),
    );
  }
}
