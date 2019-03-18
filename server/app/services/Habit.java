import java.util.ArrayList;

public abstract class Habit
{
    private ArrayList<Coordinate> path;
    private int offset; // min from 1970
    private long period; // day
    private double reliability;

    public abstract void print();

    //transform habit to document for storage purpose.
    /*
    public Document toDoc()
    {
        Document doc = new Document();
        doc.put("period",this.period);
        doc.put("offset",this.offset);
        doc.put("journey",this.journey.toDoc());
        doc.put("journey_ID",this.journey_ID);
        doc.put("dates",this.dates);
        doc.put("last_date",this.last_date);
        return doc;
    }
    //Restore an habit from a document;
    public static Habits fromDoc(Document doc)throws ParseException
    {
        long period = (Long) doc.get("period");
        long offset = (Long) doc.get("offset");
        Journey journey = Journey.fromDoc((Document) doc.get("journey"));
        int journey_ID = (Integer) doc.get("journey_ID");
        TreeSet<Long> dates = (TreeSet<Long>)doc.get("dates");
        long last_date = (Long) doc.get("last_date");
        return new Habits(period, offset, journey,journey_ID, dates, last_date);
    }
    */

}
