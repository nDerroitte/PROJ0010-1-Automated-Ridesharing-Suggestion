import services.Habit;
import services.*;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import org.bson.Document;
import java.text.SimpleDateFormat;

public class TestHabit {

    @Test
    public void test_doc() throws Exception {
        Habit h = new Habit();
        h.period = 7;
        h.firstLocation = new Coordinate(55.2, 5.45);
        h.lastLocation = new Coordinate(54.2, 5.47);
        h.standardDeviation = 90.25;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        h.offset = sdf.parse("2019-01-10 18-15-00").getTime();
        h.nbPoints = 7;
        Document doc = h.toDoc();
        Habit h2 = Habit.fromDoc(doc);
        assertTrue(h2.equals(h));
    }

    @Test
    public void test_match() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        long base_date = sdf.parse("2019-01-09 18-35-00").getTime();
        
        // should match with itself
        Habit h = new Habit();
        h.period = 7;
        double xfirst = 55.2;
        double xlast = 54.2;
        double yfirst =  5.45;
        double ylast = 5.47;
        h.firstLocation = new Coordinate(xfirst,yfirst);
        h.lastLocation = new Coordinate(xlast,ylast);
        h.standardDeviation = 30;
        h.offset = base_date;
        assertTrue(h.match(h));

        // test with different period
        Habit h2 = new Habit();
        h2.standardDeviation = 5;
        h2.period = 8;
        h2.firstLocation = new Coordinate(xfirst, yfirst);
        h2.lastLocation = new Coordinate(xlast, ylast);
        h.standardDeviation = 30;
        assertTrue(!h.match(h2));

        // test with different location
        h2.period = 7;
        h2.firstLocation = new Coordinate(0, 0);
        assertTrue(!h.match(h2));
        h2.firstLocation = new Coordinate(xfirst,yfirst);
        h2.lastLocation = new Coordinate(0, 0);
        assertTrue(!h.match(h2));

        // test with small change in location
        h2.firstLocation = new Coordinate(xfirst + Constants.COORDINATE_ERROR_ACCEPTED / 1.1,
                yfirst - Constants.COORDINATE_ERROR_ACCEPTED / 1.1);
        h2.lastLocation = new Coordinate(xlast - Constants.COORDINATE_ERROR_ACCEPTED / 1.1,
                ylast + Constants.COORDINATE_ERROR_ACCEPTED / 1.1);
        h2.offset = base_date;
        assertTrue(h.match(h2));

        //test with small change in offset.
        System.out.println("small change in habit");
        h2.offset = base_date - ((long) (60000*0.9* Math.min(h2.standardDeviation,h.standardDeviation)));
        assertTrue(h.match(h2));
        h2.offset = base_date + ((long) (60000 *0.9*Math.min(h2.standardDeviation,h.standardDeviation)));
        assertTrue(h.match(h2));

        //test with big change in offset
        System.out.println("big change in offset");
        h2.offset = base_date - ((long) (60000*1.1* Math.min(h2.standardDeviation,h.standardDeviation)));   
        assertTrue(!h.match(h2));
        h2.offset = base_date + ((long) (60000 * 1.1*Math.min(h2.standardDeviation,h.standardDeviation)));
        assertTrue(!h.match(h2));
    }

}
