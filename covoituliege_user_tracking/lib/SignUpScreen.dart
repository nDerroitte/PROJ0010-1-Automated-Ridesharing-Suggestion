import 'package:flutter/material.dart';
import 'package:flutter/gestures.dart';

import 'Cst.dart';
import 'TextInput.dart';
import 'GDPRScreen.dart';
import 'serverCommunication.dart';

/// This class represents the sign up screen of the application.
/// It allows the user to create a new account that will be directly usable.
/// The screen also contains a link to the GDPR screen, because signing up implies giving consent
/// for the data collection and usage.
class SignUpScreen extends StatefulWidget {
  @override
  _SignUpScreenState createState() => new _SignUpScreenState();
}

class _SignUpScreenState extends State<SignUpScreen> {
  static final _backgroundColor = Colors.lightBlue[50];
  List<Widget> _listViewContent;
  TextInput _usernameInput;
  TextEditingController _username = TextEditingController();
  TextInput _passwordInput;
  TextEditingController _password = TextEditingController();
  TextInput _passwordConfirmationInput;
  TextEditingController _passwordConfirmation = TextEditingController();
  TextInput _emailInput;
  TextEditingController _email = TextEditingController();
  RichText _bottomNavigationBar;
  Padding _signUpButton;
  TapGestureRecognizer _gdprRecognizer;

  /// This function pushes the GDPR screen that contains the corresponding consent text.
  _printGDPR() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => GDPRScreen()),
    );
  }

  /// This function is called when the user taps on the "S'inscrire" button,
  /// it asks the server to create a new account and/or explains why it could not be done.
  _signUp() async {
    bool goodId = _username.text != "";
    bool goodPassword = _password.text.length > 3 && _password.text.length < 17;
    bool goodConfirmation = _passwordConfirmation.text == _password.text;
    bool goodEmail = true;
    int indexOfAt = _email.text.indexOf("@");
    int indexOfDot = _email.text.lastIndexOf(".");

    /// The email should contain at least one @ and one dot,
    /// there should be at least one character before the @, after the dot and between the @ and the dot.
    if (indexOfAt < 1 ||
        indexOfDot == -1 ||
        indexOfDot == _email.text.length - 1 ||
        indexOfDot < indexOfAt + 2) {
      goodEmail = false;
    }

    if (goodId && goodPassword && goodConfirmation && goodEmail) {
      int signUpResult =
          await ServerCommunication.sendSignUp(_username.text, _password.text, _email.text);
      if (signUpResult == signUpOK) {
        Navigator.pop(context);
        return;
      }
      setState(() {
        _listViewContent = <Widget>[
          _usernameInput,
        ];
        if (signUpResult == invalidUsername) {
          _listViewContent += <Widget>[
            Text(
              "Cet identifiant existe déjà",
              style: smallWarningStyle,
              textAlign: TextAlign.right,
            )
          ];
        }

        /// Note that the server will never answer with an invalidPassword error.
        _listViewContent += <Widget>[
          _passwordInput,
        ];
        _listViewContent += <Widget>[
          Text(
            "Entre 4 et 16 caractères",
            style: smallInfoStyle,
            textAlign: TextAlign.right,
          )
        ];

        _listViewContent += <Widget>[
          _passwordConfirmationInput,
        ];

        _listViewContent += <Widget>[
          _emailInput,
        ];
        if (signUpResult == invalidEmail) {
          _listViewContent += <Widget>[
            Text(
              "Cette adresse email n'existe pas",
              style: smallWarningStyle,
              textAlign: TextAlign.right,
            )
          ];
        }

        _listViewContent += <Widget>[
          _signUpButton,
        ];
        if (signUpResult == httpError) {
          _listViewContent += <Widget>[
            Text(
              serverError,
              style: warningStyle,
            ),
          ];
        }
      });
      return;
    }

    setState(() {
      _listViewContent = <Widget>[
        _usernameInput,
      ];
      if (!goodId) {
        _listViewContent += <Widget>[
          Text(
            "Champ obligatoire",
            style: smallWarningStyle,
            textAlign: TextAlign.right,
          )
        ];
      }

      _listViewContent += <Widget>[
        _passwordInput,
      ];
      TextStyle style;
      if (!goodPassword) {
        style = smallWarningStyle;
      } else {
        style = smallInfoStyle;
      }
      _listViewContent += <Widget>[
        Text(
          "Entre 4 et 16 caractères",
          style: style,
          textAlign: TextAlign.right,
        )
      ];

      _listViewContent += <Widget>[
        _passwordConfirmationInput,
      ];
      if (!goodConfirmation) {
        _listViewContent += <Widget>[
          Text(
            "Les mots de passe sont différents",
            style: smallWarningStyle,
            textAlign: TextAlign.right,
          )
        ];
      }

      _listViewContent += <Widget>[
        _emailInput,
      ];
      if (!goodEmail) {
        _listViewContent += <Widget>[
          Text(
            "Adresse email invalide",
            style: smallWarningStyle,
            textAlign: TextAlign.right,
          )
        ];
      }

      _listViewContent += <Widget>[
        _signUpButton,
      ];
    });
  }

  /// TapGestureRecognizer don't dispose themselves automatically.
  @override
  void dispose() {
    _gdprRecognizer.dispose();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();

    /// We wrap the TextInputs in variables so that we can insert messages between them without having them rebuilt.
    _usernameInput = TextInput(
      messageToUser: 'Identifiant',
      color: _backgroundColor,
      controller: _username,
    );
    _passwordInput = TextInput(
      messageToUser: 'Mot de passe: 4 à 16 caractères',
      color: _backgroundColor,
      controller: _password,
      obscureText: true,
    );
    _passwordConfirmationInput = TextInput(
      messageToUser: 'Confirmation',
      color: _backgroundColor,
      controller: _passwordConfirmation,
      obscureText: true,
    );
    _emailInput = TextInput(
      messageToUser: 'Adresse email',
      color: _backgroundColor,
      controller: _email,
      emailAddress: true,
    );

    _signUpButton = Padding(
      padding: EdgeInsets.symmetric(horizontal: 75.0),
      child: RaisedButton(
        textColor: Colors.white,
        color: Colors.lightBlue[800],
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(30.0),
        ),
        child: Text(
          'S\'inscrire',
          //style: textStyle,
          style: TextStyle(fontSize: 18.0),
        ),
        onPressed: _signUp,
      ),
    );

    _listViewContent = <Widget>[
      _usernameInput,
      _passwordInput,
      /*Text(
        'Entre 4 et 16 caractères',
        style: smallInfoStyle,
        textAlign: TextAlign.right,
      ),*/
      _passwordConfirmationInput,
      _emailInput,
      _signUpButton,
    ];

    _gdprRecognizer = TapGestureRecognizer()..onTap = _printGDPR;
    _bottomNavigationBar = RichText(
      text: TextSpan(
        text: 'Cette application utilise vos données, en particulier votre localisation.' +
            ' En cliquant sur s\'inscrire, vous marquez votre accord avec notre ',
        //style: textStyle,
        style: TextStyle(fontSize: 18.0, color: Colors.black),
        children: <TextSpan>[
          TextSpan(
            text: 'politique de confidentialité.',
            //style: linkStyle,
            style: TextStyle(fontSize: 18.0, color: Colors.blue),
            recognizer: _gdprRecognizer,
          ),
        ],
      ),
    );
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
      bottomNavigationBar: _bottomNavigationBar,
    );
  }
}
