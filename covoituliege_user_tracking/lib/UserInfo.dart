import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
import 'dart:convert';

void main() => runApp(MaterialApp(
  home:ReadingFile(),
));


class ReadingFile extends StatefulWidget {
  @override
  ReadingFileState createState() => new ReadingFileState();
}
class ReadingFileState extends State<ReadingFile>
{
  bool isReady = false;
  final TextEditingController _controller1 = new TextEditingController();
  final TextEditingController _controller2 = new TextEditingController();
  @override
  Widget build(BuildContext context) {
    return Material(
        color: Colors.grey[285],
        child: Padding(
            padding: EdgeInsets.all(20.0),
            child :  Column(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children : <Widget>[
                        Image.asset('fig/logo.png', width: 150.0, height: 150.0),
                        Text(
                          "CovoituLiège",
                          style: TextStyle(
                            fontSize: 24.0,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ]
                  ),
                  Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children : <Widget>[
                        TextFormField(
                          controller: _controller1,
                          autocorrect: false,
                          maxLength: 20,
                          maxLines: 1,
                          keyboardType: TextInputType.text,
                          decoration: new InputDecoration(hintText: "Insert Username"),
                          onEditingComplete: ()=>setState((){
                            isReady = true;
                            FocusScope.of(context).requestFocus(new FocusNode());//dismiss keyborad
                          }),
                        ),
                        TextFormField(
                          controller: _controller2,
                          autocorrect: false,
                          enabled: isReady,
                          maxLines: 1,
                          maxLength: 20,
                          obscureText: true,
                          decoration: new InputDecoration(hintText: "Insert Password"),
                        ),
                        RaisedButton(
                          color: Colors.blue,
                          child: Text("Submit"),
                          onPressed: () {
                            String name = _controller1.text;
                            String pass = _controller2.text;
                            _controller1.clear();
                            _controller2.clear();
                            Navigator.of(context).push( // Permet de changer d'écran, un truc qu'on push wesh
                              MaterialPageRoute( //On lui dit par ou passer. On crée un widget myTSAApp
                                  builder: (context) => MainButton(new UserInfo(name, pass))),
                            );
                          },
                        )
                      ]
                  )
                ]
            )
        )
    );
  }
}

class MainButton extends StatefulWidget
{
  final UserInfo user;
  MainButton(this.user);
  @override
  State<StatefulWidget> createState() => MainButtonState();
}

class MainButtonState extends State<MainButton>
{
  final TextEditingController _controller = new TextEditingController();
  String j;
  UserInfo user;

  Future<String> get _localPath async
  {
    final directory = await getApplicationDocumentsDirectory();
    return directory.path;
  }
  Future<File> get _localFile async {
    final path = await _localPath;
    return File('$path/data.txt');
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
      return "Error";
    }
  }

  @override
  initState()
  {
    super.initState();
    user = widget.user;
  }
  @override
  Widget build(BuildContext context)
  {
    return Material(
        color: Colors.grey[285],
        child: Padding(
            padding: EdgeInsets.all(20.0),
            child :  Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  TextFormField(
                    controller: _controller,
                    autocorrect: false,
                    maxLines: 1,
                    maxLength: 20,
                    decoration: new InputDecoration(hintText: "Insert Position"),
                  ),
                  RaisedButton(
                    color: Colors.blue,
                    child: Text("Submit"),
                    onPressed: () {
                      user.addData(_controller.text);
                      j = json.encode(user);
                      writeInFile(j);
                      _controller.clear();
                    },
                  ),
                  RaisedButton(
                    color: Colors.blue,
                    child: Text("View File"),
                    onPressed: () {
                      readFile().then((s)=>print(s));
                    },
                  )
                ]
            )
        )
    );
  }
}

class UserInfo
{
  var _name;
  var _password;
  Map<String,String> data;
  UserInfo(this._name,this._password);

  void addData(String pos)
  {
    if (data ==null)
      data = new Map<String,String>();
    data[DateTime.now().toString()] = pos;
  }
  void printUser()
  {
    print("Nom: $_name\nPass: $_password\nData : $data ");
  }

  UserInfo.fromJson(Map<String, dynamic> json) :
        _name = json['userName'],
        _password = json['Password'],
        data = json['Data'];


  Map<String, dynamic> toJson() =>
      {
        'UserName': _name,
        'Password': _password,
        'Data': data
      };
}