import 'package:intl/intl.dart';

/// This class represents a simple geolocation (latitude and longitude) along
/// with its corresponding timestamp (stored as a calendar).
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

  /// Creates a new TimedLocation from a list of dynamic elements. The type
  /// List<dynamic> is needed because this is how the arguments are transmitted
  /// from platform specific code to dart code, casting has to be done somewhere.
  /// Obviously, the list should contain at least 3 elements : latitude, longitude
  /// and the number of milliseconds that have passed since 1970.
  TimedLocation(List<dynamic> l) {
    latitude = l[0].toString();
    longitude = l[1].toString();
    int millisSince1970 = int.parse(l[2].toString());
    calendar = DateFormat('yyyy-MM-dd HH-mm-ss').format(
        DateTime.fromMillisecondsSinceEpoch(millisSince1970, isUtc: true).toLocal());
  }
}
