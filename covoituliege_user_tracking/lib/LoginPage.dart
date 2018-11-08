import 'package:flutter/material.dart';
import 'package:flutter/gestures.dart';

import 'Cst.dart';
import 'TextInput.dart';
import 'MainScreen.dart';
import 'UserInfo.dart';
import 'serverCommunication.dart';
import 'ForgottenPasswordScreen.dart';
import 'SignUpScreen.dart';

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

  _connexion() async {
    // This line is disabled until the server is operational
    //int connexionResult = await checkConnexion(_usernameController.text, _passwordController.text);
    int connexionResult =
        passwordOK; //TODO remove this line (when server operational)
    setState(() {
      if (connexionResult == passwordOK) {
        Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) => MainScreen(UserInfo(_usernameController.text))),
        );
      } else {
        String errorExplanation;
        switch (connexionResult) {
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

  _forgottenPassword() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => ForgottenPasswordScreen()),
    );
  }

  _signUp() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => SignUpScreen()),
    );
  }

  @override
  void initState() {
    super.initState();
    _forgottenPasswordRecognizer = TapGestureRecognizer()
      ..onTap = _forgottenPassword;
    _signUpRecognizer = TapGestureRecognizer()..onTap = _signUp;

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
          onPressed: _connexion,
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
