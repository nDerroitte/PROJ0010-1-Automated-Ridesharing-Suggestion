import 'package:flutter/material.dart';

import 'Cst.dart';
import 'TextInput.dart';
import 'MainScreen.dart';

class LoginPage extends StatefulWidget {
  @override
  _LoginPageState createState() => new _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  static final _backgroundColor = Colors.lightBlue;
  String username;
  String password;
  List<Widget> _listViewContent;
  Widget _usernameInput;
  Widget _passwordInput;
  bool initialized = false;
  bool invalidPassPrinted = false;

  submittedUsername(String submission) {
    username = submission;
  }

  submittedPassword(String submission) {
    password = submission;
  }

  connexion() {
    bool checkPassword = true; //TODO check if password is correct
    setState(() {
      if (checkPassword) {
        Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) => MainScreen()),
        );
      } else if (!invalidPassPrinted) {
        _listViewContent += <Widget>[
          Center(
            child: Text(
              'Identifiant ou mot de passe incorrect.',
              style: warningStyle,
            ),
          )
        ];
        invalidPassPrinted = true;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    _usernameInput = TextInput(
      messageToUser: 'Identifiant',
      color: _backgroundColor,
      onSubmitted: submittedUsername,
    );

    _passwordInput = TextInput(
      messageToUser: 'Mot de passe',
      color: _backgroundColor,
      onSubmitted: submittedPassword,
      obscureText: true,
    );

    if (!initialized) {
      _listViewContent = <Widget>[
        _usernameInput,
        _passwordInput,
        Padding(
          padding: EdgeInsets.symmetric(horizontal: 75.0),
          child: RaisedButton(
            child: Text(
              'Connexion',
              style: textStyle,
            ),
            onPressed: connexion,
          ),
        ),
      ];
      initialized = true;
    }

    return Scaffold(
        appBar: appBar,
        body: Container(
          color: _backgroundColor,
          child: Center(
            child: Padding(
              padding: EdgeInsets.symmetric(
                horizontal: 30.0,
              ),
              child: ListView(
                shrinkWrap: true,
                children: _listViewContent,
              ),
            ),
          ),
        ));
  }
}
