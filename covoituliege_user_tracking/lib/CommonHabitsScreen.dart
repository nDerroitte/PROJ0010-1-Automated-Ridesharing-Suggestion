import 'package:flutter/material.dart';
import 'serverCommunication.dart';
import 'package:intl/intl.dart';

import 'Cst.dart';
import 'TextInput.dart';

/// This class represents the delete common habits screen of the application.
/// It allows the user to view the common habits he has with another user.
class CommonHabitsScreen extends StatefulWidget {
  final String connectedUser;

  CommonHabitsScreen(this.connectedUser);

  @override
  _CommonHabitsScreenState createState() => new _CommonHabitsScreenState();
}

class _CommonHabitsScreenState extends State<CommonHabitsScreen> {
  Widget _newResearchMode;
  Widget _curMode;

  String _connectedUser;
  TextEditingController _otherUserController = TextEditingController();

  /// Loads the common habits (json format) from the server, parse them and show
  /// them to the user. The floating action button (bottom right)
  /// can be tapped to make a new research.
  _loadHabits() async {
    List<dynamic> decodedJson =
        await compareUsers(_connectedUser, _otherUserController.text);
    StringBuffer data = StringBuffer();
    data.writeln("habitudes communes avec " + _otherUserController.text);
    data.writeln();
    Map<String, dynamic> location;
    for (Map<String, dynamic> habit in decodedJson) {
      data.writeln("départ :");
      location = habit["firstLocation"];
      data.write("    latitude : ");
      data.writeln(location["x"]);
      data.write("    longitude : ");
      data.writeln(location["y"]);

      data.writeln("arrivée :");
      location = habit["lastLocation"];
      data.write("    latitude : ");
      data.writeln(location["x"]);
      data.write("    longitude : ");
      data.writeln(location["y"]);

      data.write("période en jours : ");
      data.writeln(habit["period"]);

      data.write("première occurence : ");
      int millisSince1970 = habit["offset"];
      data.writeln(DateFormat('yyyy-MM-dd HH-mm-ss').format(
          DateTime.fromMillisecondsSinceEpoch(millisSince1970, isUtc: true).toLocal()));

      data.write("heure d'arrivée : ");
      millisSince1970 = habit["arrival_time"];
      data.writeln(DateFormat('yyyy-MM-dd HH-mm-ss').format(
          DateTime.fromMillisecondsSinceEpoch(millisSince1970, isUtc: true).toLocal()));

      data.write("probabilité de l'habitude : ");
      data.writeln(habit["reliability"]);

      data.write("nombre d'occurences : ");
      data.writeln(habit["nbPoints"]);

      data.write("écart-type : ");
      data.writeln(habit["standardDeviation"]);

      data.writeln();
    }
    setState(() {
      _curMode = _getPrintDataMode(data.toString());
    });
  }

  /// Shows the InputText to enable the user to make a new research.
  /// The research is launched when the user validates its input (i.e., tap on
  /// the validation button of the keyboard).
  _newResearch() {
    setState(() {
      _curMode = _newResearchMode;
    });
  }

  /// Returns a template scaffold body constructed from the listViewContent passed
  /// in argument.
  Widget _getBody(List<Widget> listViewContent) {
    return Container(
      color: backgroundColor,
      child: Center(
        child: Padding(
          padding: EdgeInsets.symmetric(
            horizontal: 30.0,
          ),
          child: ListView(
            shrinkWrap: true,
            children: listViewContent,
          ),
        ),
      ),
    );
  }

  /// Returns a template scaffold constructed from the data passed in argument.
  Widget _getPrintDataMode(String data) {
    List<Widget> printDataModeListViewContent = <Widget>[
      Text(
        "Connecté en tant que " + _connectedUser,
        style: textStyle,
      ),
      Text(
        data,
        style: textStyle,
      ),
    ];
    return Scaffold(
      appBar: appBar,
      body: _getBody(printDataModeListViewContent),
      floatingActionButton: FloatingActionButton(
        onPressed: _newResearch,
        child: Icon(Icons.autorenew),
      ),
    );
  }

  @override
  void initState() {
    super.initState();
    _connectedUser = widget.connectedUser;
    Text connectedAs = Text(
      "Connecté en tant que " + _connectedUser,
      style: textStyle,
    );
    InputText otherUser = InputText(
      labelText: 'Comparer avec ...',
      controller: _otherUserController,
      color: backgroundColor,
      onEditingComplete: _loadHabits,
    );
    List<Widget> newResearchModeListViewContent = <Widget>[
      connectedAs,
      otherUser,
    ];

    _newResearchMode = Scaffold(
      appBar: appBar,
      body: _getBody(newResearchModeListViewContent),
    );

    _curMode = _newResearchMode;
  }

  @override
  Widget build(BuildContext context) {
    return _curMode;
  }
}
