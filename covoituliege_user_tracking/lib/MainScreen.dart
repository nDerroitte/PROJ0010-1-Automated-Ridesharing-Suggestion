import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:connectivity/connectivity.dart';
import 'package:intl/intl.dart';
import 'package:geofencing/geofencing.dart';
import 'package:latlong/latlong.dart';
import 'dart:async';

import 'Cst.dart';
import 'FileHandler.dart';
import 'serverCommunication.dart';
import 'PrintDataScreen.dart';
import 'PrintAllDataScreen.dart';

/// This class represents the main screen of the application. It allows the user
/// to launch the position tracking as well as printing and deleting buffered data.
class MainScreen extends StatefulWidget {
  final String username;

  MainScreen(this.username);

  @override
  _MainScreenState createState() => new _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  Function _pressedOnOff;
  IconData _onOffIcon;

  /// Function called periodically by the background location listener,
  /// it receives a list of locations (which are supposed to be ordered from
  /// oldest to newest), analyzes them and store relevant data to be sent later.
  static Future<void> newPointsBatchCallback(
      List<TimedLocation> locations) async {
    /// These variables are used to avoid accessing files multiple times when
    /// not necessary
    for (TimedLocation loc in locations) {
      storeReceivedPoint(loc.calendar, loc.latitude, loc.longitude);
    }
    String lastCalendar;
    double lastLat;
    double lastLon;
    List<String> lastTimedLoc = await getLastTimedLoc();
    if (lastTimedLoc == null) {
      if (locations.length > 0) {
        lastCalendar = locations[0].calendar;
        lastLat = double.parse(locations[0].latitude);
        lastLon = double.parse(locations[0].longitude);
        await storePoint(
            lastCalendar, locations[0].latitude, locations[0].longitude);
      }
    } else {
      lastCalendar = lastTimedLoc[0];
      lastLat = double.parse(lastTimedLoc[1]);
      lastLon = double.parse(lastTimedLoc[2]);
    }
    String newCalendar;
    double distance;
    bool inJourney = await hasJourneyStarted();

    for (TimedLocation loc in locations) {
      distance = DistanceVincenty().distance(LatLng(lastLat, lastLon),
          LatLng(double.parse(loc.latitude), double.parse(loc.longitude)));
      if (inJourney) {
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
          await writeJourneyFromBufferedPoints();
          await storeGeofenceCenter(loc.calendar, loc.latitude, loc.longitude);
          lastCalendar = loc.calendar;
          lastLat = double.parse(loc.latitude);
          lastLon = double.parse(loc.longitude);
          inJourney = false;
        } else if (distance > minDistanceNewJourney) {
          await storePoint(loc.calendar, loc.latitude, loc.longitude);
          lastCalendar = loc.calendar;
          lastLat = double.parse(loc.latitude);
          lastLon = double.parse(loc.longitude);
        }
      } else if (distance > minDistanceNewJourney) {
        newCalendar = DateFormat(dateFormat).format(DateFormat(dateFormat)
            .parse(loc.calendar)
            .subtract(Duration(minutes: 2, seconds: 30)));
        await storePoint(newCalendar, lastLat.toString(), lastLon.toString());
        await storePoint(loc.calendar, loc.latitude, loc.longitude);
        lastCalendar = loc.calendar;
        lastLat = double.parse(loc.latitude);
        lastLon = double.parse(loc.longitude);
        inJourney = true;
      }
    }
    _sendJourneys();
  }

  /// This function clear the buffer and starts a new position tracking process.
  /// It also updates the button so that it's now a stop button.
  /// It is called when the user taps on the start button.
  _start() async {
    await clearBuffers();
    //TODO implement location batches on IOs side
    registerLocListener(newPointsBatchCallback, timeIntervalBetweenPoints,
        maxWaitTimeForUpdates);
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
    writeJourneyFromBufferedPoints();
    removeLocListener(newPointsBatchCallback);
    await stoppedLocListener();

    setState(() {
      _pressedOnOff = _start;
      _onOffIcon = Icons.play_arrow;
    });
  }

  /// Sends buffered journeys to the server. Called when the user connects.
  static _sendJourneys() async {
    if (!(await isAnonymous())) {
      ConnectivityResult connectivity = await Connectivity().checkConnectivity();

      /// We try to send the data, if it fails (likely because wifi is not available),
      /// we keep the data in a file and we will retry later.
      /// If a data unit was collected anonymously, it is filled with the current username.
      if (connectivity == ConnectivityResult.wifi &&
          await sendJourneys((await readFile()).replaceAll(
              RegExp("\"UserId\":\"\""),
              "\"UserId\":\"" + await getUserId() + "\""))) {
        clearFile();
      }
    }
  }

  /// Clear the stored data related to debug / test
  _clearDebugData() async {
    await clearFile();
    await clearReceivedPoints();
  }

  /// Push the screen showing buffered journeys.
  _printDataScreen() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => PrintDataScreen()),
    );
  }

  /// Used for testing only.
  /// Push the screen showing all received points.
  _printAllDataScreen() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => PrintAllDataScreen()),
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

  _sendJourneyWithIdWrapper(String username) async {
    await storeUserId(username);
    await _sendJourneys();
  }

  @override
  void initState() {
    super.initState();
    String _username = widget.username;
    _sendJourneyWithIdWrapper(_username);
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
                onTap: _clearDebugData,
              ),
              ListTile(
                leading: Icon(Icons.help),
                title: Text('Afficher les données locales'),
                onTap: _printDataScreen,
              ),
              ListTile(
                leading: Icon(Icons.help),
                title: Text('Afficher tout les points reçus'),
                onTap: _printAllDataScreen,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
