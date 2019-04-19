//package test;
import services.Constants;

import org.junit.Test;
import services.Coordinate;
import static org.junit.Assert.*;

/**
 * Class for testing Constants
 */
public class TestConstants{

    /**
     * Test is the Coordinate rounding is well performed
     */
    @Test
    public void CoordinateTransfo(){
        double lat = 1;
        double lon = 1;
        Coordinate a =  new Coordinate(lat,lon);
        double epsilon = Constants.ROUNDING / 10;
        assertTrue(a.isSame(Constants.CoordinateTransformation(lat+epsilon,lon+epsilon)));
        assertFalse(a.equals(Constants.CoordinateTransformation(lat+epsilon*10.1,lon+epsilon*10.1)));
    }
}