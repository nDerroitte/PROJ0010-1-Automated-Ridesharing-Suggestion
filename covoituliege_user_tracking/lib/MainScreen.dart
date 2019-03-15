import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:flutter/widgets.dart';
import 'package:connectivity/connectivity.dart';
import 'package:intl/intl.dart';
import 'dart:async';
import 'dart:convert';

import 'Cst.dart';
import 'UserInfo.dart';
import 'FileHandler.dart';
import 'serverCommunication.dart';

/// This class represents the main screen of the application. It allows the user to launch the position capturing,
/// as well as printing and deleting the buffered data.
class MainScreen extends StatefulWidget {
  final UserInfo user;
  final ServerCommunication serverCommunication;

  MainScreen(this.user, this.serverCommunication);

  @override
  _MainScreenState createState() => new _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  Function _pressedOnOff;
  Function _pressedDataButton;
  IconData _onOffIcon;
  int _capturePosIndex;
  UserInfo _user;
  Text _data;
  Stream<ConnectivityResult> _onConnectivityChanged;
  StreamSubscription<ConnectivityResult> _connectivitySubscription;
  Stream<Position> _onLocationChanged;
  StreamSubscription<Position> _locationSubscription;
  int _nbSameLocationPoints;
  bool _waitingForWifi;
  ServerCommunication _serverCommunication;
  int _minDist = 1000;
  Map<String, dynamic> _lastPos;

  /// This function gets the current user's location and adds it in a buffer,
  /// in an easy-to-parse way.
  Future<void> _newPos() async {
    Position position = await Geolocator()
        .getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    double latitude = position.latitude;
    double longitude = position.longitude;

    String calendar = DateFormat('yyyy-MM-dd HH-mm-ss').format(DateTime.now());

    Map<String, dynamic> previousPos = _lastPos;
    if (previousPos != null &&
        await Geolocator().distanceBetween(double.parse(previousPos["lat"]),
                double.parse(previousPos["long"]), latitude, longitude) <
            _minDist) {
      _nbSameLocationPoints += 1;
      if (_nbSameLocationPoints > 7) {
        await _stop();
        _start();
      }
    } else {
      _nbSameLocationPoints = 0;
      _user.addData(calendar, latitude.toString(), longitude.toString());
      _lastPos = _user.getLastPos();
    }
  }

  /// This function launches periodically the newPos function. The index handle the situation
  /// in which the user presses multiple times on the start button : only one instance can be active simultaneously.
  _capturePos(index) {
    if (index == _capturePosIndex) {
      _newPos();
      Future.delayed(Duration(minutes: 2, seconds: 30), () {
        _capturePos(index);
      });
    }
  }

  _onLocationEvent(Position pos) async {
    Position position = await Geolocator()
        .getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    double latitude = position.latitude;
    double longitude = position.longitude;
    if (_lastPos == null || await Geolocator().distanceBetween(double.parse(_lastPos["lat"]),
        double.parse(_lastPos["long"]), latitude, longitude) >=
        _minDist) { /// Only if this is not a false trigger
      _nbSameLocationPoints = 0;
      _locationSubscription.pause();
      _capturePos(_capturePosIndex);
    }
  }

  /// This function clear the buffer and starts a new capturePos process.
  /// It also updates the button so that it's now a stop button.
  /// It is called when the user taps on the start button.
  void _start() async {
    if (_locationSubscription == null) {
        _locationSubscription = _onLocationChanged.listen(_onLocationEvent);
    } else {
      _locationSubscription.resume();
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
    _capturePosIndex += 1;
    /// Can happen if the stop button is pressed before the location has changed at least once
    if (!_locationSubscription.isPaused) {
      _locationSubscription.pause();
    }
    String jSon = json.encode(_user);
    await writeInFile(jSon);
    await _printData();
    _user.clear();
    if (!_waitingForWifi) {
      _waitingForWifi = true;
      _sendPoints();
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
    if (connectivity == ConnectivityResult.wifi &&
        await _serverCommunication.sendPoints(await readFile())) {
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

  /// This function is called when the user taps on the print data button.
  /// It prints the points that are in the application local file.
  _printData() async {
    String data = await readFile();
    List<String> dataUnit = data.split("data_splitter");
    StringBuffer toPrint = StringBuffer();
    for (String userInfo in dataUnit) {
      /// The split method returns an empty String if there is nothing after the last regex (argument)
      if (userInfo == "") {
        break;
      }
      toPrint.write(UserInfo.toPrint(json.decode(userInfo)));
    }
    setState(() {
      _data = Text(
        "(appuyer à nouveau pour recharger)\n" + toPrint.toString(),
        style: textStyle,
      );
    });
  }

  @override
  void initState() {
    super.initState();
    _user = widget.user;
    _pressedOnOff = _start;
    _pressedDataButton = _printData;
    _onOffIcon = Icons.play_arrow;
    _capturePosIndex = 0;
    _data = Text(
      'afficher les données',
      style: textStyle,
    );
    _onConnectivityChanged = Connectivity().onConnectivityChanged.skip(1);
    _onLocationChanged = Geolocator()
        .getPositionStream(LocationOptions(
            distanceFilter: _minDist, accuracy: LocationAccuracy.high))
        .skip(1);
    _waitingForWifi = false;
    _serverCommunication = widget.serverCommunication;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: appBar,
      body: Container(
        color: Colors.green,
        child: Center(
          child: ListView(
            shrinkWrap: true,
            children: <Widget>[
              RaisedButton(
                child: Text(
                  "Effacer les données",
                  style: textStyle,
                ),
                onPressed: clearFile,
              ),
              IconButton(
                icon: Icon(_onOffIcon),
                onPressed: _pressedOnOff,
                iconSize: 120.0,
              ),
              RaisedButton(
                child: _data,
                onPressed: _pressedDataButton,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
