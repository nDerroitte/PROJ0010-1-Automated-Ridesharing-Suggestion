package services;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;


public class CreationHabitSM
{
    public static ArrayList<Habit> createHabitSM(ArrayList<Journey> journeys, String user)
    {
        System.out.println("Habits being created");
        ArrayList<ArrayList<SimpleHabit>> habits = new ArrayList<>(7);
        for(int i=0; i<7;i++)
            habits.add(new ArrayList<>());

        for(int i =0; i< journeys.size();i++)
        {
            Calendar date_journey = journeys.get(i).getFirstPointTime();
            int dow = date_journey.get(Calendar.DAY_OF_WEEK)-1;
            CreationHabitSM.addHabitsSM(habits.get(dow), journeys.get(i));
        }
        ArrayList<Habit> out = new ArrayList<>();
        for(int i=0; i<7;i++)
        {
            for(int j =0; j<habits.get(i).size();j++)
            {
                if(habits.get(i).get(j).getOccurences().size() > 1 && habits.get(i).get(j).getPath().size()> 3)
                    out.add(habits.get(i).get(j));
            }
        }
        System.out.printf("Habits of %s created\n", user);
        //System.out.println(out);
        try
        {
            FileWriter fw = new FileWriter("app/services/SimpleModel/results.txt", true);
            PrintWriter writer = new PrintWriter(fw);
            writer.printf("User %s.\n\n", user);
            writer.println(out);
            writer.printf("========================================================================================\n");
            writer.close();
        }
        catch(IOException e)
        {
            System.err.println("Error writing in file.");
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
