import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
s
import services.Journey;
import services.JourneyPath;
import services.Point;
import services.Coordinate;

public class TestUserGM{

    private UserGM user;

    @Test
    public void sortByPath(){
        String userID = "test";
        int mode = 0;

        Coordinate[] starts = {new Coordinate(-10,-10),new Coordinate(-100,-100)};
        Coordinate[] ends = {new Coordinate(10,10),new Coordinate(100,100)} ;
        Calendar c = new Calendar();
        for(int path=0; path < start.length;path++){
            start = starts[i];
            end = ends[i];
            ArrayList<Point> meeting = new arrayList<>();
            meeting.add(new Point(c,start));
            for(int i=0; i < 10 ;i++){
                Coordinate coord = new Coordinate(Math.random(),Math.random());
                meeting.add(new Point(c,coord));
            }    
            meeting.add(new Point(c,end));        
        }




    }
    @Test 
    public void sortJourneyByPath(){

    }
}