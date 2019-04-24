package services;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Constans class containing most of the constants used for the habit creation algorithms.
 */
public abstract class Constants
{
    /**
     * Encryption constants 
     */
    public static final String k1 = "3b924dc42d627a7113078580f8851f3594caa6fa83af0a4782e28271e43ea2c3a5a7ab514e899e0cfc7481fc08f40ddf54dad2dd8d3d83cb23a48b31d79fac05";
    public static final String k2 = "bf2dfa595fcc80bd07ce8205717e049438314c19ff92f60ffabe6d6a22909aa6d9485dca67592d32359e3ab8dd308b9999a15587e770fffcfec33b3680420375";
    public static final String k3 = "f0125212839c5c48b03d3cfdaa6bf4715802e0cedc22030a0f5a6e5068fdc9f9ba3c2c95be14e0071051bfc1fd332db414bd8f04b4e5408d08d01bff98df4cc7";
    public static final String k4 = "5b6e93a15fa73195c74c97398b60e2b3a455c1bc9a93eed6565e43fb02c79e8157fd8ab038c5ed03b37273da5f152f5617c4a2df1650cc202df95b978484dff1";
    public static final String k5 = "8dd4782d7ef7f9949f09ab4e74f0ef7367eeb4e827f00be99788d5103c6c330970e1437d71ff7c5b98193cb6f5342f03c741b7a2f099af563cfebe4057e37cdf";
    public static final String k6 = "a6da29cba629dccedd3923ab02aca91017ab6dbcc3122ef1785f630040773648103a35fa9e00ec37d11732630027eab225a584031566f001ad0548bde4c1366e";
    public static final String k7 = "87f0056b10cb51e87b8b41388e4152096533750ca4b63870ccdf05b85b3d81e3329764092a743378003cbcc070c2abd3e95d2fa006427db87197c75554e5a3ed";

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
    public static final double COORDINATE_ERROR_ACCEPTED =0.01 * 2;

    /**
     * Time at which two jounreys are considered in the same habit for the simple model
     */
    public static final int MIN_DIFF_SAME_JOURNEY = 60*3;

    /**
     * Static method that allow to transform a string (yyyy-MM-dd HH-mm-ss) to a Calendar class
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
