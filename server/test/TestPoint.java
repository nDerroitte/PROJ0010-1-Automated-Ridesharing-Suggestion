import services.*;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import java.util.*;
import java.util.Calendar;
import org.bson.Document;
import java.text.ParseException;
import services.EncryptionException;
/**
 * Test the class Point
 * @see [[Point]]
 */
public class TestPoint {

    /**
     * test if a point is well written and read from Document and the equals method.
     * @throws ParseException
     */
    @Test
    public void fromto_doc_and_equal() throws ParseException, EncryptionException{
        Calendar date = Calendar.getInstance();      
        Point p = new Point(date, new Coordinate(10,20));  
        Document doc = p.toDoc();       
        Point p2 = Point.FromDoc(doc);
        assertTrue(p2.equals(p));
    }

    /**
     * Test the time getter.
     */
    @Test 
    public void getTime(){
        Calendar date = Calendar.getInstance();      
        Point p = new Point(date, new Coordinate(10,20));  
        assertTrue(p.getTime().equals(date));
    }

    /**
     * test the position getter
     */
    @Test 
    public void getPosition(){
        Calendar date = Calendar.getInstance();      
        Point p = new Point(date, new Coordinate(10,20));  
        assertTrue(p.getPosition().isSame(new Coordinate(10,20)));
    }
}