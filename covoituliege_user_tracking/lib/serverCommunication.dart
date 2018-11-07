import 'package:http/http.dart' as http;

import 'Cst.dart';

Future<http.Response> _get(String url) {
  return http.get(url).timeout(
        Duration(seconds: 2),
      );
}

Future<int> checkConnexion(String username, String password) async {
  http.Response response;
  try {
    response = await _get(
        serverURL + "sign_in?user=" + username + "&password=" + password);
  } catch (exception) {
    return httpError;
  }

  if (response.statusCode == 200) {
    switch (response.body) {
      case "OK":
        return passwordOK;

      case "username":
        return invalidUsername;

      case "password":
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
      case "OK":
        return signUpOK;

      case "username":
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
    response = await _get(serverURL +
        "new_password?user=" +
        username +
        "&email=" +
        email);
  } catch (exception) {
    return httpError;
  }

  if (response.statusCode == 200) {
    switch (response.body) {
      case "OK":
        return newPasswordOK;

      case "username":
        return invalidUsername;

      default:
        return httpError;
    }
  } else {
    return httpError;
  }
}
