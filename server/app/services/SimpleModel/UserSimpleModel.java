package services;
import java.util.ArrayList;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import java.text.ParseException;

public class UserSimpleModel
{
    private ArrayList<Journey> unused_journeys;
    private String user_id;
    private ArrayList<Habit> habits;

    public UserSimpleModel(String user_id, MongoCollection<Document> database) throws ParseException
    {
        this.user_id = user_id;
        this.habits = new ArrayList<>();
        this.unused_journeys = new ArrayList<>();
        
        //get user journey from database
        Document user = database.find(eq("user", user_id)).first();
        if (user == null ) {
            System.err.println("User: " + user_id + " not in DB");
        }
        else{
            ArrayList<Document> journeys = (ArrayList<Document>)(user.get("journeys"));
            for(Document journey : journeys){
                unused_journeys.add(Journey.fromDoc(journey));
            }
        }
    }

    public void createHabits()
    {
        this.habits = CreationHabitSM.createHabitSM(unused_journeys, user_id);
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
