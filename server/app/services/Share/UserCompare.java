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

public class UserCompare {
    /**
     * The list of habit of the first user
     */
    private ArrayList<Habit> habits1;

    /**
     * The list of habit of the second user
     */
    private ArrayList<Habit> habits2;

    /**
     * entry point to the database
     */
    private MongoCollection<Document> db;

    /**
     * 
     * @param user1    A user
     * @param user2    A user
     * @param database Entry point to the database
     * @throws ParseException      In case habit cannot be read from database.
     * @throws EncryptionException In case of error during encryption
     */
    public UserCompare(String user1, String user2, MongoCollection<Document> database)
            throws ParseException, EncryptionException {
        this.db = database;
        this.habits1 = new ArrayList<>();
        this.habits2 = new ArrayList<>();

        ArrayList<Byte> user_id_E1 = Encrypt.encrypt(user1);
        Document user_1 = db.find(eq("user", user_id_E1)).first();
        if (user_1 == null) {
            System.err.println("User: " + user1 + " not in DB");
        } else {
            ArrayList<Document> habits = (ArrayList<Document>) (user_1.get("habits"));
            for (Document habit : habits) {
                habits1.add(Habit.fromDoc(habit));
            }
        }

        ArrayList<Byte> user_id_E2 = Encrypt.encrypt(user2);
        Document user_2 = db.find(eq("user", user_id_E2)).first();
        if (user_2 == null) {
            System.err.println("User: " + user2 + " not in DB");
        } else {
            ArrayList<Document> habits = (ArrayList<Document>) (user_2.get("habits"));
            for (Document habit : habits) {
                habits2.add(Habit.fromDoc(habit));
            }
        }
    }

    /**
     * Find similar habit between user1 and user2
     * 
     * @return A list of habit of user1 that match habit of user2 
     */
    public ArrayList<Habit> SimilarHabits() {
        ArrayList<Habit> similar = new ArrayList<>();
        for (Habit h1 : habits1) {
            for (Habit h2 : habits2) {
                if (h1.match(h2))
                    similar.add(h1);
            }
        }
        return similar;
    }
}