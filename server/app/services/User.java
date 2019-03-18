import java.util.ArrayList;


public class User
{
    private ArrayList<Journey> unused_journeys;
    private String user_id;
    private ArrayList<Habit> habits;

    public User(String user_id, ArrayList<Journey> journeys)
    {
        this.unused_journeys = journeys;
        this.user_id = user_id;
        this.habits = new ArrayList<>();
    }

    public void createHabits()
    {
        this.habits = CreationHabit.createHabitSM(unused_journeys);
    }


    public void printHabits()
    {
        for(int i =0; i<7; i++)
        {
            String out = String.format("Habits of %s :", Constants.DAY_LIST.get(i));
            System.out.println(out);
            for(int j =0; j<habits.size();j++)
            {
                habits.get(j).print();
                System.out.println("---");
            }
            System.out.println("-----------------");
        }
    }
}
