package services;
import java.util.ArrayList;
import java.text.ParseException;

import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class UserCompare
{
    /**
     * The list of unsed journeys where we want to generate habit from
     */
    private ArrayList<Habit> habits1;

    /**
     * The list of unsed journeys where we want to generate habit from
     */
    private ArrayList<Habit> habits2;


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
    public void UserSimpleModel(String user1, String user2, MongoCollection<Document> database) throws ParseException, EncryptionException
    {
        this.db = database;
        this.habits1 = new ArrayList<>();
        this.habits2 = new ArrayList<>();

        ArrayList<Byte> user_id_E1 = Encrypt.encrypt(user1);
        Document user_1 = db.find(eq("user", user_id_E1)).first();
        if (user_1 == null ) {
            System.err.println("User: " + user1 + " not in DB");
        }
        else{
            ArrayList<Document> habits = (ArrayList<Document>)(user_1.get("habits"));
            for(Document habit : habits){
                habits1.add(Habit.fromDoc(habit));
            }
        }

        ArrayList<Byte> user_id_E2 = Encrypt.encrypt(user2);
        Document user_2 = db.find(eq("user", user_id_E2)).first();
        if (user_2 == null ) {
            System.err.println("User: " + user2 + " not in DB");
        }
        else{
            ArrayList<Document> habits = (ArrayList<Document>)(user_2.get("habits"));
            for(Document habit : habits){
                habits2.add(Habit.fromDoc(habit));
            }
        }
    }

    public ArrayList<Habit> SimilarHabits()
    {
        ArrayList<Habit> similar = new ArrayList<>();
        for(Habit h1 : habits1)
        {
            for(Habit h2 : habits2)
            {
                if(h1.match(h2))
                    similar.add(h1);
            }
        }
        return similar;
    }
}