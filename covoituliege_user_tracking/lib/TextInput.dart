import 'package:flutter/material.dart';
import 'Cst.dart';

/// This class represents a little text followed by a TextInput.
/// The messageToUser argument represents the text to display before the TextField,
/// the color argument is the background color of the text,
/// the controller argument is a TextInputController that enables the caller
/// to get the current input, without having to call a function each time the input is changed.
/// The obscureText argument tells whether the text should be hidden or not (on the screen), it defaults to false.
///	The emailAddress argument tells whether the keyboard should be optimized for an email address input, it defaults to false.
class TextInput extends StatelessWidget {
  static const _padding = EdgeInsets.symmetric(vertical: 15.0);

  final String messageToUser;
  final Color color;
  final TextEditingController controller;
  final bool obscureText;
  final bool emailAddress;

  TextInput({
    @required this.messageToUser,
    @required this.color,
    @required this.controller,
    this.obscureText = false,
    this.emailAddress = false,
  })  : assert(messageToUser != null),
        assert(color != null),
        assert(controller != null);

  @override
  Widget build(BuildContext context) {
    TextInputType type;
    if (emailAddress) {
      type = TextInputType.emailAddress;
    } else {
      type = TextInputType.text;
    }
    return Material(
      color: color,
      child: Padding(
        padding: _padding,
        child: Row(
          children: <Widget>[
            Text(
              this.messageToUser + ': ',
              style: textStyle,
            ),
            Expanded(
              child: Container(
                decoration: BoxDecoration(
                    color: Colors.white),
                child: TextField(
                  obscureText: this.obscureText,
                  keyboardType: type,
                  style: textStyle,
                  controller: controller,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}