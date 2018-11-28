package controllers;

import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;
import java.io.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import com.fasterxml.jackson.databind.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import javax.json.*;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import java.util.Optional;
import services.MongoInterface;
import services.HabitGenerator;


@Singleton
public class StoreData extends Controller {
	
	 private final MongoDatabase database ;
	 private final HabitGenerator hb;

	@Inject
	public StoreData  (MongoInterface db, HabitGenerator habit_generator){
		this.database = db.get_database();	
		this.hb = habit_generator;
	}

	//En cours de devellopement.
<<<<<<< Updated upstream
<<<<<<< Updated upstream
	public Result store_data(){
		System.err.println("request: " + request().body().asText());
		String[] data = request().body().asText().split("data_splitter");
		JsonReader reader;
		JsonObject dataUnit;
		Optional cookie = request().header("cookie");
		if (!cookie.isPresent()) {
			return badRequest();
		}
		String cookieValue = cookie.get().toString().split(";")[0];
		if (!cookieValue.split("=")[0].equals("user") || cookieValue.split("=").length != 2) {
			return badRequest();
		}

		for (String jSonString : data) {
			reader = Json.createReader(new StringReader(jSonString));
			dataUnit = reader.readObject();
			reader.close();
			MongoCollection<Document> users = database.getCollection("users");
			Document user = users.find(eq("user", dataUnit.getString("UserId"))).first();
			if (user == null || !user.get("key").equals(cookieValue.split("=")[1])) {
				continue;
			}
			for (JsonValue point : dataUnit.getJsonArray("Points")) {
				//TODO add points in DB, need to check with Natan
				JsonObject _point = (JsonObject)(point);
				System.err.println(_point.getString("date"));
			}
		}
=======
=======
>>>>>>> Stashed changes
	public Result store_data(String json){
		//JsonNode body = request().body().asJson();
    		//if(json == null) {
        	//	return badRequest("Expecting Json data");
    		//}
		////get user ID from JSON
		//JsonNode user_info = body.get("UserInfo");
		//String user_id = user_info.get("UserID").asText();
		////get key and last sign in to chack authentification
		//MongoCollection<Document> users = database.getCollection("users");
		//FindIterable<field> findIterable users.find(eq("user", user_id)).projection(include("last_sign_in", "key")).first();
		hb.submit_task(a_user);
>>>>>>> Stashed changes
		return ok();
	}
}















