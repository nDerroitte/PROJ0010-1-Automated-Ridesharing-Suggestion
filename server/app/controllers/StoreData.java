package controllers;

import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;

import services.Point;
import java.io.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.*;

import com.fasterxml.jackson.databind.*;
import javax.json.stream.JsonParsingException;

import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import java.util.Optional;
import java.util.Calendar;
import services.*;
import services.EncryptionException;
import services.Decrypt;
import services.Encrypt;
import services.AES;

@Singleton
public class StoreData extends Controller {
	
	/**
	 * Entry point to the database
	 */
	 private final MongoDatabase database ;

	 /**
	  * entry point for requesting the computation of a user habits.
	  */
	 private final HabitGenerator hb;

	@Inject
	public StoreData  (MongoInterface db, HabitGenerator habit_generator){
		this.database = db.get_database();	
		this.hb = habit_generator;
	}

	/**
	 * Order to the server to push data into database 
	 * 
	 * @return Http response, always ok
	 * @throws Exception
	 * @throws EncryptionException
	 */
	@BodyParser.Of(BodyParser.TolerantText.class)
	public Result store_data() throws Exception, EncryptionException{
		String out = "";
		int nb_journey = 0;
		String[] data = request().body().asText().split("data_splitter");
		JsonReader reader;
		JsonObject dataUnit;
		Http.Cookie cookie = request().cookies().get("user");
		if (cookie == null) {
			return badRequest("Cookie required");
		}
		String cookieValue = cookie.value();
		Document user = null;
		for (String jSonString : data) {
			reader = Json.createReader(new StringReader(jSonString));
			try {
				dataUnit = reader.readObject();
			} catch (JsonParsingException e) {
				out += "json badly formatted";
				continue;
			}
			reader.close();
			MongoCollection<Document> users = database.getCollection("users");
			user = users.find(eq("user", Encrypt.encrypt(dataUnit.getString("UserId")))).first();	
			if (user == null || !user.get("key").equals(cookieValue)) {
				out += "invalid cookie/user";
				continue;
			}
		}
		if(user != null){
			String decrypted_user = Decrypt.decrypt((ArrayList<Byte>)user.get("user"));
			hb.store_data(decrypted_user,request().body().asText());
		}		
		return ok(out + " " + nb_journey + "data length: " + data.length);
	}
}
