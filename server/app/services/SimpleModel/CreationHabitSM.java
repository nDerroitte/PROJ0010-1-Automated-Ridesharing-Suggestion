package services;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Class that make the link between the SimpleHabit class and the UserSimpleModel.
 * Allow the UserSM to create SimpleHabit with these static methods/
 */
public class CreationHabitSM
{
    public static ArrayList<Journey> unused_journeys = new ArrayList<>();
    /**
     * Static method that allow to create SimpleHabits for a user from a list of journey
     * @param journeys The Arraylist of jounreys
     * @param user The user id
     * @return The arrayList of SImpleHabit
     */
    public static ArrayList<Habit> createHabitSM(ArrayList<Journey> journeys, String user)
    {
        System.out.println("Habits being created");
        // Creating habits per day
        // Initialisation
        ArrayList<ArrayList<SimpleHabit>> habits = new ArrayList<>(7);
        for(int i=0; i<7;i++)
            habits.add(new ArrayList<>());

        // Sorting the journeys by day and creating the habits for each day
        for(int i =0; i< journeys.size();i++)
        {
            Calendar date_journey = journeys.get(i).getFirstPointTime();
            int dow = date_journey.get(Calendar.DAY_OF_WEEK)-1;
            // Adding the habits in the habit Array list
            CreationHabitSM.addHabitsSM(habits.get(dow), journeys.get(i));
        }
        // Post processing
        ArrayList<Habit> out = new ArrayList<>();
        for(int i=0; i<7;i++)
        {
            for(int j =0; j<habits.get(i).size();j++)
            {
                // For each day happen habits if they are large enough and they happen several time
                if(habits.get(i).get(j).getOccurences().size() > 1)
                {
                    if(habits.get(i).get(j).getPath().size()> 1)
                        out.add(habits.get(i).get(j));
                }
                else 
                {
                    CreationHabitSM.unused_journeys.addAll(habits.get(i).get(j).getJourneys());
                }
            }
        }
        System.out.printf("Habits of %s created\n", user);
        // Writing in files
        try
        {
            String folderString = "user_habit/"+user;
            File newFile = new File(folderString);
            if(!newFile.exists())
                newFile.mkdir();
            File method3 = new File(folderString+"/3");
            method3.mkdir();
            FileWriter fw = new FileWriter(folderString+"/3/habits.txt", false);
            PrintWriter writer = new PrintWriter(fw);
            writer.printf("User %s.\n\n", user);
            for(Habit h : out)
            {
                if(h.reliability < 55.0)
                {
                    SimpleHabit habitSM = (SimpleHabit) h;
                    CreationHabitSM.unused_journeys.addAll(habitSM.getJourneys());
                    continue;
                }
                writer.println(h);
                writer.printf("========================================================================================\n");
            }
            writer.close();
        }
        catch(IOException e)
        {
            System.err.println("Error writing in file.");
        }
        
        return out;
    }

    /**
     * Create an habit from the jounrey and add it the the list of SimpleHabit
     * @param habits_day the list of simple habit to fill
     * @param journey the jounrey to consider
     */
    public static void addHabitsSM(ArrayList<SimpleHabit> habits_day, Journey journey)
    {
        SimpleHabit h = new SimpleHabit(journey);
        for(int j =0; j< habits_day.size();j++)
        {
            // If an similar habit already exist, merge them
            if(habits_day.get(j).isSame(h))
            {
                habits_day.get(j).addOccurence(journey);
                habits_day.get(j).updateReliability();
                return;
            }
        }
        habits_day.add(h);
    }
}
