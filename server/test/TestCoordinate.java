import services.Coordinate;
import services.Point;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import services.Constants;
/**
 * Test the Coordinate class
 */
public class TestCoordinate {

    /**
     * Check the coordinate is well constructed.
     */
    @Test
    public void getXgetY(){
        double x = 10;
        double y = 15;
        Coordinate c = new Coordinate(x,y);
        assertTrue(c.getX() == x && c.getY() == y);
    }

    /**
     * Test isSame
     * @see [[Coordinate.isSame()]]
     */
    @Test
    public void isSame(){
        double x1 = 10;
        double y1 = 20;
        double x2 = x1 + Constants.COORDINATE_ERROR_ACCEPTED/1.00001;
        double y2 = y1 + Constants.COORDINATE_ERROR_ACCEPTED/1.00001;
        double x3 = x1 + 1.00001 * Constants.COORDINATE_ERROR_ACCEPTED;
        double y3 = y1 + 1.00001 * Constants.COORDINATE_ERROR_ACCEPTED;
        double x4 = x1 - Constants.COORDINATE_ERROR_ACCEPTED/1.00001;
        double y4 = y1 - Constants.COORDINATE_ERROR_ACCEPTED/1.00001;
        double x5 = x1 - 1.00001 * Constants.COORDINATE_ERROR_ACCEPTED;
        double y5 = y1 - 1.00001 * Constants.COORDINATE_ERROR_ACCEPTED;   

        assertTrue(new Coordinate(x1,y1).isSame(new Coordinate(x2,y2)) 
            && new Coordinate(x2,y2).isSame(new Coordinate(x1,y1)));

        assertTrue(! new Coordinate(x1,y1).isSame(new Coordinate(x3,y3)) 
            && ! new Coordinate(x3,y3).isSame(new Coordinate(x1,y1)));

        assertTrue( new Coordinate(x1,y1).isSame(new Coordinate(x4,y4)) 
            &&  new Coordinate(x4,y4).isSame(new Coordinate(x1,y1)));
        
        assertTrue(! new Coordinate(x1,y1).isSame(new Coordinate(x5,y5)) 
            && ! new Coordinate(x5,y5).isSame(new Coordinate(x1,y1)));
    }

    /**
     * Test toString()
     * @see [[Coordinate.toString()]]
     */
    @Test
    public void testToString(){
        double x = 10;
        double y = 20;
        Coordinate c = new Coordinate(x,y);
        String s = c.toString();
        String[] split = s.split(";");
        split[0] = split[0].substring(1,split[0].length());
        split[1] = split[1].substring(0,split[1].length()-1);
        System.out.println(s);
        System.out.println(split[0]);
        System.out.println(split[1]);
        assertTrue(split.length == 2 && Double.parseDouble(split[0]) == x && Double.parseDouble(split[1]) == y);
    }
}