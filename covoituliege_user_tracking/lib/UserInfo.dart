class UserInfo
{
  var _name;
  Map<String,String> data;
  UserInfo(this._name);

  void addData(String pos)
  {
    if (data ==null)
      data = new Map<String,String>();
    String dateTime = DateTime.now().toString().replaceFirst(":", "h").replaceFirst(":", "m").replaceFirst(".", "s");
    dateTime = dateTime.substring(0, dateTime.indexOf("s") + 1);
    data[dateTime] = pos;
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