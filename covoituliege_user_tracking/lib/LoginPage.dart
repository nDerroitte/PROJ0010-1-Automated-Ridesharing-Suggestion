import 'package:flutter/material.dart';
import 'package:simple_permissions/simple_permissions.dart';

import 'Cst.dart';
import 'TextInput.dart';
import 'MainScreen.dart';
import 'UserInfo.dart';
import 'serverCommunication.dart';
import 'ForgottenPasswordScreen.dart';
import 'SignUpScreen.dart';

import 'dart:ui';

/// This class represents the login screen of the application.
/// It allows the user to connect to its account, to go the sign up screen
/// if he has no account yet, and to go to the forgotten password screen
/// if he can't remember its password.
class LoginPage extends StatefulWidget {
  @override
  _LoginPageState createState() => new _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  static final _backgroundColor = Colors.lightBlue[50];
  List<Widget> _baseListViewContent;
  List<Widget> _listViewContent;
  InputText _identifiant;
  InputText _password;
  TextEditingController _usernameController = TextEditingController();
  TextEditingController _passwordController = TextEditingController();
  ServerCommunication _serverCommunication;

  /// Ask the server to check the username-password pair, and push the tracking screen if yes.
  /// If the connection fails, the cause is given to the user.
  _connection() async {
    int connectionResult = await _serverCommunication.checkConnection(
        _usernameController.text, _passwordController.text);
    setState(() {
      if (connectionResult == passwordOK) {
        Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) => MainScreen(
                  UserInfo(_usernameController.text), _serverCommunication)),
        );
      } else if (connectionResult == anonymousConnexion) {
        Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) => MainScreen(
                  UserInfo(_usernameController.text),
                  _serverCommunication,
                  true)),
        );
      } else {
        String errorExplanation;
        switch (connectionResult) {
          case invalidUsername:
            errorExplanation = 'Cet identifiant n\'existe pas';
            break;

          case invalidPassword:
            errorExplanation = 'Mot de passe invalide';
            break;

          case httpError:
            errorExplanation = serverError;
        }
        _listViewContent = _baseListViewContent +
            <Widget>[
              Center(
                child: Text(
                  errorExplanation,
                  style: warningStyle,
                ),
              )
            ];
      }
    });
  }

  /// Function called by the "Mot de passe oublié?" text, it simply pushes the ForgottenPassword screen.
  _forgottenPassword() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => ForgottenPasswordScreen()),
    );
  }

  /// Function called by the "S'inscrire" text, it simply pushes the SignUp screen.
  _signUp() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => SignUpScreen()),
    );
  }

  @override
  void initState() {
    super.initState();
    _identifiant = InputText(
      messageToUser: 'Identifiant',
      controller: _usernameController,
      color: _backgroundColor,
      /*decoration: new InputDecoration(
        labelText: "Identifiant",
        fillColor: Colors.white,
        border: new OutlineInputBorder(
          borderRadius: new BorderRadius.circular(25.0),
          borderSide: new BorderSide(
          ),
        ),),*/
    );

    _password = InputText(
      messageToUser: 'Mot de passe',
      controller: _passwordController,
      color: _backgroundColor,

      /*decoration: new InputDecoration(
        labelText: "Mot de passe",
        fillColor: Colors.white,
        border: new OutlineInputBorder(
          borderRadius: new BorderRadius.circular(25.0),
          borderSide: new BorderSide(
          ),
        ),),*/
    );

    /// This variable holds the content of the screen. It's useful in the case we want to add a text
    /// at the bottom (that occurs in case of connection error), it avoids rebuilding the main content each time.
    _baseListViewContent = <Widget>[
      _identifiant,
      _password,
      Padding(
        padding: EdgeInsets.symmetric(horizontal: 75.0),
        child: RaisedButton(
          textColor: Colors.white,
          color: Colors.lightBlue[800],
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(30.0),
          ),
          child: Text(
            'Connexion',
            style: TextStyle(fontSize: 18.0),
          ),
          onPressed: _connection,
        ),
      ),
      Divider(),
      ListTile(
          leading: Icon(Icons.help),
          title: Text('Mot de passe oublié?'),
          onTap: _forgottenPassword),
      ListTile(
          leading: Icon(Icons.settings),
          title: Text("S'inscrire"),
          onTap: _signUp),
    ];

    _listViewContent = _baseListViewContent;

    _serverCommunication = ServerCommunication();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: appBar,
      body: Container(
        /*decoration: new BoxDecoration(
          image: new DecorationImage(image: new AssetImage("car.png"), fit: BoxFit.cover,),
        ),*/
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
      ),
    );
  }
}
