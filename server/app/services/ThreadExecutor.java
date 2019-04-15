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

@Singleton
public class ThreadExecutor implements HabitGenerator {
    private final MongoDatabase database;
    private final ExecutorService worker;
    private final MongoCollection<Document> users;

    @Inject
    public ThreadExecutor(MongoInterface db) {
        this.worker = Executors.newSingleThreadExecutor();
        this.database = db.get_database();
        this.users = database.getCollection("users");
    }

    @Override
    public void submitTask(String userID, int method) {
        try {
            this.worker.submit(new ComputationUnit(userID, method, users));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

class ComputationUnit implements Runnable {
    private final String user_id;
    private final int method;
    private final MongoCollection<Document> database;

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
