package services;
import java.util.TreeSet;
import org.bson.Document;

/*
An habit is considered to be periodic.
*/
public class Habits
{
    private long period;
    private long offset;
    private Journey journey;
    private int journey_ID; //identify to which journey correspond the habit.
    private TreeSet<Long> dates; //contain all the date which is in the habit.
    private long last_date; //the last day of the journey_ID, whether it is in the habit.

    //create an empty habit
    public Habits(long period, long offset, Journey journey, int journey_ID)
    {
        this.period = period;
        this.offset = offset % period;
        this.journey = journey;
        this.journey_ID  = journey_ID;
        this.dates = new TreeSet<>();
        this.last_date = 0;
    }
    //create an habit
    public Habits(long period, long offset, Journey journey, int journey_ID, TreeSet<Long> dates,long last_date)
    {
        this.period = period;
        this.offset = offset % period;
        this.journey = journey;
        this.journey_ID  = journey_ID;
        this.dates = dates;
        this.last_date = last_date;
    }
    //check if a date is in the habit.
    public boolean inHabit(long date)
    {
        return date % this.period == this.offset;
    }
    public long getPeriod(){
        return this.period;
    }
    public long getOffset(){
        return this.offset;
    }
    //number of date which is in the habit
    public int getHit()
    {
        return dates.size();
    }
    //number of date which should be in the habit.
    public long getTotal()
    {
        if(dates.size() == 0){
            return 0;
        }
        return (this.last_date - dates.first()) / this.period + 1;
    }
    //Clue on the reliability of the habit. If hit rate close to 1, the habit is reliable. Always between 0 and 1
    public float getHitRate()
    {
        return (float) this.getHit()/ (float) this.getTotal();
    }
    //return true if x does not bring new information regarding this
    //(Ex: if this.period = 10 and this.offset = 0, x.period = 20 and x.offset = 0, x doesn't bring new information)
    public boolean equivalent(Habits x){
        if(x.getPeriod() % period != 0){
            return false;
        }
        return x.getOffset() % period == offset;
    }
    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        if (!(obj instanceof Habits))
            return false;
        if (obj == this)
            return true;
        return this.getOffset() == ((Habits) obj).getOffset() &&
                ((Habits) obj).getPeriod() == this.getPeriod();
    }
    @Override
    public int hashCode(){
        return Long.hashCode(this.offset) % (Integer.MAX_VALUE/2) + Long.hashCode(this.period) % (Integer.MAX_VALUE/2);
    }
    //update the habit from the new knowledge bring by new_date.
    public void update(long new_date)
    {
        if(this.last_date < new_date)
        {
            this.last_date = new_date;
        }
        if(inHabit(new_date))
        {
            this.dates.add(new_date);
            return;
        }
    }
    public void print(){
        System.out.println("Period " + this.period +" Journey id "+ this.journey_ID+ " offset " + this.offset + " hit: " + this.getHit() + " total " + this.getTotal());
    }
    //transform habit to document for storage purpose.
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
        Jounrey journey = Journey.FromDoc(doc.get("journey"));
        int journey_ID = (Integer) doc.get("journey_ID");
        TreeSet<Long> dates = (TreeSet<Long>)doc.get("dates");
        long last_date = (Long) doc.get("last_date");
        return new Habits(period, offset, journey_ID, dates, last_date);
    }
    //round
    //Constants.ROUND_PARAM
    private long round(long x){
        if(x % Constants.ROUND_PARAM < Constants.ROUND_PARAM / 2)
           return (x / Constants.ROUND_PARAM) * Constants.ROUND_PARAM;
        else
            return ((x / Constants.ROUND_PARAM) + 1) * Constants.ROUND_PARAM;
    }
}
