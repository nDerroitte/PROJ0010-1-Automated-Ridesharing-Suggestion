import java.util.ArrayList;



public class User
{
    public ArrayList<Habits> user_habits;
    private ArrayList<Journey> unused_journeys;

    public User()
    {
        this.user_habits = new ArrayList<>();
        this.unused_journeys = new ArrayList<>();
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
        boolean to_add = true;
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
            // Code Cédric



        //Check nouveau + habits
            //ligne modulo
    }
}
