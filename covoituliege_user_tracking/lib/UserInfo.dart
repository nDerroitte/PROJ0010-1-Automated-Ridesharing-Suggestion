/// This class represents the points captured for the currently connected user,
/// it's used to convert easily the data to and from a JSon format.
/// The format is not perfect yet, it should be changed soon.
class UserInfo {
  String _name;
  String _data;
  UserInfo(this._name);

  void addData(String data) {
    if (_data == null)
      _data = data;
    else
      _data = _data + data;
  }

  void printUser() {
    print("Nom: $_name\nData : $_data ");
  }

  UserInfo.fromJson(Map<String, dynamic> json)
      : _name = json['userName'],
        _data = json['Data'];

  Map<String, dynamic> toJson() => {'UserName': _name, 'Data': _data};
}
