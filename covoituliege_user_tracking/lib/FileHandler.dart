import 'package:path_provider/path_provider.dart';
import 'dart:io';

Future<String> get _localPath async {
  final directory = await getApplicationDocumentsDirectory();
  return directory.path;
}

Future<File> get _localFile async {
  final path = await _localPath;
  return File('$path/data.json');
}

Future<File> writeInFile(String jsonString) async {
  final file = await _localFile;
  return file.writeAsString('$jsonString');
}

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