import java.util.ArrayList;
import java.util.Calendar;


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
        for(int i =0; i< unused_journeys.size();i++)
        {
            Calendar date_journey = unused_journeys.get(i).getFirstPointTime();
            int dow = date_journey.get(Calendar.DAY_OF_WEEK)-1;
            addHabits(this.habits.get(dow), unused_journeys.get(i));
        }
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
