package services;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.MongoCollection;
import services.*;

public class User
{
    public ArrayList<Habits> user_habits;
    private ArrayList<Journey> unused_journeys;
    private String user_id;
    private MongoCollection<Document> users;

    public User(String user_id, ArrayList<Journey> journeys,MongoCollection<Document> DBusers)
    {
        this.user_habits = new ArrayList<>();
        this.unused_journeys = journeys;
        this.user_id = user_id;
        this.users = DBusers;
    }
    //for test only
    public User(){
        ;
    }

    public void addJourney(Journey new_journey)
    {
        unused_journeys.add(new_journey);
    }

    public void createHabits()
    {
        if( this.unused_journeys.size()<20)
            return;

        ArrayList<ArrayList<Journey>> journey_list = new ArrayList<>();
        boolean to_add ;
        int i,j;
        //1. Vérifier trajet
        for ( i =1;i<unused_journeys.size();i++)
        {
            to_add = true;
            for(j =0;j<journey_list.size();j++)
            {
                if(journey_list.get(j).get(0).sameJourney(unused_journeys.get(i)))
                {
                    journey_list.get(j).add(unused_journeys.get(i));
                    to_add = false;
                    break;
                }
            }
            if(to_add)
            {
                journey_list.add(new ArrayList<>());
                journey_list.get(j).add(unused_journeys.get(i));
            }
        }

        //Check nouveau + unused
        ArrayList<Long> long_array = new ArrayList<>();
        ArrayList<Habits> habits = new ArrayList<>();
        for (i =0; i<journey_list.size();i++)
        {
            for(j =0;j<journey_list.get(i).size();j++)
            {
                long_array.add(journey_list.get(i).get(j).getFirstPointTime());
            }
            habits.addAll(getHabits(long_array, journey_list.get(i).get(0), i));
            long_array.clear();
        }
        
        //write result in DB
        LinkedList<Document> docs = new LinkedList<>();
        for(Habits habit: habits){
            docs.add(habit.toDoc());
        }
        users.updateOne(eq("user",this.user_id),set("habit",docs));
    }
    public ArrayList<Habits> getHabits(ArrayList<Long> array, Journey journey,int journey_id)
    {
        Collections.sort(array);
        ArrayList<Habits> habits = new ArrayList<Habits>();

        for(int i = array.size() - 1; i > 0; i--)
        {
            for(int j = i-1; j > 0; j--)
            {
                long period = 0;
                period = array.get(i) - array.get(j);
                if(period <= 0){
                    continue;
                }
                //System.out.println("current period is : " + period +  " current offset is: " + array.get(i));
                Habits cur_habit = new Habits(period,array.get(i), journey, journey_id);
                if(isRedundant(habits,cur_habit))
                {
                    //System.out.println("HABIT ALREADY FIND !");
                    continue;
                }
                long cur_date = (array.get(array.size()-1) / period) * period + cur_habit.getOffset();
                //Only consider the 400 last dates.
                int max_attempt = 400;
                int attempt = 0;
                while((attempt < 3 || cur_habit.getHit()/(float)attempt > Constants.MIN_HIT_RATE) && (cur_date > array.get(0) && max_attempt > 0))
                {
                    if(Collections.binarySearch(array, cur_date) > 0){
                        cur_habit.update(cur_date);
                    }
                    cur_date -= period;
                    attempt ++;
                }
                cur_habit.update(array.get(array.size()-1));
                if(cur_habit.getHit() > 3 && cur_habit.getHitRate() > Constants.MIN_HIT_RATE)
                {
                    habits.add(cur_habit);
                    //System.out.println("find period of: " + cur_habit.getPeriod() +  " with an offset of " + cur_habit.getHit() + " hit rate:" + cur_habit.getHitRate());
                    //System.out.println("hit : " + cur_habit.getHit() + " total: " + cur_habit.getTotal());
                }
                //System.out.println("hit: " + cur_habit.getHit() + " total " + cur_habit.getTotal());
            }
        }
        //this.user_habits.addAll(habits);

        return habits;
    }
    private static boolean isRedundant(ArrayList<Habits> habits,Habits candidate)
    {
        Iterator<Habits> ite = habits.iterator();
        while (ite.hasNext()) {
            if (ite.next().equivalent(candidate)) {
                return true;
            }
        }
        return false;
    }
}
