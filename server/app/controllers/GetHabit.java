package controllers;

import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;
import java.io.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
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
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Future;

import services.HabitGenerator;
import services.MongoInterface;

 

@Singleton
public class GetHabit extends Controller {
	
	private final MongoDatabase database;
    private final HabitGenerator hb;

	@Inject
	public GetHabit (MongoInterface db, HabitGenerator habit_generator){
        this.database = db.get_database();
		this.hb = habit_generator;
	}

	public Result get_habit(String a_user,String method) {
		MongoCollection<Document> users = database.getCollection("users");
		if(a_user.equals("all")){
			System.out.println("Computing habit of all user");
			MongoCursor<Document> cursor = users.find().iterator();
			try {
				while (cursor.hasNext()) {
					Document user = cursor.next();						
					hb.submitTask((String) user.get("user"),(ArrayList<Document>) user.get("journeys"),Integer.parseInt(method));
					System.out.println("User: " + user.get("user") + " is submit");
				}
			} 
			catch(Exception e){
				e.printStackTrace();
			}
			finally {
				cursor.close();
			}
			return ok("computing...");
		}		
		Document user = users.find(and(eq("user", a_user))).first();
		if(user != null) {
			ArrayList<Document> journeys = (ArrayList<Document>)(user.get("journeys"));
            hb.submitTask(a_user, journeys,Integer.parseInt(method));
            return ok("computing...");
		}

		if (users.find(eq("user",a_user)).first() == null){
			return ok("user: " + a_user + " doesn't exist");		
		}
		return ok("incorrect pasword");
	}


}










