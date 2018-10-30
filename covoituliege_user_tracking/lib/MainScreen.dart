import 'package:flutter/material.dart';
import 'package:location/location.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'dart:async';
import 'dart:io';
import 'dart:convert';

import 'Cst.dart';
import 'UserInfo.dart';

class MainScreen extends StatefulWidget {
  final UserInfo user;
  MainScreen(this.user);
  @override
  _MainScreenState createState() => new _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  bool _givenConsent;
  bool _hasRefused;
  bool _stopped;
  Function _onPressed;
  IconData _icon;
  UserInfo user;

  Future<String> get _localPath async
  {
    final directory = await getApplicationDocumentsDirectory();
    return directory.path;
  }
  Future<File> get _localFile async {
    final path = await _localPath;
    return File('$path/data.json');
  }
  Future<File> writeInFile(String jsonString) async {
    final file = await _localFile;
    return file.writeAsString('$jsonString');
  }
  Future<String> readFile() async {
    try {
      final file = await _localFile;

      // Read the file
      String contents = await file.readAsString();

      return contents;
    } catch (e) {
      print("Error reading file!");
      return "Error";
    }
  }

  Future<void> newPos() async {
    Map<String, double> currentLocation = <String, double>{};
    var location = new Location();
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      currentLocation = await location.getLocation();
    } on PlatformException {
      currentLocation = null;
    }
    double latitude = currentLocation['latitude'];
    double longitude = currentLocation['longitude'];
    user.addData("latitude: " + latitude.toString() + ", longitude: " + longitude.toString());
    String jSon = json.encode(user);
    writeInFile(jSon);
  }

  _capturePos(i) {
    if (!_stopped) {
      newPos();
      Future.delayed(Duration(minutes: 2, seconds: 30), () {
        _capturePos(i + 1);
      });
    }
  }

  _accepted() {
    //TODO: tell to server consent has been given
    setState(() {
      _givenConsent = true;
    });
  }

  _refused() {
    setState(() {
      _hasRefused = true;
    });
  }

  _start() {
    _stopped = false;
    _capturePos(0);
    setState(() {
      _onPressed = _stop;
      _icon = Icons.stop;
    });
  }

  _stop() {
    _stopped = true;
    setState(() {
      readFile().then((s) => print(s));
      _onPressed = _start;
      _icon = Icons.play_arrow;
    });
  }

  @override
  void initState() {
    super.initState();
    user = widget.user;
    _givenConsent = false; //TODO ask to server if consent were already given
    _hasRefused = false;
    _onPressed = _start;
    _icon = Icons.play_arrow;
  }

  @override
  Widget build(BuildContext context) {
    if (_hasRefused) {
      return Scaffold(
        appBar: appBar,
        body: Container(
          color: Colors.red,
          child: Center(
            child: Text(
              'Désolé, pour des raisons techniques, nous ne pouvons pas' +
                  ' vous proposer le service sans utiliser votre position.',
              style: textStyle,
            ),
          ),
        ),
      );
    } else if (_givenConsent) {
      return Scaffold(
        appBar: appBar,
        body: Container(
          color: Colors.green,
          child: Center(
            child: IconButton(
              icon: Icon(_icon),
              onPressed: _onPressed,
              iconSize: 120.0,
            ),
          ),
        ),
      );
    } else {
      return Scaffold(
        appBar: appBar,
        body: Container(
          color: Colors.green,
          child: Center(
              child: ListView(
            shrinkWrap: true,
            children: <Widget>[
              Center(
                child: Text(
                  'TODO: consent text',
                  style: textStyle,
                ),
              ),
              Padding(
                padding: EdgeInsets.symmetric(horizontal: 75.0),
                child: RaisedButton(
                  child: Text(
                    'J\'accepte',
                    style: textStyle,
                  ),
                  onPressed: _accepted,
                ),
              ),
              Padding(
                padding: EdgeInsets.symmetric(horizontal: 75.0),
                child: RaisedButton(
                  child: Text(
                    'Je refuse',
                    style: textStyle,
                  ),
                  onPressed: _refused,
                ),
              ),
            ],
          )),
        ),
      );
    }
  }
}
