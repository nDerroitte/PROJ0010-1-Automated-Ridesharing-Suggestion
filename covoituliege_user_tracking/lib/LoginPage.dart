import 'package:flutter/material.dart';
import 'package:flutter/gestures.dart';

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
  TapGestureRecognizer _forgottenPasswordRecognizer;
  TapGestureRecognizer _signUpRecognizer;

  /// Ask the server to check the username-password pair, and push the tracking screen if yes.
  /// If the connection fails, the cause is given to the user.
  _connection() async {
    int connectionResult = await checkConnection(_usernameController.text, _passwordController.text);
    setState(() {
      if (connectionResult == passwordOK) {
        Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) => MainScreen(UserInfo(_usernameController.text))),
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

  /// TapGestureRecognizer don't dispose themselves automatically.
  @override
  void dispose() {
    _forgottenPasswordRecognizer.dispose();
    _signUpRecognizer.dispose();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    _forgottenPasswordRecognizer = TapGestureRecognizer()
      ..onTap = _forgottenPassword;
    _signUpRecognizer = TapGestureRecognizer()..onTap = _signUp;

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
      Center(
        child: RichText(
          text: TextSpan(
            text: "Mot de passe oublié?",
            style: underlinedStyle,
            recognizer: _forgottenPasswordRecognizer,
            children: <TextSpan>[
              TextSpan(
                text: "\n\n",
                style: textStyle,
              ),
              TextSpan(
                text: "S'inscrire",
                style: underlinedStyle,
                recognizer: _signUpRecognizer,
              ),
            ],
          ),
        ),
      )
    ];

    _listViewContent = _baseListViewContent;
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
