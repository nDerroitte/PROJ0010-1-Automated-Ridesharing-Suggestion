import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:connectivity/connectivity.dart';
import 'package:intl/intl.dart';
import 'package:geofencing/geofencing.dart';
import 'package:latlong/latlong.dart';
import 'dart:async';

import 'Cst.dart';
import 'UserInfo.dart';
import 'FileHandler.dart';
import 'serverCommunication.dart';
import 'PrintDataScreen.dart';
import 'PrintAllDataScreen.dart';

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
    /*TODO remove (later)
    List<TimedLocation> test1 = [TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 15-01-24"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 16-03-45"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 16-06-38"),
    TimedLocation.fromExplicit("50.0000024", "50.00000012", "2019-04-23 16-09-21")];
    List<TimedLocation> test2 = [TimedLocation.fromExplicit("45.0", "50.0", "2019-04-23 16-13-18"),
    TimedLocation.fromExplicit("40.0", "50.0", "2019-04-23 16-16-45"),
    TimedLocation.fromExplicit("35.0", "50.0", "2019-04-23 16-19-26")];
    List<TimedLocation> test3 = [TimedLocation.fromExplicit("35.0", "50.0", "2019-04-23 16-22-47"),
    TimedLocation.fromExplicit("35.0", "50.0", "2019-04-23 16-25-37"),
    TimedLocation.fromExplicit("35.0", "50.0", "2019-04-23 16-28-19")];
    List<TimedLocation> test4 = [TimedLocation.fromExplicit("35.0", "50.0", "2019-04-23 16-31-36"),
    TimedLocation.fromExplicit("35.0", "50.0", "2019-04-23 16-34-18"),
    TimedLocation.fromExplicit("35.0", "50.0", "2019-04-23 16-37-01"),
    TimedLocation.fromExplicit("35.0", "50.0", "2019-04-23 16-40-08")];
    List<TimedLocation> test5 = [TimedLocation.fromExplicit("35.0", "50.0", "2019-04-23 16-43-51"),
    TimedLocation.fromExplicit("38.0", "50.0", "2019-04-23 16-46-12"),
    TimedLocation.fromExplicit("42.0", "50.0", "2019-04-23 16-49-51"),
    TimedLocation.fromExplicit("44.5", "50.0", "2019-04-23 16-53-08")];
    List<TimedLocation> test6 = [TimedLocation.fromExplicit("46.8521", "50.0", "2019-04-23 16-57-51")];
    List<TimedLocation> test7 = [TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-01-00")];
    List<TimedLocation> test8 = [TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-03-00"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-06-00"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-09-00"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-12-00"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-15-00"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-18-00"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-21-00"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-24-00"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-27-00"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-30-00"),
    TimedLocation.fromExplicit("50.0", "50.0", "2019-04-23 17-33-00")];
    await newPointsBatchCallback(test1);
    await newPointsBatchCallback(test2);
    await newPointsBatchCallback(test3);
    await newPointsBatchCallback(test4);
    await newPointsBatchCallback(test5);
    await newPointsBatchCallback(test6);
    await newPointsBatchCallback(test7);
    await newPointsBatchCallback(test8);*/

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

  @override
  void initState() {
    super.initState();
    _user = widget.user;
    storeUserId(_user.getId());
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
                onTap: clearFile,
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
