
import services.Point;
import services.Coordinate;
import services.Journey;
import services.*;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import java.util.*;
import java.util.Calendar;
import org.bson.Document;
import java.text.ParseException;
import services.EncryptionException;

public class TestJourney {


    public void fromto_doc_and_equal(Journey journey) throws ParseException, EncryptionException{
        Journey unchanged = journey;             
        Document doc = journey.toDoc();       
        Journey changed = Journey.fromDoc(doc);
        assertTrue(unchanged.equals(changed));
    }
}