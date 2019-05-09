import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'Cst.dart';

/// This class represents a text input with a little label (labelText argument)
/// showing which information the user should write in the input.
/// The color argument is the background color of the widget,
/// the controller argument is a TextInputController that enables the caller
/// to get the current input.
/// The obscureText argument tells whether the text should be hidden or not
/// (on the screen), it defaults to false.
///	The emailAddress argument tells whether the keyboard should be optimized
/// for an email address input, it defaults to false.
class InputText extends StatelessWidget {
  static const _padding = EdgeInsets.symmetric(vertical: 15.0);

  final String labelText;
  final Color color;
  final TextEditingController controller;
  final bool obscureText;
  final bool emailAddress;
  final Function onEditingComplete;

  InputText({
    @required this.labelText,
    @required this.color,
    @required this.controller,
    this.obscureText = false,
    this.emailAddress = false,
    this.onEditingComplete
  })  : assert(labelText != null),
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
            Expanded(
              child: Container(
                child: TextFormField(
                  onEditingComplete: this.onEditingComplete,
                  obscureText: this.obscureText,
                  keyboardType: type,
                  style: textStyle,
                  controller: controller,
                  inputFormatters: <TextInputFormatter>[
                    BlacklistingTextInputFormatter(
                        RegExp('[\\&|\\=|\\?|\\[|\\]|\\#]')),
                  ],
                  decoration: new InputDecoration(
                    labelText: this.labelText,
                    fillColor: Colors.black,
                    border: new OutlineInputBorder(
                      borderRadius: new BorderRadius.circular(25.0),
                      borderSide: new BorderSide(),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
