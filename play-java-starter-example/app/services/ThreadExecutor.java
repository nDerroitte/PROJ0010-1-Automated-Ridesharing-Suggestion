package services;

import com.mongodb.MongoClientURI ;
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
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.*;

import javax.inject.*;
import com.google.common.util.concurrent.*;

@Singleton
public class ThreadExecutor implements HabitGenerator{
	private final MongoDatabase database ;
    private final ThreadFactoryBuilder factoryBuilder;
    private final ThreadFactory factory;
    private final ExecutorService worker;
    private final MongoCollection<Document> users;
    private final MongoCollection<Document> habits;


    @Inject
	public ThreadExecutor  (MongoInterface db){
        this.factoryBuilder = new ThreadFactoryBuilder();
        this.factoryBuilder.setPriority(1);
        this.factory = factoryBuilder.build();
        this.worker = Executors.newSingleThreadExecutor(factory);
        this.database = db.get_database();
        this.users = database.getCollection("users");
        this.habits = database.getCollection("habits");
	}

    @Override 
	public void submitTask(String userID,ArrayList<Journey> journeys) {
       	this.worker.submit(new ComputationUnit(userID, journeys));
    }

}
class ComputationUnit implements Runnable {
    private final String user_id;
    private final ArrayList<Journey> journeys;
    ComputationUnit(String userID, ArrayList<Journey> journeys)
    {  
        this.user_id = userID;
        this.journeys = journeys;
    }
    public void run() 
    {        
        User user = new User(this.user_id , journeys);
        user.createHabits();
    }
}