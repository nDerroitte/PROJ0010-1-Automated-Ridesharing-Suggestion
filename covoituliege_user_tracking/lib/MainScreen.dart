import 'package:flutter/material.dart';
import 'package:location/location.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
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
  bool _stopped;
  Function _pressedOnOff;
  Function _pressedDataButton;
  IconData _onOffIcon;
  int _capturePosIndex;
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
    formatted.writeln(splittedAtComma[0]); // username
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

  _capturePos(index) {
    if (!_stopped && index == _capturePosIndex) {
      _newPos();
      Future.delayed(Duration(minutes: 2, seconds: 30), () {
        _capturePos(index);
      });
    }
  }

  _start() {
    _stopped = false;
    _bufferedData.clear();
    _capturePos(_capturePosIndex);
    setState(() {
      _pressedOnOff = _stop;
      _onOffIcon = Icons.stop;
    });
  }

  _stop() {
    _stopped = true;
    _capturePosIndex += 1;
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
        "(tap again to reload)\n" + data.replaceAll("\\n", "\n"),
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
    _bufferedData = StringBuffer();
    _data = Text(
      'print data',
      style: textStyle,
    );
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
