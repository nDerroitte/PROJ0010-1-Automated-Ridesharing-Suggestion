class _Point {
  String _calendar;
  String _lat;
  String _long;
  _Point(this._calendar, this._lat, this._long);

  static String toPrint(Map<String, dynamic> point) {
    StringBuffer res = StringBuffer();
    List<String> dateAndTime = point['calendar'].split(" ");
    res.writeln("Date: ${dateAndTime[0]}");
    res.writeln("Time: ${dateAndTime[1]}");
    res.writeln("Latitude: ${point['lat']}");
    res.writeln("Longitude: ${point['long']}");
    return res.toString();
  }

  _Point.fromJson(Map<String, dynamic> json)
    : _calendar = json['calendar'],
      _lat = json['lat'],
      _long = json['long'];

  Map<String, dynamic> toJson() =>
      {
        'calendar': _calendar,
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

  void addData(String calendar, String lat, String long) {
    _points += <_Point>[_Point(calendar, lat, long)];
  }

  Map<String, dynamic> toJson() => {'UserId': _userId, 'Points': _points};
}
