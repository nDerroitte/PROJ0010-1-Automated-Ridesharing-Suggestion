import 'package:flutter/material.dart';

import 'Cst.dart';
import 'TextInput.dart';
import 'MainScreen.dart';
import 'UserInfo.dart';

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
  TextEditingController _usernameController = TextEditingController();
  TextEditingController _passwordController = TextEditingController();

  connexion() {
    username = _usernameController.text;
    password = _passwordController.text;
    bool checkPassword = true; //TODO check if password is correct
    setState(() {
      if (checkPassword) {
        Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) => MainScreen(UserInfo(username))),
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
      controller: _usernameController,
    );

    _passwordInput = TextInput(
      messageToUser: 'Mot de passe',
      color: _backgroundColor,
      controller: _passwordController,
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
