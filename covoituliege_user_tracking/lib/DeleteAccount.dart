import 'package:flutter/material.dart';

import 'Cst.dart';
import 'TextInput.dart';
import 'LoginPage.dart';
import 'serverCommunication.dart';

/// This class represents the delete account screen of the application.
/// It allows the user to delete its account based on its username and password.
class DeleteAccountScreen extends StatefulWidget {
  final String username;
  final String password;

  DeleteAccountScreen(this.username, this.password);

  @override
  _DeleteAccountScreenState createState() => new _DeleteAccountScreenState();
}

class _DeleteAccountScreenState extends State<DeleteAccountScreen> {
  List<Widget> _baseListViewContent;
  List<Widget> _listViewContent;
  InputText _username;
  InputText _password;
  TextEditingController _usernameController = TextEditingController();
  TextEditingController _passwordController = TextEditingController();

  /// Ask the server to delete the account, and push the LoginPage screen if it worked.
  /// If the deletion fails, the cause is given to the user.
  _deleteAccount() async {
    int deleteResult = await deleteAccount(
        _usernameController.text, _passwordController.text);
    setState(() {
      if (deleteResult == credentialsOK) {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => LoginPage(),
          ),
        );
      } else {
        String errorExplanation;
        switch (deleteResult) {
          case invalidUsername:
            errorExplanation = 'Cet identifiant n\'existe pas';
            break;

          case invalidPassword:
            errorExplanation = 'Mot de passe invalide';
            break;

          default:
            errorExplanation = httpErrorText;
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

  @override
  void initState() {
    super.initState();
    _username = InputText(
      labelText: 'Identifiant',
      controller: _usernameController,
      color: backgroundColor,
    );
    _usernameController.text = widget.username;

    _password = InputText(
      labelText: 'Mot de passe',
      controller: _passwordController,
      color: backgroundColor,
      obscureText: true,
    );
    _passwordController.text = widget.password;

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
          onPressed: _deleteAccount,
        ),
      ),
    ];

    _listViewContent = _baseListViewContent;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: appBar,
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
