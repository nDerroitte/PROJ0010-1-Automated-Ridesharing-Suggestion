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
import services.Habit;
import java.text.ParseException;
import services.EncryptionException;
import services.Decrypt;
import services.Encrypt;
import services.AES;

@Singleton
public class GetHabit extends Controller {
	
	private final MongoDatabase database;
    private final HabitGenerator hb;

	@Inject
	public GetHabit (MongoInterface db, HabitGenerator habit_generator){
        this.database = db.get_database();
		this.hb = habit_generator;
	}

	public Result compute_habit(String a_user) throws EncryptionException{
		ArrayList<Byte> a_user_E = Encrypt.encrypt(a_user);
		MongoCollection<Document> users = database.getCollection("users");
		if(a_user.equals("all")){
			System.out.println("Computing habit of all user");
			MongoCursor<Document> cursor = users.find().iterator();
			//try {
				while (cursor.hasNext()) {
					Document user = cursor.next();
					//Decrypt le string user.get(user)	
					String decrypted_user = Decrypt.decrypt((ArrayList<Byte>)user.get("user"));
					hb.submitTask(decrypted_user);
					//utiliser le crypter 
					System.out.println("User: " + decrypted_user + " is submit");
				}
			//} 
			//catch(Exception e){
			//	e.printStackTrace();
			//}
			//finally {
				cursor.close();
			//}
			return ok("computing...");
		}
		else{
			//encrypter le a_usre 
			Document user = users.find(and(eq("user", a_user_E))).first();
			if(user != null) {
				hb.submitTask(a_user);
				return ok("computing...");
			}
			return ok("user does not exist");
		}		

	}

	public Result get_habit(String a_user,String a_password) throws ParseException, EncryptionException{
		//encrypt the user and password (reusse the code)
		ArrayList<Byte> a_user_E = Encrypt.encrypt(a_user);
		ArrayList<Byte> a_password_E = Encrypt.encrypt(a_password);
		Document user = database.getCollection("users").find(and(eq("user", a_user_E), eq("password", a_password_E))).first();
		ArrayList<Document> habits = (ArrayList<Document>)(user.get("habits"));
		String out = "";
		if (user == null){
			return ok("user doesn't exist");		
		}
		for(Document habit: habits){
			Document habit_D = (Habit.fromDoc(habit)).toDocNotEncrypted();
			out += habit_D.toJson();
			out += "data_splitter";
		}
		return ok(out.toString());
	}

	public Result update_habit(String a_user,String password){
		/*
		String[] data = request().body().asText().split("data_splitter");
		JsonReader reader;
		JsonObject dataUnit;
		MongoCollection<Document> users = database.getCollection("users");
		for (String jSonString : data) {
			reader = Json.createReader(new StringReader(jSonString));
			dataUnit = reader.readObject();
			reader.close();
			Document user = users.find(and(eq("user", a_user), eq("password", a_password))).first();
			if(user==null){
				return ok("user not found or incorect password");
			}
			users.updateOne(and(eq("user", a_user)));

		}*/
		return ok("");


	}

}










