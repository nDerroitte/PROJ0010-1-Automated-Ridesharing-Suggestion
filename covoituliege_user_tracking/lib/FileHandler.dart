import 'package:path_provider/path_provider.dart';
import 'dart:io';

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

/// Write the argument in the application local file.
Future<File> writeInFile(String data) async {
  final file = await _localFile;
  return file.writeAsString('$data');
}

/// Returns the current application local file content.
/// Returns "Error reading file" in case of exception, will be improved.
Future<String> readFile() async {
  try {
    final file = await _localFile;

    // Read the file
    String contents = await file.readAsString();

    return contents;
  } catch (e) {
    print("Error reading file!");
    return "Error reading file";
  }
}
