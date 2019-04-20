import 'package:path_provider/path_provider.dart';
import 'dart:io';
import 'UserInfo.dart';
import 'dart:convert';
import 'dart:async';

/// This file contains different wrappers to read and write in the application local file.

const _STARTED_KEY = "started";
const _OVER_KEY = "over";

/// Getter for the path of the application local file.
Future<String> get _localPath async {
  final directory = await getApplicationDocumentsDirectory();
  return directory.path;
}

/// Getter for the application local file.
Future<File> get _localFile async {
  final path = await _localPath;
  return File('$path/data.json');
}

Future<File> get _geofenceFile async {
  final path = await _localPath;
  return File('$path/geofence');
}

Future<File> get _geofenceCenter async {
  final path = await _localPath;
  return File('$path/geofenceCenter');
}

Future<File> get _journey async {
  final path = await _localPath;
  return File('$path/journey');
}

Future<File> get _llStarted async {
  final path = await _localPath;
  return File('$path/llStarted');
}

Future<File> get _bufferedPoints async {
  final path = await _localPath;
  return File('$path/points');
}

/// TODO rewrite docs
/// Write the argument in the application local file.
_writeInFile(String data, File where) async {
  RandomAccessFile file = await where.open(mode: FileMode.append);
  file.lockSync(FileLock.blockingExclusive);
  await file.writeString(data);
  file.unlockSync();
  file.close();
}

/// Delete all the content of the application local file.
clearFile() async {
  _clearFile(await _localFile);
}

clearGeofence() async {
  _clearFile(await _geofenceFile);
}

clearBuffer() async {
  _clearFile(await _bufferedPoints);
}

_clearFile(File which) async {
  RandomAccessFile file = await which.open(mode: FileMode.write);
  file.lockSync(FileLock.blockingExclusive);
  await file.truncate(0);
  file.unlockSync();
  file.close();
}

/// Returns the current application local file content.
/// Returns "Error reading file" in case of exception, will be improved.
Future<String> readFile() async {
  return _readFile(await _localFile);
}
Future<String> _readFile(File which) async {
  try {
    return await which.readAsString();;
  } catch (e) {
    // return empty file
    return "";
  }
}

storeGeofenceById(String id) async {
  File file = await _geofenceFile;
  //TODO for defect table : forgotten await so file became empty for no reason
  await _clearFile(file);
  await _writeInFile(id, file);
}

/// The calendar should be formatted as "yyyy-MM-dd HH-mm-ss"
storePoint(String calendar, String lat, String lon) async {
  await _writeInFile(calendar + "," + lat + "," + lon + "\n", await _bufferedPoints);
  await _startJourney();
}

storeGeofenceCenter(String latitude, String longitude) async {
  await _writeInFile(latitude + "," + longitude, await _geofenceCenter);
}

Future<String> getLastGeofenceId() async {
  String id;
  try {
    id = await _readFile(await _geofenceFile);
  } catch (e) {
    return null;
  }
  if (id == "") {
    return null;
  }
  return id;
}

Future<List<String>> getLastTimedLoc() async {
  if (!(await hasJourneyStarted())) {
    String data = await _readFile(await _geofenceCenter);
    if (data == "") {
      return null;
    }
    List<String> res = [""];
    // sublist because the split method returns a last empty string
    res.addAll(data.split(",").sublist(0, 2));
    return res;
  }
  String data = await _readFile(await _bufferedPoints);
  if (data == "") {
    return null;
  }
  List<String> points = data.split("\n");
  // -2 and sublist because the split method returns a last empty string
  return points[points.length - 2].split(",").sublist(0, 3);
}

Future<String> getLastCalendar() async {
  String data = await _readFile(await _bufferedPoints);
  if (data == "") {
    return null;
  }
  List<String> points = data.split("\n");
  /// -2 because the split method returns a last empty string
  return points[points.length - 2].split(",")[0];
}

writeJourneyFromBufferedPoints(UserInfo user) async {
  List<String> points = (await _readFile(await _bufferedPoints)).split("\n");
  /// Once again, the split method return a last empty string
  points = points.sublist(0, points.length - 1);
  List<String> calendarAndPos;
  for (String point in points) {
    calendarAndPos = point.split(",");
    user.addData(calendarAndPos[0], calendarAndPos[1], calendarAndPos[2]);
  }
  String jSon = json.encode(user);
  await _writeInFile(jSon + "data_splitter", await _localFile);
  user.clear();
  await _clearFile(await _bufferedPoints);
  await _endJourney();
}

_startJourney() async {
  File file = await _journey;
  await _clearFile(file);
  await _writeInFile(_STARTED_KEY, file);
}

_endJourney() async {
  File file = await _journey;
  await _clearFile(file);
  await _writeInFile(_OVER_KEY, file);
}

hasJourneyStarted() async {
  String data = await _readFile(await _journey);
  if (data == "" || data == _OVER_KEY) {
    return false;
  }
  return true;
}

startedLocListener() async {
  File file = await _llStarted;
  await _clearFile(file);
  await _writeInFile(_STARTED_KEY, file);
}

stoppedLocListener() async {
  File file = await _llStarted;
  await _clearFile(file);
  await _writeInFile(_OVER_KEY, file);
}

isLocListenerStarted() async {
  String data = await _readFile(await _llStarted);
  if (data == "" || data == _OVER_KEY) {
    return false;
  }
  return true;
}