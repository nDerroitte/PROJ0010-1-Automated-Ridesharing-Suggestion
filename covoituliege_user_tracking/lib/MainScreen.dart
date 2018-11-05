import 'package:flutter/material.dart';
import 'package:location/location.dart';
import 'package:flutter/services.dart';
import 'dart:async';
import 'dart:convert';

import 'Cst.dart';
import 'UserInfo.dart';
import 'FileHandler.dart';

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
  Function _pressedOnOff;
  IconData _onOffIcon;
  UserInfo _user;
  StringBuffer _bufferedData;
  Text _data;

  Future<String> _readFile() async {
    String data = await readFile();
    data = data
        .replaceAll("{", "")
        .replaceAll("}", "")
        .replaceAll('"', "")
        .replaceFirst("Data:", "");
    StringBuffer formatted = StringBuffer();

    List<String> splittedAtComma = data.split(",");
    formatted.writeln(splittedAtComma[0]); // splittedAtComma[0] is the username
    formatted.write(splittedAtComma[1]);

    return formatted.toString();
  }

  Future<void> _newPos() async {
    Map<String, double> currentLocation = <String, double>{};
    var location = new Location();
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      currentLocation = await location.getLocation();
    } on PlatformException {
      /// We only skip one point, it doesn't hurt as long as this is rare
      _bufferedData.writeln("-1: A PlatformException occured");
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
    _bufferedData.writeln("\nPoint:");
    _bufferedData.writeln("Date = " + dateAndTime[0]);
    _bufferedData.writeln("Time = " + dateAndTime[1]);
    _bufferedData.writeln("Latitude = " + latitude.toString());
    _bufferedData.writeln("Longitude = " + longitude.toString());
  }

  _capturePos() {
    if (!_stopped) {
      _newPos();
      Future.delayed(Duration(minutes: 2, seconds: 30), () {
        _capturePos();
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
    _bufferedData.clear();
    _capturePos();
    setState(() {
      _pressedOnOff = _stop;
      _onOffIcon = Icons.stop;
    });
  }

  _stop() {
    _stopped = true;
    _user.addData(_bufferedData.toString());
    String jSon = json.encode(_user);
    writeInFile(jSon);
    setState(() {
      _pressedOnOff = _start;
      _onOffIcon = Icons.play_arrow;
    });
  }

  _printData() async {
    String data = await _readFile();
    setState(() {
      _data = Text(
        data.replaceAll("\\n", "\n"),
        style: textStyle,
      );
    });
  }

  @override
  void initState() {
    super.initState();
    _user = widget.user;
    _givenConsent = false; //TODO ask to server if consent were already given
    _hasRefused = false;
    _pressedOnOff = _start;
    _onOffIcon = Icons.play_arrow;
    _bufferedData = StringBuffer();
    _data = Text(
      'print data',
      style: textStyle,
    );
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
                  onPressed: _printData,
                ),
              ],
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
