import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Habit
{
    private ArrayList<Coordinate> path;
    private ArrayList<Calendar> occurences;
    private int day;
    private double reliability;
    private long oldness;

    // Create an empty habit
    public Habit(Journey journey)
    {
        this.path = journey.getPath();
        this.occurences = new ArrayList<>();
        occurences.add(journey.getFirstPointTime());
        this.day = occurences.get(0).get(Calendar.DAY_OF_WEEK)-1;
        this.oldness = 1;
        this.reliability = 100;
    }

    public ArrayList<Coordinate> getPath()
    {
        return this.path;
    }

    public void addOccurence(Calendar date)
    {
        this.occurences.add(date);
    }
    public ArrayList<Calendar> getOccurences()
    {
        return occurences;
    }

    public int getDay()
    {
        return day;
    }

    public void updateReliability()
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        long diff = now.getTimeInMillis() - this.occurences.get(0).getTimeInMillis();
        this.oldness = 1 + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)/14;
        double r = 100*this.occurences.size()/this.oldness;
        this.reliability =  r> 100? 100 :r ;
    }

    public boolean isSame(Habit other)
    {
        if(this.day == other.getDay() && other.samePath(this.path))
            if(sameTime(other))
                return true;
        return false;
    }

    private boolean sameTime(Habit target)
    {
        Calendar temp = Calendar.getInstance();
        temp.set(this.occurences.get(0).get(Calendar.YEAR),
                this.occurences.get(0).get(Calendar.MONTH),
                this.occurences.get(0).get(Calendar.DAY_OF_MONTH),
                target.getOccurences().get(0).get(Calendar.HOUR_OF_DAY),
                target.getOccurences().get(0).get(Calendar.MINUTE),
                target.getOccurences().get(0).get(Calendar.SECOND)
                );
        long diff = this.occurences.get(0).getTimeInMillis() - temp.getTimeInMillis();
        long diff_min = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);

        if(diff_min > -Constants.MIN_DIFF_SAME_JOURNEY && diff_min < Constants.MIN_DIFF_SAME_JOURNEY)
            return true;
        return false;

    }
    private boolean samePath(ArrayList<Coordinate> target)
    {
        for(int i = 0; i< this.path.size();i++)
        {
            if(target.get(i).isSame(this.path.get(i)))
                    continue;
            else
                return false;
        }
        return true;
    }

    private double meanStarting ()
    {
        double mean = 0.0;
        for(int i =0; i< occurences.size();i++)
        {
            Calendar temp = occurences.get(i);
            double min_start = temp.get(Calendar.HOUR_OF_DAY)*60 + temp.get(Calendar.MINUTE);
            mean += min_start;
        }
        mean /= occurences.size();
        return mean;
    }

    public void print()
    {
        double mean_start = meanStarting();
        int avg_hr_start = (int) mean_start/60;
        int avg_min_start = (int) mean_start%60;
        String out = String.format("Average start : %d:%d on %s.\nReliability of this habit : %f.",
                avg_hr_start,avg_min_start, Constants.DAY_LIST.get(day), reliability );

        System.out.println(out);
    }


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
