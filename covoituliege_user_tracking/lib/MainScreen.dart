import 'package:flutter/material.dart';
import 'package:location/location.dart';
import 'package:geolocator/geolocator.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:connectivity/connectivity.dart';
import 'dart:async';
import 'dart:convert';

import 'Cst.dart';
import 'UserInfo.dart';
import 'FileHandler.dart';
import 'serverCommunication.dart';

/// This class represents the main screen of the application. It allows the user to launch the position capturing,
/// as well as to print the points currently in the file to have an idea of the kind of data we collect.
/// [The behaviour is likely to change].
class MainScreen extends StatefulWidget {
  final UserInfo user;
  MainScreen(this.user);
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
  Location _location = new Location();
  StreamSubscription<Map<String, double>> _locationSubscription;
  int _nbSameLocationPoints;
  bool _waitingForWifi;

  /// This function get the current user's location and add it in a buffer,
  /// in an easy-to-parse way. This behaviour should change as using a RAM buffer causes problems
  /// if the application is closed (we did not find any callback to handle this event).
  Future<void> _newPos() async {
    Map<String, double> currentLocation = <String, double>{};
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      currentLocation = await _location.getLocation();
    } on PlatformException {
      /// We only skip one point, it doesn't hurt as long as this is rare
      return;
    }
    double latitude = currentLocation['latitude'];
    double longitude = currentLocation['longitude'];
    String dateTime = DateTime.now()
        .toString()
        .replaceFirst(":", "h")
        .replaceFirst(":", "m")
        .replaceFirst(".", "s");
    dateTime = dateTime.substring(0, dateTime.indexOf("s") + 1);
    List<String> dateAndTime = dateTime.split(" ");

    Map<String, dynamic> lastPos = _user.getLastPos();
    if (lastPos != null &&
        await Geolocator().distanceBetween(double.parse(lastPos["lat"]),
                double.parse(lastPos["long"]), latitude, longitude) <
            1000) {
      if (_nbSameLocationPoints > 2) {
        _stop();
        _start();
      } else {
        _nbSameLocationPoints += 1;
      }
    } else {
      _nbSameLocationPoints = 0;
      _user.addData(dateAndTime[0], dateAndTime[1], latitude.toString(),
          longitude.toString());
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
  _start() {
    _locationSubscription =
        _location.onLocationChanged().listen((Map<String, double> result) {
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
  _stop() {
    _capturePosIndex += 1;
    _locationSubscription.cancel();
    String jSon = json.encode(_user);
    writeInFile(jSon);
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
    var connectivity = await Connectivity().checkConnectivity();
    if (connectivity == ConnectivityResult.wifi && await sendPoints(await readFile())) {
      _waitingForWifi = false;
    } else {
      Connectivity().onConnectivityChanged.listen((ConnectivityResult result) {
        _sendPoints();
      });
    }
  }

  /// This function is called when the user taps on the print data button.
  /// It prints the points that are in the application local file.
  _printData() async {
    String data = UserInfo.toPrint(json.decode(await readFile()));
    setState(() {
      _data = Text(
        "(appuyer à nouveau pour recharger)\n" + data,
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
    _waitingForWifi = false;
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
