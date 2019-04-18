package services;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class Constants
{
    private Constants() {
        System.err.println("This class should never be instantiate.");
        System.exit(1);
    }
    public static final List<String> DAY_LIST = Arrays.asList("SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY");

    public static final double ROUNDING = 0.01;
    public static final double COORDINATE_ERROR_ACCEPTED =0.01;

    public static final int MIN_DIFF_SAME_JOURNEY = 60*3;

    public static  Calendar stringToCalendar(String str_date) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date date = sdf.parse(str_date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    public static Coordinate CoordinateTransformation(double lat, double lon)
    {
        double x = 0;
        double y = 0;
        x = Math.round(lat/ROUNDING);
        y = Math.round(lon/ROUNDING);
        x *= ROUNDING;
        y *= ROUNDING;
        
        return new Coordinate(x,y);
    }
}
