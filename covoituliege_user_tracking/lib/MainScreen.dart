import 'package:flutter/material.dart';
import 'package:location/location.dart';
import 'package:geolocator/geolocator.dart';
import 'package:flutter/services.dart';
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
  Stream<Map<String, double>> _onLocationChanged;
  StreamSubscription<Map<String, double>> _locationSubscription;
  int _nbSameLocationPoints;
  bool _waitingForWifi;
  ServerCommunication _serverCommunication;

  /// This function gets the current user's location and adds it in a buffer,
  /// in an easy-to-parse way.
  Future<void> _newPos() async {
    Map<String, double> currentLocation = <String, double>{};
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      currentLocation = await Location().getLocation();
    } on PlatformException {
      /// We only skip one point, it doesn't hurt as long as this is rare
      return;
    }
    double latitude = currentLocation['latitude'];
    double longitude = currentLocation['longitude'];
    String calendar = DateFormat('yyyy-MM-dd HH-mm-ss').format(DateTime.now());

    Map<String, dynamic> lastPos = _user.getLastPos();
    if (lastPos != null &&
        await Geolocator().distanceBetween(double.parse(lastPos["lat"]),
                double.parse(lastPos["long"]), latitude, longitude) <
            1000) {
      _nbSameLocationPoints += 1;
      if (_nbSameLocationPoints > 7) {
        await _stop();
        _start();
      }
    } else {
      _nbSameLocationPoints = 0;
      _user.addData(calendar, latitude.toString(), longitude.toString());
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

  /// This function clear the buffer and starts a new capturePos process.
  /// It also updates the button so that it's now a stop button.
  /// It is called when the user taps on the start button.
  void _start() {
    _locationSubscription =
        _onLocationChanged.listen((Map<String, double> result) {
      _nbSameLocationPoints = 0;
      _locationSubscription.cancel();
      _capturePos(_capturePosIndex);
    });
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
    _locationSubscription.cancel();
    String jSon = json.encode(_user);
    await writeInFile(jSon);
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
    _onLocationChanged = Location()
        .onLocationChanged(); //TODO check bug callback called every second
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
