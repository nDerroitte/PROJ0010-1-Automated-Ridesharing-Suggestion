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
import services.MongoInterface;


@Singleton
public class StoreData extends Controller {
	
	 private final MongoDatabase database ;

	@Inject
	public SignIn  (MongoInterface db){
		this.database = db.get_database();	
	}

	public Result store_data(String a_user, String a_password){
		JsonNode body = request().body().asJson();
    		if(json == null) {
        		return badRequest("Expecting Json data");
    		}
		//get user ID from JSON
		JsonNode user_info = body.get("UserInfo");
		String user_id = user_info.get("UserID").asText();
		//get key and last sign in to chack authentification
		MongoCollection<Document> users = database.getCollection("users");
		FindIterable<field> findIterable users.find(eq("user", user_id)).projection(include("last_sign_in", "key")).first();
		
	}
}















