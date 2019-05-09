import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'FileHandler.dart';
import 'Cst.dart';

/// Used for testing only.
/// This class represents a simple screen showing the buffered journeys.
class PrintAllDataScreen extends StatefulWidget {
  @override
  _PrintAllDataScreenState createState() => new _PrintAllDataScreenState();
}

class _PrintAllDataScreenState extends State<PrintAllDataScreen> {
  // Initialize _data with an empty Text to avoid null pointer exception
  // that can occur because of the asynchronous _printData() execution
  Text _data = Text(
    "",
    style: textStyle,
  );

  void _printData() async {
    String toPrint = await getAllReceivedPoints();
    setState(() {
      _data = Text(
        toPrint,
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
