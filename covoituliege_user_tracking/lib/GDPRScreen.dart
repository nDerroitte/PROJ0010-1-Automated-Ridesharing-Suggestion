import 'package:flutter/material.dart';
import 'Cst.dart';

/// This class is a simple long text screen with no user interactions
/// The user can hit the return button when he is done reading.
class GDPRScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: appBar,
      body: gdprText,
    );
  }
}
