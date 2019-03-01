import 'package:flutter/material.dart';
import 'package:simple_permissions/simple_permissions.dart';

import 'Cst.dart';
import 'TextInput.dart';
import 'MainScreen.dart';
import 'UserInfo.dart';
import 'serverCommunication.dart';
import 'ForgottenPasswordScreen.dart';
import 'SignUpScreen.dart';

/// This class represents the login screen of the application.
/// It allows the user to connect to its account, to go the sign up screen
/// if he has no account yet, and to go to the forgotten password screen
/// if he can't remember its password.
class LoginPage extends StatefulWidget {
  @override
  _LoginPageState createState() => new _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  static final _backgroundColor = Colors.lightBlue;
  List<Widget> _baseListViewContent;
  List<Widget> _listViewContent;
  TextEditingController _usernameController = TextEditingController();
  TextEditingController _passwordController = TextEditingController();
  ServerCommunication _serverCommunication;

  /// Ask the server to check the username-password pair, and push the tracking screen if yes.
  /// If the connection fails, the cause is given to the user.
  _connection() async {
    //int connectionResult = await _serverCommunication.checkConnection(
    //    _usernameController.text, _passwordController.text);
    int connectionResult = passwordOK; //TODO delete this, this is only for easier testing
    setState(() {
      if (connectionResult == passwordOK) {
        Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) => MainScreen(
                  UserInfo(_usernameController.text), _serverCommunication)),
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
    /// This variable holds the content of the screen. It's useful in the case we want to add a text
    /// at the bottom (that occurs in case of connection error), it avoids rebuilding the main content each time.
    _baseListViewContent = <Widget>[
      TextInput(
        messageToUser: 'Identifiant',
        color: _backgroundColor,
        controller: _usernameController,
      ),
      TextInput(
        messageToUser: 'Mot de passe',
        color: _backgroundColor,
        controller: _passwordController,
        obscureText: true,
      ),
      Padding(
        padding: EdgeInsets.symmetric(horizontal: 75.0),
        child: RaisedButton(
          child: Text(
            'Connexion',
            style: textStyle,
          ),
          onPressed: _connection,
        ),
      ),
      FlatButton(
        onPressed: _forgottenPassword,
        color: Colors.white,
        child: Text(
          "Mot de passe oublié?",
          style: textStyle,
        ),
      ),
      FlatButton(
        onPressed: _signUp,
        color: Colors.white,
        child: Text(
          "S'inscrire",
          style: textStyle,
        ),
      ),
    ];

    _listViewContent = _baseListViewContent;

    /// This line is necessary because the location package has an issue with the requestPermissions
    /// callbacks for SDK < 21.
    SimplePermissions.requestPermission(Permission.AlwaysLocation);

    _serverCommunication = ServerCommunication();
  }

  @override
  Widget build(BuildContext context) {
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
      ),
    );
  }
}
