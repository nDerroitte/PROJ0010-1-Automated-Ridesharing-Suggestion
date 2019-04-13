package services;
import java.util.TreeSet;
import org.bson.Document;
import services.*;
import java.text.ParseException;

/*
An habit is considered to be periodic.
*/
public class Habit
{    
    public long period;
    public long offset;
    public double reliability;
 
    //transform habit to document for storage purpose.
    public Document toDoc()
    {
        Document doc = new Document();
        doc.put("period",this.period);
        doc.put("offset",this.offset);
        doc.put("reliability",this.reliability);
        return doc;
    }
    //Restore an habit from a document;
    public static Habit fromDoc(Document doc)throws ParseException
    {
    Habit h = new Habit();
	h.period = (Long) doc.get("period");
	h.offset = (Long) doc.get("offset");
	h.reliability = (Double) doc.get("reliability");
	return h;
    }
    public String toString(){
        return "period: " + period + " reliability: " + reliability + " offset: " + offset; 
    }

    public void print()
    {
        System.out.println(this.toString());
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Habit){
            Habit h = (Habit) o;
            return h.offset == offset && h.period == period && h.reliability == reliability;
        }
        return false;
    }
    
}
