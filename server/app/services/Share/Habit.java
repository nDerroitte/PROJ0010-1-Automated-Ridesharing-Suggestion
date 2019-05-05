package services;
import java.util.TreeSet;
import org.bson.Document;
import services.*;
import java.text.ParseException;
import java.util.Date;
import com.mongodb.client.*;
import java.util.ArrayList;
import services.EncryptionException;
import services.Decrypt;
import services.Encrypt;
import services.AES;

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
    public Document toDoc() throws EncryptionException
    {
        //Encrypt
        Document doc = new Document();
        ArrayList<Byte> period_E = Encrypt.encrypt(Long.toString(period));
        ArrayList<Byte> offset_E = Encrypt.encrypt(Long.toString(offset));
        ArrayList<Byte> reliability_E = Encrypt.encrypt(Double.toString(this.reliability));
        ArrayList<Byte> nbPoints_E = Encrypt.encrypt(Integer.toString(nbPoints));
        ArrayList<Byte> standardDeviation_E = Encrypt.encrypt(Double.toString(standardDeviation));

        doc.put("period",period_E); //Encrypt
        doc.put("offset",offset_E); //Encrypt
        doc.put("reliability",reliability_E); //Encrypt
        doc.put("firstLocation",firstLocation.toDoc()); //NE PAS Encrypt
        doc.put("lastLocation",lastLocation.toDoc()); //NE PAS Encrypt
        doc.put("nbPoints",nbPoints_E); //Encrypt
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
    public static Habit fromDoc(Document doc)throws ParseException, EncryptionException
    {
        //Decrypt
        Habit h = new Habit();
        String period_D = Decrypt.decrypt((ArrayList<Byte>)doc.get("period"));
        String offset_D = Decrypt.decrypt((ArrayList<Byte>)doc.get("offset"));
        String reliability_D = Decrypt.decrypt((ArrayList<Byte>)doc.get("reliability"));
        String nbPoints_D = Decrypt.decrypt((ArrayList<Byte>)doc.get("nbPoints"));
        String standardDeviation_D = Decrypt.decrypt((ArrayList<Byte>)doc.get("standardDeviation"));

	    h.period = Long.parseLong(period_D);//(Long) doc.get("period");
	    h.offset = Long.parseLong(offset_D);//(Long) doc.get("offset");
        h.reliability = Double.parseDouble(reliability_D) ;//(Double) doc.get("reliability");
        h.firstLocation = Coordinate.fromDoc( (Document) doc.get("firstLocation")) ;
        h.lastLocation = Coordinate.fromDoc( (Document) doc.get("lastLocation")) ;
        h.nbPoints = Integer.parseInt(nbPoints_D); //(Integer) doc.get("nbPoints");
        h.standardDeviation= Double.parseDouble(standardDeviation_D);//(Double) doc.get("standardDeviation");
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
        int period_in_minut = (int) period*24*60;
        CircularDist circ_dist = new CircularDist(period_in_minut);
        double std = Math.min(standardDeviation,h.standardDeviation);
        int offset1 = (int) ((offset / 60000) % period_in_minut);
        int offset2 = (int) ((h.offset / 60000) % period_in_minut);
        int dist = circ_dist.compute(offset1,offset2);
        if(dist < std){
            return true;
        }
        else{
            return false;
        }
    }
    
}
