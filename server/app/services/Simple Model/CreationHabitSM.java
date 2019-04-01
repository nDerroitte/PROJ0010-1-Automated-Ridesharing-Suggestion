import java.util.ArrayList;
import java.util.Calendar;
package services;


public class CreationHabitSM
{
    public static ArrayList<Habit> createHabitSM(ArrayList<Journey> journeys)
    {
        ArrayList<ArrayList<SimpleHabit>> habits = new ArrayList<>(7);
        for(int i=0; i<7;i++)
            habits.add(new ArrayList<>());

        for(int i =0; i< journeys.size();i++)
        {
            Calendar date_journey = journeys.get(i).getFirstPointTime();
            int dow = date_journey.get(Calendar.DAY_OF_WEEK)-1;
            CreationHabit.addHabitsSM(habits.get(dow), journeys.get(i));
        }
        ArrayList<Habit> out = new ArrayList<>();
        for(int i=0; i<7;i++)
        {
            for(int j =0; j<habits.get(i).size();j++)
            {
                out.add(habits.get(i).get(j));
            }
        }
        return out;
    }

    public static void addHabitsSM(ArrayList<SimpleHabit> habits_day, Journey journey)
    {
        SimpleHabit h = new SimpleHabit(journey);
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
}
