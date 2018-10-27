class UserInfo
{
  var _name;
  Map<String,String> data;
  UserInfo(this._name);

  void addData(String pos)
  {
    if (data ==null)
      data = new Map<String,String>();
    data[DateTime.now().toString()] = pos;
  }
  void printUser()
  {
    print("Nom: $_name\nData : $data ");
  }

  UserInfo.fromJson(Map<String, dynamic> json) :
        _name = json['userName'],
        data = json['Data'];


  Map<String, dynamic> toJson() =>
      {
        'UserName': _name,
        'Data': data
      };
}