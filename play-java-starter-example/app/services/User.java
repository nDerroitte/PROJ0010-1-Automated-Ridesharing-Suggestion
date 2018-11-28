package services;
import java.util.ArrayList;
import java.util.Iterator;


public class User
{
    public ArrayList<Habits> user_habits;
    private ArrayList<Journey> unused_journeys;
    private String user_id;

    public User()
    {
        this.user_habits = new ArrayList<>();
        this.unused_journeys = new ArrayList<>();
    }
    public User(String user_id, ArrayList<Journey> journeys)
    {
        this.user_id = user_id;
        this.user_habits = new ArrayList<>();
        this.unused_journeys = journeys;
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
        for (i =0; i<journey_list.size();i++)
        {
            for(j =0;j<journey_list.get(i).size();j++)
            {
                long_array.add(journey_list.get(i).get(j).getFirstPointTime());
            }
            getHabits(long_array,i);
            long_array.clear();

        }

    }
    public ArrayList<Habits> getHabits(ArrayList<Long> array, int journey_id)
    {
        ArrayList<Habits> habits = new ArrayList<Habits>();

        for(int i = array.size() - 1; i > 0; i--)
        {
            for(int j = i-1; j > 0; j--)
            {
                long period = 0;
                if(array.get(i) > array.get(j)){
                    period = array.get(i) - array.get(j);
                }
                else if(array.get(i) < array.get(j)){
                    period = array.get(j) - array.get(i);
                }
                else{
                    continue;
                }
                System.out.println("current period is : " + period +  " current offset is: " + array.get(i));
                Habits cur_habit = new Habits(period,array.get(i),journey_id);
                if(isRedundant(habits,cur_habit))
                {
                    System.out.println("HABIT ALREADY FOUND !");
                    continue;
                }
                int k = i;
                while((cur_habit.getTotal() < 3 || cur_habit.getHitRate() > 0.8) && k > 0)
                {
                    cur_habit.update(array.get(k));
                    k--;
                }
                if(cur_habit.getHit() > 3 && cur_habit.getHitRate() > 0.8)
                {
                    habits.add(cur_habit);
                    System.out.println("find period of: " + cur_habit.getPeriod() +  " with an offset of " + cur_habit.getHit() + " hit rate:" + cur_habit.getHitRate());
                    System.out.println("hit : " + cur_habit.getHit() + " total: " + cur_habit.getTotal());
                }
                System.out.println("hit: " + cur_habit.getHit() + " total " + cur_habit.getTotal());
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
