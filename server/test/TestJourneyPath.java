import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import services.Constants;
import services.JourneyPath;
import services.Coordinate;
/**
 * Class for testing JourneyPath
 * @see [[JourneyPath]]
 */
public class TestJourneyPath{

    /**
     * Test that the hash of two equivalent JourneyPath are the same
     * @see [[JourneyPath.hashCode()]]
     */
    @Test
    public void testHash(){
        ArrayList<Coordinate> in = new ArrayList<>();
        Coordinate start = new Coordinate(-1,-1);
        Coordinate middle = new Coordinate(0,0);
        Coordinate end = new Coordinate(2,3);
        in.add(start);
        in.add(middle);
        in.add(end);

        JourneyPath a = new JourneyPath(in);
        JourneyPath b = new JourneyPath(in);

        assertTrue(a.hashCode() == b.hashCode());
    }

    /**
     * Test equal function
     * @see [[JourneyPath.equals(Obj)]]
     */
    @Test 
    public void testEqual(){
        ArrayList<Coordinate> in = new ArrayList<>();
        Coordinate start = new Coordinate(-1,-1);
        Coordinate middle = new Coordinate(0,0);
        Coordinate end = new Coordinate(2,3);
        in.add(start);
        in.add(middle);
        JourneyPath c = new JourneyPath(in);
        in.add(end);

        JourneyPath a = new JourneyPath(in);
        JourneyPath b = new JourneyPath(in);

        assertEquals(a,b);
        assertFalse(a.equals(in));
        assertFalse(a.equals(c));
    }
}