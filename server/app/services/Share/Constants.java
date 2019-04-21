package services;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Constans class containing most of the constants used for the habit creation algorithms.
 */
public final class Constants
{
    /**
     * This is a final class. Should never be instantiated
     */
    private Constants() {
        System.err.println("This class should never be instantiate.");
        System.exit(1);
    }

    /**
     * List of day. Allow to easily transform int to string for the day
     */
    public static final List<String> DAY_LIST = Arrays.asList("SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY");

    /**
     * Parameter for rounding the coordinates 
     * ROUNDING gives the rounding of each coordinates and COORDINATE_ERROR_ACCEPTED gives
     * the precision at which the coordinates are considered the same. 
     * The two should be equals
     */
    public static final double ROUNDING = 0.01;
    public static final double COORDINATE_ERROR_ACCEPTED =0.01;

    /**
     * Time at which two jounreys are considered in the same habit for the simple model
     */
    public static final int MIN_DIFF_SAME_JOURNEY = 60*3;

    /**
     * Static methid that allow to transform a string (yyyy-MM-dd HH-mm-ss) to a Calendar class
     * @param str_date the string to transform
     * @return the corresponding Calendar
     * @throws ParseException if the parse of the string failed
     */
    public static  Calendar stringToCalendar(String str_date) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date date = sdf.parse(str_date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Transform the coordinates taken by the mobile into coordinates that the alogrithm can 
     * work on.
     * We do not work with the altitude at the moment.
     * @param lat the lattitude coordinate
     * @param lon the longitude coordinate
     * @return a coordinate point of the rounded localisation
     */
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
