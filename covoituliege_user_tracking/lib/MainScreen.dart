import 'package:flutter/material.dart';
//import 'package:geolocator/geolocator.dart';
import 'package:flutter/widgets.dart';
import 'package:connectivity/connectivity.dart';
import 'package:intl/intl.dart';
import 'package:geofencing/geofencing.dart';
import 'dart:async';

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
    /// 1000 geofences per second, il would take more that 500 millions years to overflow
    DateTime curTime = DateTime.now();
    String calendar = DateFormat('yyyy-MM-dd HH-mm-ss').format(DateTime.now());
    String expectedId = await getLastGeofenceId();
    print("ids in ids : " + ids.toString());
    if (ids.contains(expectedId)) {
      String prevCalendar = await getLastCalendar();
      if (prevCalendar != null && curTime
          .subtract(minPauseTimeBetweenJourneys)
          .isAfter(DateFormat('yyyy-MM-dd HH-mm-ss').parse(prevCalendar))) {
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

  /// This function clear the buffer and starts a new capturePos process.
  /// It also updates the button so that it's now a stop button.
  /// It is called when the user taps on the start button.
  /// //TODO rewrite doc
  void _start() async {
    /*Position position = await Geolocator()
        .getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    double latitude = position.latitude;
    double longitude = position.longitude;
    String calendar = DateFormat('yyyy-MM-dd HH-mm-ss').format(DateTime.now());
    clearBuffer();
    storePoint(calendar, latitude.toString(), longitude.toString());*/
    clearBuffer();

    GeofenceRegion newGeofence = GeofenceRegion('0', 0, 0,
        distBetweenPoints, <GeofenceEvent>[GeofenceEvent.exit],
        androidSettings: androidSettings);

    /// No volatile state can be stored in background because the application
    /// may get killed at anytime (and reopened when needed)
    storeGeofenceById('0');

    await GeofencingManager.initialize();
    await GeofencingManager.registerGeofence(
        newGeofence, outOfGeofenceCallback);
    setState(() {
      _pressedOnOff = _stop;
      _onOffIcon = Icons.stop;
    });
  }

  /// This function is called when the user taps on the stop button.
  /// It saves the currently buffered data in a file (but it should send it if possible, this is still to do),
  /// and updates the button so that it becomes a start button.
  _stop() async {
    /*Position position = await Geolocator()
        .getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    double latitude = position.latitude;
    double longitude = position.longitude;

    String calendar = DateFormat('yyyy-MM-dd HH-mm-ss').format(DateTime.now());
    storePoint(calendar, latitude.toString(), longitude.toString());*/
    writeJourneyFromBufferedPoints(_user);

    String lastGeofence = await getLastGeofenceId();
    if (lastGeofence != null) {
      GeofencingManager.removeGeofenceById(lastGeofence);
      clearGeofence();
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
    if (await getLastGeofenceId() == null) {
      _pressedOnOff = _start;
      _onOffIcon = Icons.play_arrow;
    } else {
      _pressedOnOff = _stop;
      _onOffIcon = Icons.stop;
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
                //begin: const FractionalOffset(0.0, 0.0),
                //end: const FractionalOffset(1.0, 0.0),
                stops: [0.0, 1.0],
                tileMode: TileMode.clamp),
          ),
        ),
      ),
      body: Container(
        color: Colors.lightBlue[50],
        //width: 400.0,
        //height: 200.0,
        //decoration: new BoxDecoration(
        //  image: new DecorationImage(image: new AssetImage("localisation2.png"), fit: BoxFit.cover,),
        //),
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
                  //child: _data,
                  //subtitle : _data,
                  //onTap: _pressedDataButton
                  onTap: _printDataScreen),

/*
              RaisedButton(
                textColor: Colors.white,
                color: Colors.red,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30.0),
                ),

                child: Text(
                  "Effacer les données",
                  style: textStyle,
                ),
                onPressed: clearFile,
              ),

             RaisedButton(
                textColor: Colors.white,
                color: Colors.orange,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30.0),
                ),
                child: _data,
                onPressed: _pressedDataButton,
              ),
*/
            ],
          ),
        ),
      ),
    );
  }
}
