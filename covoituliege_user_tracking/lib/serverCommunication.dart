import 'package:http/http.dart' as http;

import 'Cst.dart';

/// This file contains wrappers to send messages to the server and handling the potential exceptions.
/// The functions returns integers, defined in the Cst.dart file, that represent the different possible answers of the server.
/// An httpError is returned in case of Exception or not understood answer.

Future<http.Response> _get(String url) {
  return http.get(
    Uri.encodeFull(url),
    headers: {
      "Accept": "application/json",
      "host": "localhost:9000",
    },
  ).timeout(
    Duration(seconds: 5),
  );
}

Future<bool> sendPoints(String data) async {
  http.Response response;
  try {
    response = await http.post(
      serverURL,
      headers: {
        "Accept": "application/json",
        "host": "localhost:9000",
      },
      body: data,
    );
  } catch (exception) {
    return false;
  }

  if (response.statusCode == 200) {
    return true;
  } else {
    return false;
  }
}

Future<int> checkConnection(String username, String password) async {
  http.Response response;
  try {
    response = await _get(
        serverURL + "sign_in?user=" + username + "&password=" + password);
  } catch (exception) {
    return httpError;
  }

  if (response.statusCode == 200) {
    switch (response.body) {
      case "connection OK":
        return passwordOK;

      case "user doesn't exist":
        return invalidUsername;

      case "incorrect pasword":
        return invalidPassword;

      default:
        return httpError;
    }
  } else {
    return httpError;
  }
}

Future<int> sendSignUp(String username, String password, String email) async {
  http.Response response;
  try {
    response = await _get(serverURL +
        "sign_up?user=" +
        username +
        "&password=" +
        password +
        "&email=" +
        email);
  } catch (exception) {
    return httpError;
  }

  if (response.statusCode == 200) {
    switch (response.body) {
      case "user successfully recorded":
        return signUpOK;

      case "pseudo already used":
        return invalidUsername;

      default:
        return httpError;
    }
  } else {
    return httpError;
  }
}

Future<int> sendNewPassword(String username, String email) async {
  http.Response response;
  try {
    response = await _get(
        serverURL + "forgotten_password?user=" + username + "&email=" + email);
  } catch (exception) {
    return httpError;
  }

  if (response.statusCode == 200) {
    switch (response.body) {
      case "username OK":
        return forgottenPasswordOK;

      case "user doesn't exist":
        return invalidUsername;

      default:
        return httpError;
    }
  } else {
    return httpError;
  }
}
