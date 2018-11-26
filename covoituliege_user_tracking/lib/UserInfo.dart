class _Point {
  String _date;
  String _time;
  String _lat;
  String _long;
  _Point(this._date, this._time, this._lat, this._long);

  static String toPrint(Map<String, dynamic> point) {
    StringBuffer res = StringBuffer();
    res.writeln("Date: ${point['date']}");
    res.writeln("Time: ${point['time']}");
    res.writeln("Latitude: ${point['lat']}");
    res.writeln("Longitude: ${point['long']}");
    return res.toString();
  }

  _Point.fromJson(Map<String, dynamic> json)
    : _date = json['date'],
      _time = json['time'],
      _lat = json['lat'],
      _long = json['long'];

  Map<String, dynamic> toJson() =>
      {
        'date': _date,
        'time': _time,
        'lat': _lat,
        'long': _long,
      };
}


/// This class represents the points captured for the currently connected user,
/// it's used to convert easily the data to and from a JSon format.
class UserInfo {
  String _userId;
  List<_Point> _points;
  UserInfo(this._userId) {
    _points = <_Point>[];
  }

  static String toPrint(Map<String, dynamic> userInfo) {
    StringBuffer res = StringBuffer();
    res.writeln("UserId: ${userInfo["UserId"]}\n");
    List<dynamic> points = userInfo["Points"];
    for (dynamic point in points) {
      res.writeln(_Point.toPrint(point));
    }
    return res.toString();
  }

  Map<String, dynamic> getLastPos() {
    if (_points.length > 0) {
      return _points[_points.length - 1].toJson();
    } else {
      return null;
    }
  }

  void addData(String date, String time, String lat, String long) {
    _points += <_Point>[_Point(date, time, lat, long)];
  }

  Map<String, dynamic> toJson() => {'UserId': _userId, 'Points': _points};
}
