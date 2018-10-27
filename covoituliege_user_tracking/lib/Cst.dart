import 'package:flutter/material.dart';

const textStyle = TextStyle(fontSize: 24.0, color: Colors.black);
const warningStyle = TextStyle(fontSize: 24.0, color: Colors.red);

final _appBarColor = Colors.green;

final appBar = AppBar(
  title: Text(
    'User Tracking CovoitULiège',
    style: textStyle,
  ),
  centerTitle: true,
  backgroundColor: _appBarColor,
);
