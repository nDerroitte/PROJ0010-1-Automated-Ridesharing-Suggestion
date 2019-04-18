import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'dart:convert';
import 'UserInfo.dart';
import 'FileHandler.dart';

/// This class represents the main screen of the application. It allows the user to launch the position capturing,
/// as well as printing and deleting the buffered data.
class PrintDataScreen extends StatefulWidget {
  @override
  _PrintDataScreenState createState() => new _PrintDataScreenState();
}

class _PrintDataScreenState extends State<PrintDataScreen> {
  List list = List();
  var isLoading = false;
  Function generateData;
  Text _data;


  void printData() async {
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
        style: TextStyle(fontSize: 18.0),
      );
    });
  }

  @override
  void initState() {
    super.initState();
    printData();
   _data = Text(
      '',
      style: TextStyle(fontSize: 18.0),
    );
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
                stops: [0.0, 1.0],
                tileMode: TileMode.clamp),
          ),
        ),),



      body: Container(
        color: Colors.lightBlue[50],
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
