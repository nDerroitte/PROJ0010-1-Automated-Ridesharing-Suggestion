package services;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
public class User
{
    private ArrayList<Journey> unused_journeys;
    private String user_id;
    private ArrayList<ArrayList<Habit>> habits;

    public User(String user_id, ArrayList<Journey> journeys)
    {
        this.unused_journeys = journeys;
        this.user_id = user_id;
        this.habits = new ArrayList<>(7);
        for(int i=0; i<7;i++)
        {
            habits.add(new ArrayList<>());
        }
    }

    public void createHabits()
    {
        //natan stuff
        for(int i =0; i< unused_journeys.size();i++)
        {
            Calendar date_journey = unused_journeys.get(i).getFirstPointTime();
            int dow = date_journey.get(Calendar.DAY_OF_WEEK)-1;
            addHabits(this.habits.get(dow), unused_journeys.get(i));
        }

        //cedric stuff without sorting by day
        HashMap<JourneyPath,ArrayList<Journey>> sorted_journey = sortJourneyByPath();
        Iterator it = sorted_journey.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ArrayList<Journey> data = (ArrayList<Journey>)pair.getValue();
            ComputeHabit computer = new ComputeHabit(journeyToLong(data));
            LinkedList<Habits> habits = computer.getHabit();
            printHabits(habits);
            //cedric stuff with sorting by day.
            HashMap<Integer,ArrayList<Journey>> journey_by_day = sortJourneyByDay(data);
            Iterator byday = journey_by_day.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                ArrayList<Journey> databyday = (ArrayList<Journey>)entry.getValue();
                ComputeHabit ch = new ComputeHabit(journeyToLong(databyday));
                LinkedList<Habits> habitsbyday = computer.getHabit();
                printHabits(habitsbyday);
            }       
        }
    }
    public void printHabits(LinkedList<Habits> habits){
        for(Habits habit : habits){
            System.out.println(habit.toString());
        }
    }

    HashMap<Integer,ArrayList<Journey>> sortJourneyByDay(ArrayList<Journey> journeys){
        HashMap<Integer,ArrayList<Journey>> out = new HashMap<>();
        for(Journey journey : journeys){
            Calendar date_journey = journey.getFirstPointTime();
            int key = date_journey.get(Calendar.DAY_OF_WEEK)-1; 
            if(out.containsKey(key)){
                out.get(key).add(journey);  
            }
            else{
                ArrayList<Journey> array = new ArrayList<>();
                array.add(journey);
                out.put(key,array);
            } 
        }
        return out;
    }

    ArrayList<Long> journeyToLong(ArrayList<Journey> journeys){
        ArrayList<Long> out = new ArrayList<Long>();
        for(Journey journey : journeys){
            out.add(journey.getFirstPointTime().getTimeInMillis());
        }
        return out;
    }
    public void addHabits(ArrayList<Habit> habits_day, Journey journey)
    {
        Habit h = new Habit(journey);
        for(int j =0; j< habits_day.size();j++)
        {
            if(habits_day.get(j).isSame(h))
            {
                habits_day.get(j).addOccurence(journey.getFirstPointTime());
                habits_day.get(j).updateReliability();
                return;
            }
        }
        habits_day.add(h);
    }
    public void updateReliability()
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        int dow = now.get(Calendar.DAY_OF_WEEK)-1;
        dow = dow == 0 ? 6 : dow-1;
        for(int i =0 ; i< habits.get(dow).size(); i++)
        {
            habits.get(dow).get(i).updateReliability();
        }
    }

    public HashMap<JourneyPath,ArrayList<Journey>> sortJourneyByPath(){
        HashMap<JourneyPath,ArrayList<Journey>> out = new HashMap<>();
        for(Journey journey : unused_journeys){
            JourneyPath key = new JourneyPath(journey.getPath());
            if(out.containsKey(key)){
                out.get(key).add(journey);  
            }
            else{
                ArrayList<Journey> array = new ArrayList<>();
                array.add(journey);
                out.put(key,array);
            }            
        }
        return  out;
    }

    public void printHabits()
    {
        for(int i =0; i<7; i++)
        {
            String out = String.format("Habits of %s :", Constants.DAY_LIST.get(i));
            System.out.println(out);
            for(int j =0; j<habits.get(i).size();j++)
            {
                habits.get(i).get(j).print();
                System.out.println("---");
            }
            if (habits.get(i).size() == 0)
                System.out.println("No habits for this day yet!");
            System.out.println("-----------------");
        }
    }

}
