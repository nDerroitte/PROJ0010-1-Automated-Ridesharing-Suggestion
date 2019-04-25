package services;
import java.util.TreeSet;
import org.bson.Document;
import services.*;
import java.text.ParseException;
import java.util.Date;
import com.mongodb.client.*;


/**
 * Parent class of the two habit class. Allow (mainly) to generate/get the doc for the database
 */
public class Habit
{    
    /**
     * Period of the habit
     */
    public Coordinate firstLocation;
    /**
     * Period of the habit
     */
    public Coordinate lastLocation;
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
     * Number of occurences
     */
    public int nbPoints;
    /**
     * standard Deviation
     */
    public double standardDeviation;

    /**
     * Transform the habit to the Document format
     * @return the document correspondng to the habit
     */
    public Document toDoc()
    {
        //Encrypt
        Document doc = new Document();
        doc.put("period",period); //Encrypt
        doc.put("offset",offset); //Encrypt
        doc.put("reliability",this.reliability); //Encrypt
        doc.put("firstLocation",firstLocation.toDoc()); //NE PAS Encrypt
        doc.put("lastLocation",lastLocation.toDoc()); //NE PAS Encrypt
        doc.put("nbPoints",nbPoints); //Encrypt
        return doc;
    }

        public Document toDocNotEncrypted()
    {
        Document doc = new Document();
        doc.put("period",period); 
        doc.put("offset",offset); 
        doc.put("reliability",this.reliability); 
        doc.put("firstLocation",firstLocation.toDoc()); 
        doc.put("lastLocation",lastLocation.toDoc()); 
        doc.put("nbPoints",nbPoints);
        doc.put("standardDeviation",standardDeviation);
        return doc;
    }

    /**
     * Restore an habit from a document
     * @param doc Document to transform
     * @return h the habit generated
     * @throws ParseException
     */
    public static Habit fromDoc(Document doc)throws ParseException
    {
        //Decrypt
        Habit h = new Habit();
	    h.period = (Long) doc.get("period");
	    h.offset = (Long) doc.get("offset");
        h.reliability = (Double) doc.get("reliability");
        h.firstLocation = (Coordinate) doc.get("firstLocation");
        h.lastLocation = (Coordinate) doc.get("lastLocation");
        h.nbPoints = (Integer) doc.get("nbPoints");
        h.standardDeviation= (Double) doc.get("standardDeviation");
	    return h;
    }

    /**
     * Override the toString method
     * @return  the string corresponding to this class
     */
    public String toString(){
        double standardDeviationDisp  = Math.round(standardDeviation*100.0)/100.0;
        return "Period: " + period + "\nReliability: " + reliability + "\nOffset: " + new Date(offset).toString() + "\nFirst Location: " + firstLocation + "\nLast Location: " + lastLocation  + "\nNumber of occurrences: " + nbPoints+"\nStandard Deviation: " +standardDeviationDisp + "min."; 
    }


    /**
     * Override the equals method to check if two habits are similar
     * @return  true if they are similar. False otherwise
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
