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

    public static final double DECIMALS_ROUNDING_POSITION = 4;
    public static final int COORDINATE_ERROR_ACCEPTED =5;

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
        long x = 0;
        long y = 0;
        double tmp_x = Math.floor(lat*Math.pow(10,DECIMALS_ROUNDING_POSITION));
        double tmp_y = Math.floor(lon*Math.pow(10,DECIMALS_ROUNDING_POSITION));

        x = (long)Math.floor(tmp_x - 50 * Math.pow(10,DECIMALS_ROUNDING_POSITION));
        y = (long)Math.floor(tmp_y - 50 * Math.pow(10,DECIMALS_ROUNDING_POSITION));

        return new Coordinate(x,y);
    }
}