package services;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
public class UserGM
{
    private ArrayList<Journey> unused_journeys;
    private String user_id;
    private int mode;

    public UserGM(String user_id, ArrayList<Journey> journeys,int mode)
    {
        this.unused_journeys = journeys;
        this.user_id = user_id;
	this.mode = mode;
    }

    public void createHabits()
    {

        //cedric stuff without sorting by day
        HashMap<JourneyPath,ArrayList<Journey>> sorted_journey = sortJourneyByPath();
        Iterator it = sorted_journey.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ArrayList<Journey> data = (ArrayList<Journey>)pair.getValue();
            ComputeHabit computer = new ComputeHabit(journeyToLong(data));
            LinkedList<Habit> habits = computer.getHabit();
            printHabits(habits);
            //cedric stuff with sorting by day.
	    if(mode == 1){
            	HashMap<Integer,ArrayList<Journey>> journey_by_day = sortJourneyByDay(data);
            	Iterator byday = journey_by_day.entrySet().iterator();
            	while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry)it.next();
                    ArrayList<Journey> databyday = (ArrayList<Journey>)entry.getValue();
                    ComputeHabit ch = new ComputeHabit(journeyToLong(databyday));
                    LinkedList<Habit> habitsbyday = computer.getHabit();
                    printHabits(habitsbyday);
                }  
	    }
     
        }
    }
    public void printHabits(LinkedList<Habit> habits){
        for(Habit habit : habits){
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

}
