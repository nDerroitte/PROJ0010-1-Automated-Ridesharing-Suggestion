import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'dart:convert';
import 'UserInfo.dart';
import 'FileHandler.dart';
import 'Cst.dart';

/// This class represents a simple screen showing the buffered journeys.
class PrintDataScreen extends StatefulWidget {
  @override
  _PrintDataScreenState createState() => new _PrintDataScreenState();
}

class _PrintDataScreenState extends State<PrintDataScreen> {
  Text _data = Text(
    "",
    style: textStyle,
  );

  void _printData() async {
    String data = await readFile();
    List<String> dataUnit = data.split("data_splitter");
    StringBuffer toPrint = StringBuffer();
    for (String userInfo in dataUnit) {
      /// The split method returns an empty String if there is nothing after the last regex (argument),
      /// but the json.decode can't handle empty string
      if (userInfo == "") {
        break;
      }
      toPrint.write(UserInfo.toPrint(json.decode(userInfo)));
    }
    setState(() {
      _data = Text(
        toPrint.toString(),
        style: textStyle,
      );
    });
  }

  @override
  void initState() {
    super.initState();
    _printData();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: appBar,
      body: Container(
        color: backgroundColor,
        child: Center(
          child: ListView(
            shrinkWrap: true,
            children: <Widget>[
              _data,
            ],
          ),
        ),
      ),
    );
  }
}
