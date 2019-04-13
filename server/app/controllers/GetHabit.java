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
		Document user = users.find(and(eq("user", a_user))).first();
		if(user != null) {
			ArrayList<Document> journeys = (ArrayList<Document>)(user.get("journeys"));
			switch(method){
				case "simple": 
					hb.submitTask(a_user, journeys,2);
				case "general":
					hb.submitTask(a_user, journeys,0);
				case "general_weekly":
					hb.submitTask(a_user, journeys,1);
			}
            return ok("computing...");
		}
		if (users.find(eq("user",a_user)).first() == null){
			return ok("user doesn't exist");		
		}
		return ok("incorrect pasword");
	}
}










