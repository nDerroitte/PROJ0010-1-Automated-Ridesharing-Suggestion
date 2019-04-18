package services;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

public class SimpleHabit extends Habit
{
    private ArrayList<Coordinate> path;
    private ArrayList<Calendar> occurences;
    private int day; //offset
    private long period;
    private double reliability;
    private long oldness;

    // Create an empty habit
    public SimpleHabit(Journey journey)
    {
        this.path = journey.getPath();
        this.occurences = new ArrayList<>();
        occurences.add(journey.getFirstPointTime());
        this.day = occurences.get(0).get(Calendar.DAY_OF_WEEK)-1;
        this.oldness = 1;
        this.period = 7;
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
        Calendar c = Calendar.getInstance();
        Collections.sort(occurences);
        int nbHabits = this.occurences.size();
        long diff = this.occurences.get(nbHabits-1).getTimeInMillis() - this.occurences.get(0).getTimeInMillis();
        c.setTimeInMillis(diff);
        float mDay = c.get(Calendar.DAY_OF_MONTH);
        System.out.println("Diff day "+mDay);
        System.out.print(mDay/7);
        this.oldness = Math.round(mDay/7);
        System.out.println("Oldness" +oldness);
        double r = 100*this.occurences.size()/this.oldness;
        this.reliability =  r> 100? 100 :r ;
    }

    public boolean isSame(SimpleHabit other)
    {
        if(this.day == other.getDay() && other.samePath(this.path))
            if(sameTime(other))
                return true;
        return false;
    }

    private boolean sameTime(SimpleHabit target)
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
        if(target.get(0).isSame(this.path.get(0)) && target.get(target.size()-1).isSame(path.get(path.size()-1)))
            return true;
        return false;
        /*
        if(target.size() < path.size())
            return false;
        for(int i = 0; i< this.path.size();i++)
        {
            if(target.get(i).isSame(this.path.get(i)))
            {
                continue;
            }
            else
            {
                return false;
            }
        }
        return true;*/
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

    @Override
    public String toString()
    {
        double mean_start = meanStarting();
        int avg_hr_start = (int) mean_start/60;
        int avg_min_start = (int) mean_start%60;
        String occuString = "";
        for( Calendar cal : occurences)
            occuString += cal.getTime();
        return String.format("Average start : %d:%d on %s.\nReliability of this habit : %f.\nNumber of occurencre : %d\nOccurences : %s\nLength path (points) : %d\nOldness : %d\n--------------------------\n",
               avg_hr_start,avg_min_start, Constants.DAY_LIST.get(day), reliability,  occurences.size(), occuString,  path.size(), oldness );
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

}
