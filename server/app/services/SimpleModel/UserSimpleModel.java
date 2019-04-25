package services;
import java.util.ArrayList;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import java.text.ParseException;
import services.EncryptionException;
import services.Decrypt;
import services.Encrypt;
import services.AES;
/**
 * User class for the Simple Model algorithm
 */
public class UserSimpleModel
{
    /**
     * The list of unsed journeys where we want to generate habit from
     */
    private ArrayList<Journey> unused_journeys;
    /**
     * User id
     */
    private String user_id;
    /**
     * List of habits of the user
     */
    private ArrayList<Habit> habits;

     /**
     * entry point to the database
     */
    private MongoCollection<Document> db;

    /**
     * Constructor of the USM
     * @param user_id string of the user id
     * @param database MongoDB database
     * @throws ParseException if the user doesnt exist u-in the database
     */
    public UserSimpleModel(String user_id, MongoCollection<Document> database) throws ParseException, EncryptionException
    {
        this.user_id = user_id;
        this.habits = new ArrayList<>();
        this.unused_journeys = new ArrayList<>();
        this.db = database;
        
        // Get user journey from database
        //Encrypt user id 
        ArrayList<Byte> user_id_E = Encrypt.encrypt(user_id);
        Document user = database.find(eq("user", user_id_E)).first();
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

    /**
     * Create habits for the user
     */
    public ArrayList<Journey> createHabits() throws EncryptionException
    {
        this.habits = CreationHabitSM.createHabitSM(unused_journeys, user_id);

        habitToDB(this.habits);
        return unused_journeys = CreationHabitSM.unused_journeys;
    }

    public void habitToDB(ArrayList<Habit> new_habits) throws EncryptionException{
         //Encrypt user id 
        ArrayList<Byte> user_id_E = Encrypt.encrypt(user_id);
        Document user = db.find(eq("user", user_id_E)).first();
        ArrayList<Document> habits = (ArrayList<Document>)(user.get("habits"));
        if(habits == null){
            habits = new ArrayList<Document>();
        }
        for(Habit h : new_habits){
            habits.add(h.toDoc());
        }
         //Encrypt user id 
        db.updateOne(eq("user",user_id_E),set("habits", habits));
    }
}
