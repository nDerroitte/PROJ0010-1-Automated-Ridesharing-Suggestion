package services;
import java.util.TreeSet;
import org.bson.Document;
import services.*;
import java.text.ParseException;
import java.util.Date;
import com.mongodb.client.*;
import java.util.ArrayList;
import services.EncryptionException;
import services.AES;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

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
     * First departure date of the habit
     */
    public long offset;

    /**
     * Arrival time of the habit
     */
    public long arrival_time;

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
    public Document toDoc() throws EncryptionException, UnsupportedEncodingException
    {
        Document doc = new Document();
        ArrayList<Byte> period_E = MongoDB.aes.encrypt(Long.toString(period));
        ArrayList<Byte> offset_E = MongoDB.aes.encrypt(Long.toString(offset));
        ArrayList<Byte> reliability_E = MongoDB.aes.encrypt(Double.toString(this.reliability));
        ArrayList<Byte> nbPoints_E = MongoDB.aes.encrypt(Integer.toString(nbPoints));
        ArrayList<Byte> standardDeviation_E = MongoDB.aes.encrypt(Double.toString(standardDeviation));

        doc.put("period",period_E); 
        doc.put("offset",offset_E); 
        doc.put("reliability",reliability_E); 
        doc.put("firstLocation",firstLocation.toDoc()); 
        doc.put("lastLocation",lastLocation.toDoc()); 
        doc.put("nbPoints",nbPoints_E); 
        doc.put("standardDeviation",standardDeviation_E);
        return doc;
    }

        public Document toDocNotEncrypted()
    {
        Document doc = new Document();
        doc.put("period",period); 
        doc.put("offset",offset); 
        doc.put("reliability",this.reliability); 
        doc.put("firstLocation",firstLocation.toDocNotEncrypted()); 
        doc.put("lastLocation",lastLocation.toDocNotEncrypted()); 
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
    public static Habit fromDoc(Document doc)throws ParseException, EncryptionException, IOException
    {
        Habit h = new Habit();
        String period_D = MongoDB.aes.decrypt((ArrayList<Byte>)doc.get("period"));
        String offset_D = MongoDB.aes.decrypt((ArrayList<Byte>)doc.get("offset"));
        String reliability_D = MongoDB.aes.decrypt((ArrayList<Byte>)doc.get("reliability"));
        String nbPoints_D = MongoDB.aes.decrypt((ArrayList<Byte>)doc.get("nbPoints"));
        String standardDeviation_D = MongoDB.aes.decrypt((ArrayList<Byte>)doc.get("standardDeviation"));

	    h.period = Long.parseLong(period_D);
	    h.offset = Long.parseLong(offset_D);
        h.reliability = Double.parseDouble(reliability_D) ;
        h.firstLocation = Coordinate.fromDoc( (Document) doc.get("firstLocation")) ;
        h.lastLocation = Coordinate.fromDoc( (Document) doc.get("lastLocation")) ;
        h.nbPoints = Integer.parseInt(nbPoints_D); 
        h.standardDeviation= Double.parseDouble(standardDeviation_D);
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
            return h.offset == offset && h.period == period && h.reliability == reliability 
                && nbPoints == h.nbPoints && standardDeviation == h.standardDeviation
                && firstLocation.isSame(h.firstLocation) && lastLocation.isSame(h.lastLocation);
        }
        return false;
    }

    /**
     * Return true if h and self match.
     * 
     * The two habit mathc if:
     *  -they have the same period
     *  -
     *  
     * @param h
     * @return
     */
    public boolean match(Habit h){
        if(h.period != this.period){
            return false;
        }
        if(!firstLocation.isSame(h.firstLocation) || !lastLocation.isSame(h.lastLocation)){
            return false;
        }
        int period_in_minute = (int) period*24*60;
        CircularDist circ_dist = new CircularDist(period_in_minute);
        double std = Math.min(standardDeviation,h.standardDeviation);
        System.out.println("minimul std: " + std);
        double dist = circ_dist.compute(offset,h.offset);
        System.out.println("Dist between offset: " + dist);
        if((double) dist < std){
            return true;
        }
        else{
            return false;
        }
    }
    
}
