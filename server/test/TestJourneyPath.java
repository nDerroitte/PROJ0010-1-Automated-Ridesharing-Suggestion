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
    }
}