import java.util.TreeSet;
import org.bson.Document;


public class Habits
{
    private long period;
    private long offset;
    private int journey_ID;
    private TreeSet<Long> dates; //contain all the date which is in the habit.
    private long last_date; //the last day of this journey, whether it is in the habit.



    public Habits(long period, long offset, int journey_ID)
    {
        this.period = period;
        this.offset = offset % period;
        this.journey_ID  = journey_ID;
        this.dates = new TreeSet<>();
        this.last_date = 0;
    }
    public Habits(long period, long offset, int journey_ID,TreeSet<Long> dates,long last_date)
    {
        this.period = period;
        this.offset = offset % period;
        this.journey_ID  = journey_ID;
        this.dates = dates;
        this.last_date = last_date;
    }
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
    public int getHit()
    {
        return dates.size();
    }
    public long getTotal()
    {
        if(dates.size() == 0){
            return 0;
        }
        return (this.last_date - dates.first()) / this.period;
    }
    public float getHitRate()
    {
        return (float) this.getHit()/ (float) this.getTotal();
    }
    //return true if x does not aport new information regarding this
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
    public void update(long new_date)
    {
        if(!inHabit(new_date))
        {
            if(this.last_date < new_date)
            {
                this.last_date = new_date;
            }
        }
        else
        {
            this.dates.add(new_date);
            return;
        }
    }
    public void print(){
        System.out.println("Period " + this.period +" Journey id "+ this.journey_ID+ " offset " + this.offset + " hit: " + this.getHit() + " total " + this.getTotal());
    }

    public Document toDoc()
    {
        Document doc = new Document();
        doc.put("period",this.period);
        doc.put("offset",this.offset);
        doc.put("journey_ID",this.journey_ID);
        doc.put("dates",this.dates);
        doc.put("last_date",this.last_date);
        return doc;
    }

    public static Habits FromDoc(Document doc)
    {
        long period = doc.get("period");
        long offset = doc.get("offset");
        int journey_ID = doc.get("journey_ID");
        TreeSet<Long> dates = doc.get("dates");
        long last_date = doc.get("last_date");
        return new Habits(period, offset, journey_ID, dates, last_date);
    }
}
