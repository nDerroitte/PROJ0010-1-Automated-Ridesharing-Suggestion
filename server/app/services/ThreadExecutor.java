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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.*;

import javax.inject.*;
import com.google.common.util.concurrent.*;

@Singleton
public class ThreadExecutor implements HabitGenerator{
	private final MongoDatabase database ;
    private final ExecutorService worker;
    private final MongoCollection<Document> users;
    private final MongoCollection<Document> habits;


    @Inject
	public ThreadExecutor(MongoInterface db){
        this.worker = Executors.newSingleThreadExecutor();
        this.database = db.get_database();
        this.users = database.getCollection("users");
        this.habits = database.getCollection("habits");
	}

   @Override 
    public void submitTask(String userID, ArrayList<Document> journeys,int method) 
    {
        try{
           this.worker.submit(new ComputationUnit(userID, journeys, users,method));
        } catch(Exception e){
            e.printStackTrace();
        }

    }

}
class ComputationUnit implements Runnable{
    private final String user_id;
    private final ArrayList<Journey> journeys;
    private final MongoCollection<Document> users;
    private final int method;

    ComputationUnit(String userID, ArrayList<Document> journeys, MongoCollection<Document> users, int method)
    {  
        this.user_id = userID;
        this.journeys = new ArrayList<>();
        this.method = method;
        try{
            for (Document i :journeys)
                this.journeys.add(Journey.fromDoc(i));
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        this.users = users;
    }
    public void run() 
    {   
        switch(this.method){
            case 0:
            
            UserGM usergm = new UserGM(user_id,journeys,0);
            usergm.createHabits();
            break;

            case 1:
            usergm = new UserGM(user_id,journeys,1);
            usergm.createHabits();
            break;

            case 2:
            usergm = new UserGM(user_id,journeys,2);
            usergm.createHabits();  
            break;          

            case 3:
            UserSimpleModel user = new UserSimpleModel(user_id,journeys);
            user.createHabits();
            break;
        }  
    }
}
