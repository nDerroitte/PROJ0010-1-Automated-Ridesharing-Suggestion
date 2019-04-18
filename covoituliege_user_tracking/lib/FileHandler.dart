import 'package:path_provider/path_provider.dart';
import 'dart:io';
import 'UserInfo.dart';
import 'dart:convert';

/// This file contains different wrappers to read and write in the application local file.

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
  try {
    return _readFile(await _localFile);
  } catch (e) {
    print("Error reading file!");
    return "Error reading file";
  }
}
Future<String> _readFile(File which) async {
    // Read the file
    String contents = await which.readAsString();

    return contents;
}

storeGeofenceById(String id) async {
  File file = await _geofenceFile;
  //TODO for defect table : forgotten await so file became empty for no reason
  await _clearFile(file);
  await _writeInFile(id, file);
}

/// The calendar should be formatted as "yyyy-MM-dd HH-mm-ss"
storePoint(String calendar, String lat, String lon) async {
  _writeInFile(calendar + "," + lat + "," + lon + "\n", await _bufferedPoints);
}

Future<String> getLastGeofenceId() async {
  String id;
  try {
    id = await _readFile(await _geofenceFile);
    print("id in getLastGeofenceId : " + id);
  } catch (e) {
    return null;
  }
  if (id == "") {
    print("id == empty string");
    return null;
  }
  return id;
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
    print("point in writeJourney : " + point);
    calendarAndPos = point.split(",");
    user.addData(calendarAndPos[0], calendarAndPos[1], calendarAndPos[2]);
  }
  String jSon = json.encode(user);
  await _writeInFile(jSon + "data_splitter", await _localFile);
  user.clear();
  await _clearFile(await _bufferedPoints);
}