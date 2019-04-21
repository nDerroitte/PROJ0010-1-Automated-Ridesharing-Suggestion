package services;
import java.util.TreeSet;
import org.bson.Document;
import services.*;
import java.text.ParseException;
import java.util.Date;


/**
 * Parent class of the two habit class. Allow (mainly) to generate/get the doc for the database
 */
public class Habit
{    
    /**
     * Period of the habit
     */
    public long period;
    /**
     * Offset of the habit
     */
    public long offset;
    /**
     * Reliability of the habit
     */
    public double reliability;
 
    /**
     * Transform the habit to the Document format
     * @return the document correspondng to the habit
     */
    public Document toDoc()
    {
        Document doc = new Document();
        doc.put("period",this.period);
        doc.put("offset",this.offset);
        doc.put("reliability",this.reliability);
        return doc;
    }

    /**
     * Restore an habit from a document
     * @param doc Document to transform
     * @return the habit generated
     * @throws ParseException
     */
    public static Habit fromDoc(Document doc)throws ParseException
    {
        Habit h = new Habit();
	    h.period = (Long) doc.get("period");
	    h.offset = (Long) doc.get("offset");
	    h.reliability = (Double) doc.get("reliability");
	    return h;
    }

    /**
     * Override the toString method
     * @return: the string correspinding to this class
     */
    public String toString(){
        return "\n period: " + period + "\n reliability: " + reliability + "\n offset: " + new Date(offset).toString(); 
    }


    /**
     * Override the equals method to check if two habits re similar
     * @return: true if they are similar. False otherwise
     */
    @Override
    public boolean equals(Object o){
        if( o instanceof Habit){
            Habit h = (Habit) o;
            return h.offset == offset && h.period == period && h.reliability == reliability;
        }
        return false;
    }
    
}
