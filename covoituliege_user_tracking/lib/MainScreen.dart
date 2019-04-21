import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:connectivity/connectivity.dart';
import 'package:intl/intl.dart';
import 'package:geofencing/geofencing.dart';
import 'package:latlong/latlong.dart';
import 'package:flutter/services.dart';
import 'dart:async';
import 'dart:ui';

import 'Cst.dart';
import 'UserInfo.dart';
import 'FileHandler.dart';
import 'serverCommunication.dart';
import 'PrintDataScreen.dart';

/// This class represents the main screen of the application. It allows the user
/// to launch the position tracking as well as printing and deleting buffered data.
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

  /// Function called periodically by the background location listener,
  /// it receives a list of locations (which are supposed to be ordered from
  /// oldest to newest), analyzes them and store relevant data to be sent later.
  static Future<void> newPointsBatchCallback(
      List<TimedLocation> locations) async {
    /// These variables are used to avoid accessing files multiple times when
    /// not necessary
    String lastCalendar;
    double lastLat;
    double lastLon;
    List<String> lastTimedLoc = await getLastTimedLoc();
    if (lastTimedLoc == null) {
      if (locations.length > 0) {
        lastCalendar = locations[0].calendar;
        lastLat = double.parse(locations[1].latitude);
        lastLon = double.parse(locations[2].longitude);
        await storePoint(
            lastCalendar, locations[1].latitude, locations[2].longitude);
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
      if (await hasJourneyStarted()) {
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
          await storeGeofenceCenter(loc.calendar, loc.latitude, loc.longitude);
          lastCalendar = loc.calendar;
          lastLat = double.parse(loc.latitude);
          lastLon = double.parse(loc.longitude);
        } else if (distance > minDistanceNewJourney) {
          await storePoint(loc.calendar, loc.latitude, loc.longitude);
          lastCalendar = loc.calendar;
          lastLat = double.parse(loc.latitude);
          lastLon = double.parse(loc.longitude);
        }
      } else if (distance > minDistanceNewJourney) {
        await storePoint(lastCalendar, lastLat.toString(), lastLon.toString());
        await storePoint(loc.calendar, loc.latitude, loc.longitude);
        lastCalendar = loc.calendar;
        lastLat = double.parse(loc.latitude);
        lastLon = double.parse(loc.longitude);
      }
    }
  }

  /// This function clear the buffer and starts a new position tracking process.
  /// It also updates the button so that it's now a stop button.
  /// It is called when the user taps on the start button.
  _start() async {
    clearBuffer();
    //TODO implement location batches on IOs side
    registerLocListener(newPointsBatchCallback, timeIntervalBetweenPoints,
        maxWaitTimeForUpdates, minDistanceBetweenPoints);
    await startedLocListener();
    setState(() {
      _pressedOnOff = _stop;
      _onOffIcon = Icons.stop;
    });
  }

  /// This function is called when the user taps on the stop button.
  /// It creates a journey from the currently buffered points,
  /// stops the position tracking process and
  /// updates the button so that it becomes a start button.
  _stop() async {
    writeJourneyFromBufferedPoints(_user);
    removeLocListener(newPointsBatchCallback);
    await stoppedLocListener();

    setState(() {
      _pressedOnOff = _start;
      _onOffIcon = Icons.play_arrow;
    });
  }

  /// Sends buffered journeys to the server. Called when the user connects.
  _sendJourneys() async {
    ConnectivityResult connectivity = await Connectivity().checkConnectivity();

    /// We try to send the data, if it fails (likely because wifi is not available),
    /// we wait for the state of the connectivity to change and we retry.
    /// If a data unit was collected anonymously, it is filled with the current username.
    if (connectivity == ConnectivityResult.wifi &&
        await _serverCommunication.sendJourneys((await readFile()).replaceAll(
            RegExp("\"UserId\":\"\""),
            "\"UserId\":\"" + _user.getId() + "\""))) {
      clearFile();
      _waitingForWifi = false;
    } else {
      _connectivitySubscription =
          _onConnectivityChanged.listen((ConnectivityResult result) {
        _connectivitySubscription.cancel();
        _sendJourneys();
      });
    }
  }

  /// Push the screen showing buffered journeys.
  _printDataScreen() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => PrintDataScreen()),
    );
  }

  /// Wrapper checking whether the app should show first a play or a stop button
  /// Needed because the app can be killed at anytime when in background
  _playOrStop() async {
    if (await isLocListenerStarted()) {
      _pressedOnOff = _stop;
      _onOffIcon = Icons.stop;
    } else {
      _pressedOnOff = _start;
      _onOffIcon = Icons.play_arrow;
    }
    setState(() {});
  }

  @override
  void initState() {
    super.initState();
    _user = widget.user;
    _anonymous = widget.anonymous;
    if (!_anonymous && !_waitingForWifi) {
      _sendJourneys();
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
      appBar: appBar,
      body: Container(
        color: backgroundColor,
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
