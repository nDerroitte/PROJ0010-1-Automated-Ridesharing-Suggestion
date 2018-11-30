import 'dart:io';
import 'dart:convert';
import 'package:flutter/services.dart';

import 'Cst.dart';

/// This file contains wrappers to send messages to the server and handling the potential exceptions.
/// The functions returns integers, defined in the Cst.dart file, that represent the different possible answers of the server.
/// An httpError is returned in case of Exception or not understood answer.

class ServerCommunication {
  // TODO the hostname in the URL and in the CN field of the pkcs12 file should be the same, DON'T FORGET to regenerate the
  // TODO files when releasing on the production server
  String cookie;

  static Future<HttpClient> _secureClient() async {
    SecurityContext context = SecurityContext();
    final _cert = await rootBundle.load("CACovoit.pem");
    context.setTrustedCertificatesBytes(
        _cert.buffer.asUint8List(_cert.offsetInBytes, _cert.lengthInBytes));
    return HttpClient(context: context);
  }

  static Future<HttpClientResponse> _get(String url) async {
    HttpClient client = await _secureClient();
    return client.getUrl(Uri.parse(url)).then((HttpClientRequest request) {
      request.headers.set(HttpHeaders.hostHeader, "localhost:19001");
      request.headers.set(HttpHeaders.acceptHeader, "application/json");
      return request.close();
    });
  }

  Future<HttpClientResponse> _post(String url, String body) async {
    HttpClient client = await _secureClient();
    return client.postUrl(Uri.parse(url)).then((HttpClientRequest request) {
      request.headers.set(HttpHeaders.hostHeader, "localhost:19001");
      request.headers.set(HttpHeaders.acceptHeader, "application/json");
      request.headers.set(HttpHeaders.cookieHeader, cookie);
      List<int> bodyBytes = latin1.encode(body);
      request.headers.set(HttpHeaders.contentLengthHeader, bodyBytes.length.toString());
      request.add(bodyBytes);
      return request.close();
    });
  }

  static Future<String> _responseBody(HttpClientResponse response) async {
    StringBuffer body = StringBuffer();
    for (String readUnit in await response.transform(latin1.decoder).toList()) {
      body.write(readUnit);
    }
    return body.toString();
  }

  Future<bool> _sendPoints(String jsonData, int tryIndex) async {
    if (tryIndex > 4) {
      return false;
    }
    HttpClientResponse response;
    try {
      response = await _post(serverURL + "store_data?", jsonData);
    } catch (exception) {
      print(exception);
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
    HttpClientResponse response;
    try {
      response = await _get(
          serverURL + "sign_in?user=" + username + "&password=" + password);
    } catch (exception) {
      return httpError;
    }
    cookie = response.headers.value("set-cookie");

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

  static Future<int> sendSignUp(
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

  static Future<int> sendNewPassword(String username, String email) async {
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
}
