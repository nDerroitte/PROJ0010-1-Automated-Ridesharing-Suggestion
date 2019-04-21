package services;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.lang.Math;


/**
 * Reprensentation of the simple habits of the model
 */
public class SimpleHabit extends Habit
{
    /**
     * First path recorded in the habit. Will serve as reference
     */
    private ArrayList<Coordinate> path;
    /**
     * ArrayList of date where the habit happened
     */
    private ArrayList<Calendar> occurences;
    /**
     * Day of the week where the habit take place
     * [0-6]
     */
    private int day; 
    /**
     * Period always is a week
     */
    private long period;
    /**
     * Reliability (in %) of the habits
     */
    private double reliability;
    /**
     * Number of weeks of the habit
     */
    private long oldness;

    /**
     * Constructor of the SimpleHabit class
     * @param journey: the first journey of this habit
     */
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

    /**
     * Getter of the path.
     * @return : the path of the fist journey of the habit.
     */
    public ArrayList<Coordinate> getPath()
    {
        return this.path;
    }

    /**
     * Add a new occurence to the list of occurence of the habit
     * @param date: the date where the new jounrey happened
     */
    public void addOccurence(Calendar date)
    {
        this.occurences.add(date);
    }

    /**
     * Getter of the arraylist of occurences of the habits
     * @return : the array list of occurences of the habits
     */
    public ArrayList<Calendar> getOccurences()
    {
        return occurences;
    }


    /**
     * Getter of the day of the habit
     * @return: the day of the week of the habit in int [0-6]
     */
    public int getDay()
    {
        return day;
    }

    /**
     * Update the reliability of the habit
     */
    public void updateReliability()
    {
        Calendar c = Calendar.getInstance();
        // Sort the list of occurences by date.
        Collections.sort(occurences);

        int nbHabits = this.occurences.size();
        // Time difference between the fist and last journey (in mls)
        long diff = this.occurences.get(nbHabits-1).getTimeInMillis() - this.occurences.get(0).getTimeInMillis();

        // Transform that in number of week
        c.setTimeInMillis(diff);
        float mDay = c.get(Calendar.DAY_OF_MONTH);
        this.oldness = Math.round(mDay/7);
        if(oldness == 0)
            return;
        
        // Update the reliability
        double r = 100*this.occurences.size()/this.oldness;
        this.reliability =  r> 100? 100 :r ;
    }

    /**
     * Check if another SimpleHabit is the same as this
     * @param other: another simplehabit
     * @return : boolean, true if they are the same. False otherwise
     */
    public boolean isSame(SimpleHabit other)
    {
        if(this.day == other.getDay() && other.samePath(this.path))
            if(sameTime(other))
                return true;
        return false;
    }


    /**
     * Check if another SimpleHabit takes place at the same kind of hours then this one
     * @param target: the other simple habit
     * @return boolean, true if they happen the same schedule. False otherwise
     */
    private boolean sameTime(SimpleHabit target)
    {
        // Time transformation
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

        // check acording to the Constants class
        if(diff_min > -Constants.MIN_DIFF_SAME_JOURNEY && diff_min < Constants.MIN_DIFF_SAME_JOURNEY)
            return true;
        return false;

    }

    /**
     * Check if another habit has the same path then this one
     * @param target: the other habit
     * @return true if they share the same arrival/departure. False otherwise
     */
    private boolean samePath(ArrayList<Coordinate> target)
    {
        if(target.get(0).isSame(this.path.get(0)) && target.get(target.size()-1).isSame(path.get(path.size()-1)))
            return true;
        return false;
    }

    /**
     * Average the departure time of the habit
     * @return: the average departure time
     */
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

    /**
     * Overwrite the toString method
     * @return: the string correspinding to this class
     */
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
}
