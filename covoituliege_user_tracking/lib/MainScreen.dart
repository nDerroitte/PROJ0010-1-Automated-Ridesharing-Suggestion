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
  bool _anonymous;

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
      _lastPos = _user.addData(calendar, latitude.toString(), longitude.toString());
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
    Position position = await Geolocator()
        .getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    double latitude = position.latitude;
    double longitude = position.longitude;

    String calendar = DateFormat('yyyy-MM-dd HH-mm-ss').format(DateTime.now());

    _lastPos = _user.addData(calendar, latitude.toString(), longitude.toString());

    _capturePosIndex += 1;
    /// Can happen if the stop button is pressed before the location has changed at least once
    if (!_locationSubscription.isPaused) {
      _locationSubscription.pause();
    }
    String jSon = json.encode(_user);
    await writeInFile(jSon);
    await _printData();
    _user.clear();
    if (!_waitingForWifi && !_anonymous) {
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
        "(Appuyer pour recharger)\n" + toPrint.toString(),
        style: textStyle,
      );
    });
  }

  _printDataScreen() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => PrintDataScreen()),
    );
  }

  @override
  void initState() {
    super.initState();
    _user = widget.user;
    _anonymous = widget.anonymous;
    _pressedOnOff = _start;
    _pressedDataButton = _printData;
    _onOffIcon = Icons.play_arrow;
    _capturePosIndex = 0;
    /*_data = Text(
      'Afficher les données',
      style: textStyle,
    );*/
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
      appBar: AppBar(
        title: Center(child: Text('Ugo              ')), //LET THE SPACE, IT IS FOR CENTERING
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
        ),),
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
              /*
              Divider(),
              ListTile(
                  leading: Icon(Icons.settings),
                  title: Text('Effacer les données'),
                  onTap: clearFile ),
              ListTile(
                  leading: Icon(Icons.help),
                  title: Text('Afficher les données'),
                  //child: _data,
                  //subtitle : _data,
                  //onTap: _pressedDataButton
                  onTap: _printDataScreen
              ),
              */


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

            ],
          ),
        ),
      ),
    );
  }
}
