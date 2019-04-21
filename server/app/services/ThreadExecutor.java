package services;

import com.mongodb.MongoClientURI;
import com.mongodb.MongoClient;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.ServerAddress;
import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.*;

import javax.inject.*;
import com.google.common.util.concurrent.*;
/**
 * Implement the thread pool for computing the habits of an user 
 * 
 * The thread pool is an unbouded single thread.
 * @see submitTask(String,int) for requesting the computation of the habit of an user.
 * @author Cedric
 */
@Singleton
public class ThreadExecutor implements HabitGenerator {
    /**
     * Entry point to the database
     */
    private final MongoDatabase database;
    /**
     * task executor
     */
    private final ExecutorService worker;
    /**
     * Entry point to the the collection users of the database.
     */
    private final MongoCollection<Document> users;

    /**
     * @param db: entry point to the database.
     */
    @Inject
    public ThreadExecutor(MongoInterface db) {
        this.worker = Executors.newSingleThreadExecutor();
        this.database = db.get_database();
        this.users = database.getCollection("users");
    }

    /**
     * 
     * @param userID : id of the user for who we want to compute its habits.
     * @param method : an integer saying which method to use. 
     * <ul>
     * <li> method = 0: use of the general model with a period which is a multiple of 1 day
     * <li> method = 1: use of the general model with a period which is a multiple of 7 day
     * <li> method = 2: use of general model with period multiple of 1 and sort the journey by day
     * <li> method = 3: use of the simple model
     * </ul>
     * @see services.UserGM for general model
     * @see services.UserSimpleModel for the simple model
     *                  
     */
    @Override
    public void submitTask(String userID, int method) {
        try {
            this.worker.submit(new ComputationUnit(userID, method, users));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
/**
 * Worker of ThreadExecutor
 */
class ComputationUnit implements Runnable {
    private final String user_id;
    private final int method;
    private final MongoCollection<Document> database;

    /**
     * 
     * @param userID A user 
     * @param method method use for computing habit of UserID
     * @param database entry point to the database
     */
    ComputationUnit(String userID, int method, MongoCollection<Document> database) {
        this.user_id = userID;
        this.method = method;
        this.database = database;
    }

    public void run() {
        try {
            switch (this.method) {
            case 0:

                UserGM usergm = new UserGM(user_id, database, 0);
                usergm.createHabits();
                break;

            case 1:
                usergm = new UserGM(user_id, database, 1);
                usergm.createHabits();
                break;

            case 2:
                usergm = new UserGM(user_id, database, 2);
                usergm.createHabits();
                break;

            case 3:
                UserSimpleModel user = new UserSimpleModel(user_id, database);
                user.createHabits();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
