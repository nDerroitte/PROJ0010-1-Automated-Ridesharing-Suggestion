import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'dart:async';

import 'Cst.dart';

class MainScreen extends StatefulWidget {
  @override
  _MainScreenState createState() => new _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  bool _givenConsent;
  bool _hasRefused;
  bool _stopped;
  Function _onPressed;
  IconData _icon;
  final Geolocator locator = Geolocator();

  static const test = 'a';

  _capturePos(i) {
    //TODO capture position
    if (_stopped) {
      Future.delayed(Duration(seconds: 0), () {
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
    //TODO launch background function to capture position
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
      _onPressed = _start;
      _icon = Icons.play_arrow;
    });
  }

  @override
  void initState() {
    super.initState();
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
