import 'package:flutter/material.dart';

import 'Cst.dart';
import 'TextInput.dart';
import 'serverCommunication.dart';

/// This class represents the forgotten password screen of the application.
/// It allows the user to give its username and the corresponding email,
/// in order to receive an email containing its password.
class ForgottenPasswordScreen extends StatefulWidget {
  @override
  _ForgottenPasswordScreenState createState() =>
      new _ForgottenPasswordScreenState();
}

class _ForgottenPasswordScreenState extends State<ForgottenPasswordScreen> {
  static final _backgroundColor = Colors.lightBlue[50];
  List<Widget> _listViewContent;
  InputText _usernameInput;
  TextEditingController _username = TextEditingController();
  InputText _emailInput;
  TextEditingController _email = TextEditingController();
  Padding _askNewPasswordButton;

  _askNewPassword() async {
    bool goodId = _username.text != "";
    bool goodEmail = true;
    int indexOfAt = _email.text.lastIndexOf("@");
    int indexOfDot = _email.text.lastIndexOf(".");

    /// The email should contain at least one @ and one dot,
    /// there should be at least one character before the @, after the dot and between the @ and the dot.
    if (indexOfAt < 1 ||
        indexOfDot == -1 ||
        indexOfDot == _email.text.length - 1 ||
        indexOfDot < indexOfAt + 2) {
      goodEmail = false;
    }

    if (goodId && goodEmail) {
      int newPasswordResult =
          await ServerCommunication.sendNewPassword(_username.text, _email.text);

      /// If the username exists, we don't tell the user whether the email address is good or not,
      if (newPasswordResult == forgottenPasswordOK) {
        setState(() {
          _listViewContent = <Widget>[
            _usernameInput,
            _emailInput,
            _askNewPasswordButton,
            Text(
              "Si l'adresse email correspond à l'identifiant, un mail contenant votre mot de passe vous sera envoyé.",
              style: confirmationStyle,
            ),
          ];
        });
        return;
      }
      setState(() {
        _listViewContent = <Widget>[
          _usernameInput,
        ];

        /// An invalid username is a username that's not in our database
        if (newPasswordResult == invalidUsername) {
          _listViewContent += <Widget>[
            Text(
              "Cet identifiant n'existe pas",
              style: smallWarningStyle,
              textAlign: TextAlign.right,
            )
          ];
        }

        _listViewContent += <Widget>[
          _emailInput,
          _askNewPasswordButton,
        ];

        /// Tell the user that the problem comes from the server, so that he doesn't try multiple emails.
        if (newPasswordResult == httpError) {
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
        _askNewPasswordButton,
      ];
    });
  }

  @override
  void initState() {
    super.initState();

    /// We wrap the InputTexts in variables so that we can insert messages between them without having them rebuilt.
    _usernameInput = /*TextFormField(
      controller: _username,
      decoration: new InputDecoration(
        labelText: "Identifiant",

        fillColor: Colors.white,
        border: new OutlineInputBorder(
          borderRadius: new BorderRadius.circular(25.0),
          borderSide: new BorderSide(
          ),
        ),),);*/

    InputText(
      messageToUser: 'Identifiant',
      color: _backgroundColor,
      controller: _username,
    );
     _emailInput = InputText(
      messageToUser: 'Adresse email',
      color: _backgroundColor,
      controller: _email,
      emailAddress: true,
    );

    _askNewPasswordButton = Padding(
      padding: EdgeInsets.symmetric(horizontal: 25.0),
      child: RaisedButton(
        textColor: Colors.white,
        color: Colors.lightBlue[800],
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(30.0),
        ),
        child: Text(
          'Nouveau mot de passe',
          //style: textStyle,
          style: TextStyle(fontSize: 18.0),
          textAlign: TextAlign.center,
        ),
        onPressed: _askNewPassword,
      ),
    );

    _listViewContent = <Widget>[
      _usernameInput,
      _emailInput,
      _askNewPasswordButton,
    ];
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
