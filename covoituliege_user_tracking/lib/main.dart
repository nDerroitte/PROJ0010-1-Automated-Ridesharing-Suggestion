import 'package:flutter/material.dart';
import 'LoginPage.dart';

void main() async {
  runApp(new UserTracking());
}

class UserTracking extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'User Tracking CovoitULiège',
      home: Container(
        child: LoginPage(),
      ),
    );
  }
}
