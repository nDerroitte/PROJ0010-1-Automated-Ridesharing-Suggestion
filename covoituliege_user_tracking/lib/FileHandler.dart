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
writeInFile(String data) async {
  File _file = await _localFile;
  RandomAccessFile file = await _file.open(mode: FileMode.append);
  file.lockSync(FileLock.blockingExclusive);
  await file.writeString('${data}data_splitter');
  file.unlockSync();
  file.close();
}

/// Delete all the content of the application local file.
clearFile() async {
  File _file = await _localFile;
  RandomAccessFile file = await _file.open(mode: FileMode.write);
  file.lockSync(FileLock.blockingExclusive);
  await file.truncate(0);
  file.unlockSync();
  file.close();
}

/// Returns the current application local file content.
/// Returns "Error reading file" in case of exception, will be improved.
Future<String> readFile() async {
  try {
    File file = await _localFile;

    // Read the file
    String contents = await file.readAsString();

    return contents;
  } catch (e) {
    print("Error reading file!");
    return "Error reading file";
  }
}
