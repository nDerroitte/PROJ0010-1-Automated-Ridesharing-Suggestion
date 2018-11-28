import 'package:http/http.dart' as http;

import 'Cst.dart';

/// This file contains wrappers to send messages to the server and handling the potential exceptions.
/// The functions returns integers, defined in the Cst.dart file, that represent the different possible answers of the server.
/// An httpError is returned in case of Exception or not understood answer.

class ServerCommunication {
  String cookie;

  static Future<http.Response> _get(String url) {
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

  Future<http.Response> _post(String url, String body) {
    return http
        .post(
      Uri.encodeFull(url),
      headers: {
        "Accept": "application/json",
        "host": "localhost:9000",
        "Cookie": cookie,
      },
      body: body,
    )
        .timeout(
      Duration(seconds: 5),
    );
  }

  Future<bool> _sendPoints(String jsonData, int tryIndex) async {
    if (tryIndex > 9) {
      return false;
    }
    http.Response response;
    try {
      response = await _post(
          serverURL + "store_data?", jsonData);
    } catch (exception) {
      return _sendPoints(jsonData, tryIndex + 1);
    }

    if (response.statusCode == 200) {
      return true;
    } else {
      return _sendPoints(jsonData, tryIndex + 1);
    }
  }

  Future<bool> sendPoints(String jsonData) async {
    return _sendPoints(jsonData, 0);
  }

  Future<int> checkConnection(String username, String password) async {
    http.Response response;
    try {
      response = await _get(
          serverURL + "sign_in?user=" + username + "&password=" + password);
    } catch (exception) {
      return httpError;
    }

    cookie = response.headers["set-cookie"];

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

  static Future<int> sendSignUp(String username, String password, String email) async {
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

  static Future<int> sendNewPassword(String username, String email) async {
    http.Response response;
    try {
      response = await _get(
          serverURL + "forgotten_password?user=" + username + "&email=" +
              email);
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
}

Future<int> _storeData(String jsonData, int tryNumber) async {
  if (tryNumber > 9) {
    return httpError;
  }
  http.Response response;
  try {
    response = await _post(serverURL + "store_data?", jsonData);
  } catch (exception) {
    return _storeData(jsonData, tryNumber + 1);
  }
  if (response.statusCode == 200) {
    return storeDataOK;
  } else {
    return _storeData(jsonData, tryNumber + 1);
  }
}

Future<int> storeData(String jsonData) async {
  return _storeData(jsonData, 0);
}