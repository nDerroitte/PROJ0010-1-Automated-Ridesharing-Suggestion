import 'package:intl/intl.dart';

class TimedLocation {
  String latitude;
  String longitude;
  String calendar;

  // Not used anymore, kept just in case.
  TimedLocation.fromExplicit(String latitude, String longitude, String calendar) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.calendar = calendar;
  }

  TimedLocation(List<dynamic> l) {
    latitude = l[0].toString();
    longitude = l[1].toString();
    int millisSince1970 = int.parse(l[2].toString());
    calendar = DateFormat('yyyy-MM-dd HH-mm-ss').format(
        DateTime.fromMillisecondsSinceEpoch(millisSince1970, isUtc: true).toLocal());
  }
}
