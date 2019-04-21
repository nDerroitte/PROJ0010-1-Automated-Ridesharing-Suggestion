import 'package:flutter/material.dart';

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
/// It is also possible to connect and store data anonymously, which will later
/// be sent in the name of the connected user
class LoginPage extends StatefulWidget {
  @override
  _LoginPageState createState() => new _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  List<Widget> _baseListViewContent;
  List<Widget> _listViewContent;
  InputText _username;
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
                UserInfo(_usernameController.text), _serverCommunication),
          ),
        );
      } else if (connectionResult == anonymousConnexion) {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => MainScreen(
                  UserInfo(_usernameController.text),
                  _serverCommunication,
                  true,
                ),
          ),
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
    _username = InputText(
      labelText: 'Identifiant',
      controller: _usernameController,
      color: backgroundColor,
    );

    _password = InputText(
      labelText: 'Mot de passe',
      controller: _passwordController,
      color: backgroundColor,
      obscureText: true,
    );

    /// This variable holds the content of the screen. It's useful in the case we want to add a text
    /// at the bottom (that occurs in case of connection error), it avoids rebuilding the main content each time.
    _baseListViewContent = <Widget>[
      _username,
      _password,
      Padding(
        padding: EdgeInsets.symmetric(horizontal: 75.0),
        child: RaisedButton(
          textColor: Colors.white,
          color: buttonColor,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(30.0),
          ),
          child: Text(
            'Connexion',
            style: textStyle,
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
      appBar: AppBar(
        title: Center(child: Text('Ugo')),
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
              tileMode: TileMode.clamp,
            ),
          ),
        ),
      ),
      body: Container(
        color: backgroundColor,
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
