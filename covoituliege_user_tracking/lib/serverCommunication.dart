import 'dart:io';
import 'dart:convert';
import 'dart:async';
import 'package:flutter/services.dart';

import 'Cst.dart';
import 'FileHandler.dart';

/// This file contains wrappers to send messages to the server and handling the potential exceptions.
/// The functions returns integers, defined in the Cst.dart file, that represent the different possible answers of the server.
/// An httpError is returned in case of Exception or not understood answer.

/// Returns a secure HttpClient filled with the security certificate and key
Future<HttpClient> _secureClient() async {
  SecurityContext context = SecurityContext();
  final _cert = await rootBundle.load("spem2.pem");
  context.setTrustedCertificatesBytes(
      _cert.buffer.asUint8List(_cert.offsetInBytes, _cert.lengthInBytes));
  return HttpClient(context: context);
}

/// Sends a get message to the url given in argument and returns a future
/// containing the response
Future<HttpClientResponse> _get(String url) async {
  HttpClient client = await _secureClient();
  return client.getUrl(Uri.parse(url)).then((HttpClientRequest request) {
    request.headers.set(HttpHeaders.hostHeader, "spem2.montefiore.ulg.ac.be:443");
    request.headers.set(HttpHeaders.acceptHeader, "application/json");
    return request.close();
  });
}

/// Sends a post message to the url given in argument, appending the body
/// (also given in argument) to it, and returns a future containing the response
Future<HttpClientResponse> _post(String url, String body) async {
  HttpClient client = await _secureClient();
  String cookie = await getCookie();
  return client.postUrl(Uri.parse(url)).then((HttpClientRequest request) {
    request.headers.set(HttpHeaders.hostHeader, "spem2.montefiore.ulg.ac.be:443");
    request.headers.set(HttpHeaders.acceptHeader, "application/json");
    request.headers.set(HttpHeaders.cookieHeader, cookie);
    List<int> bodyBytes = latin1.encode(body);
    request.headers.set(HttpHeaders.contentLengthHeader, bodyBytes.length.toString());
    request.add(bodyBytes);
    return request.close();
  });
}

/// Wrapper that extracts the body of a response
Future<String> _responseBody(HttpClientResponse response) async {
  StringBuffer body = StringBuffer();
  for (String readUnit in await response.transform(latin1.decoder).toList()) {
    body.write(readUnit);
  }
  return body.toString();
}

/// If the communication with the server failed for any reason,
/// retry up to a given number of times
Future<bool> _sendJourneys(String jsonData, int tryIndex) async {
  if (tryIndex > 4) {
    return false;
  }
  HttpClientResponse response;
  try {
    response = await _post(serverURL + "store_data?", jsonData);
  } catch (exception) {
    return _sendJourneys(jsonData, tryIndex + 1);
  }

  if (response.statusCode == 200) {
    return true;
  } else {
    return _sendJourneys(jsonData, tryIndex + 1);
  }
}

/// Sends the journeys contained in the argument to the server
Future<bool> sendJourneys(String jsonData) async {
  return _sendJourneys(jsonData, 0);
}

/// Tries to connect with the given logs, see Cst.dart for the different
/// possible results
Future<int> checkConnection(String username, String password) async {
  if (username == "" && password == "") {
    return anonymousConnexion;
  }
  HttpClientResponse response;
  try {
    response = await _get(
        serverURL + "sign_in?user=" + username + "&password=" + password);
  } catch (exception) {
    return httpError;
  }
  String cookie = response.headers.value("set-cookie");
  if (cookie != null) {
    await storeCookie(cookie);
  }

  if (response.statusCode == 200) {
    switch (await _responseBody(response)) {
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

/// Tries to sign up a new user, may fail for instance is the username is
/// already used
Future<int> sendSignUp(
    String username, String password, String email) async {
  HttpClientResponse response;
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
    switch (await _responseBody(response)) {
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

/// Asks the server to send an email containing the password of the given user.
/// If the sent email is not the same as the one used for signing up,
/// the request is silently dropped(we do not want to give this information to the user).
Future<int> sendPasswordRequest(String username, String email) async {
  HttpClientResponse response;
  try {
    response = await _get(serverURL +
        "forgotten_password?user=" +
        username +
        "&email=" +
        email);
  } catch (exception) {
    return httpError;
  }

  if (response.statusCode == 200) {
    switch (await _responseBody(response)) {
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
