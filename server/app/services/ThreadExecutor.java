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
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.*;
import java.text.SimpleDateFormat;
import java.io.*;
import javax.json.*;
import javax.inject.*;
import com.google.common.util.concurrent.*;
import services.MongoDB;

/**
 * Implement the thread pool for computing the habits or storing the journey of an user
 * The thread pool is an unbouded single thread.
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
     * 
     */
    @Override
    public void submitTask(String userID) {
        try {
            this.worker.submit(new ComputationUnit(userID, users));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Submit task for storing data
     * 
     * @param userID Owner of the data
     * @param data Data to store
     */
    @Override
    public void store_data(String userID, String data){
        this.worker.submit(new StoreData(this,users,userID,data));
    }

}

/**
 * Worker responsible for computing user habit.
 */
class ComputationUnit implements Runnable {
    private final String user_id;
    private final MongoCollection<Document> database;

    /**
     * 
     * @param userID   A user
     * @param database entry point to the database
     */
    ComputationUnit(String userID, MongoCollection<Document> database) {
        this.user_id = userID;
        this.database = database;
    }

    /**
     * Compute user habit
     */
    public void run() {
        try {
            //first use the simple model
            UserSimpleModel simple_user = new UserSimpleModel(user_id, database);

            //use the general model to find the habit that the simple one cannot find
            ArrayList<Journey> unused_journey = simple_user.createHabits();
            UserGM user_gm = new UserGM(user_id, database, unused_journey);
            user_gm.createHabits();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

/**
 * Worker responsible for toring user data.
 */
class StoreData implements Runnable {
    private final String user_id;
    private final MongoCollection<Document> database;
    private final String[] data;
    HabitGenerator hb;

    /**
     * Store user data into DB and ask for computing its habit.
     * 
     * @param hb        entry point for requesting the computation of a user habits.
     * @param db        entry point to the database
     * @param userID    ID of the user
     * @param raw_data  data to store
     */
    public StoreData(HabitGenerator hb, MongoCollection<Document> db, String userID, String raw_data) {
        user_id = userID;
        this.data = raw_data.split("data_splitter");
        database = db;
        this.hb = hb;
    }

    /**
     * Push data into the database.
     */
    @Override
    public void run() {
        try {
            JsonReader reader;
            JsonObject dataUnit;
            Document user = null;
            int i = 0;
            for (String jSonString : data) {
                System.out.println("Make " + i +" out of " + data.length);
                i++;
                reader = Json.createReader(new StringReader(jSonString));
                dataUnit = reader.readObject();
                reader.close();
                user = database.find(eq("user", MongoDB.aes.encrypt(dataUnit.getString("UserId")))).first();
                ArrayList<Point> point_list = new ArrayList<>();

                for (JsonValue point : dataUnit.getJsonArray("Points")) {
                    JsonObject _point = (JsonObject) (point);
                    Calendar cal;
                    cal = Constants.stringToCalendar(_point.getString("calendar"));

                    double lat = Double.parseDouble(_point.getString("lat"));
                    double lon = Double.parseDouble(_point.getString("long"));
                    Coordinate coord = Constants.CoordinateTransformation(lat, lon);
                    Point current_point = new Point(cal, coord);
                    point_list.add(current_point);
                }
                // A journey with only one point has no meaning : this is a measurement error
                if (point_list.size() <= 1) {
                    continue;
                }
                Journey current_journey = new Journey(point_list);
                ArrayList<Document> journeys = (ArrayList<Document>) (user.get("journeys"));
                journeys.add(current_journey.toDoc());
                database.updateOne(eq("user", user.get("user")), set("journeys", journeys));
            }
            if (user != null) {
                hb.submitTask(user_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}