import 'package:flutter/material.dart';
import 'Cst.dart';

class TextInput extends StatelessWidget {
  static const _padding = EdgeInsets.symmetric(vertical: 15.0);

  final String messageToUser;
  final Color color;
  final TextEditingController controller;
  final obscureText;

  const TextInput({
    @required this.messageToUser,
    @required this.color,
    @required this.controller,
    this.obscureText = false,
  })  : assert(messageToUser != null),
        assert(color != null),
        assert(controller != null);

  @override
  Widget build(BuildContext context) {
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
                  keyboardType: TextInputType.text,
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