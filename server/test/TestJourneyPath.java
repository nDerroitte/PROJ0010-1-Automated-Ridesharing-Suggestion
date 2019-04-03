import services.Constants;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import services.JourneyPath;
import services.Coordinate;

public class TestJourneyPath{

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
        System.out.println(a.toString());
        System.out.println(c.toString());
        assertFalse(a.equals(c));
    }
}