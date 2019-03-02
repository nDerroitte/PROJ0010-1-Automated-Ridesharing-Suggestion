package services;
import java.util.TreeSet;
import org.bson.Document;
import services.*;
import java.text.ParseException;

/*
An habit is considered to be periodic.
*/
public class Habits
{    
    public long period;
    public long offset;
    public double spread;
    public double reliability;
 
    /* transform habit to document for storage purpose.
    public Document toDoc()
    {
        Document doc = new Document();
        doc.put("period",this.period);
        doc.put("offset",this.offset);
        doc.put("spread",this.journey.toDoc());
        doc.put("reliability",this.journey_ID);
        return doc;
    }
    //Restore an habit from a document;
    public static Habits fromDoc(Document doc)throws ParseException
    {
        Habits h = new Habits();
	h.period = (Long) doc.get("period");
	h.offset = (Long) doc.get("offset");
	h.spread = (Double) doc.get("spread");
	h.reliability = (Double) doc.get("reliability");
	return h;
    }
    */
}
