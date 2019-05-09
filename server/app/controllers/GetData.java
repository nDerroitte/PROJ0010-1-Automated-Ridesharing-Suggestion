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
import services.EncryptionException;
import services.Decrypt;
import services.Encrypt;
import services.AES;
import services.*;
import java.text.ParseException;

@Singleton
public class GetData extends Controller {
	
	private final MongoDatabase database;

	@Inject
	public GetData (MongoInterface db){
		this.database = db.get_database();
	}

	public Result get_data(String a_user, String a_password) throws EncryptionException, ParseException{
		MongoCollection<Document> users = database.getCollection("users");
		//encrypt a_user et a_passward 
		ArrayList<Byte> a_user_E = Encrypt.encrypt(a_user);
		ArrayList<Byte> a_password_E = Encrypt.encrypt(a_password);
		Document user = users.find(and(eq("user", a_user_E), eq("password", a_password_E))).first();
		StringBuffer data = new StringBuffer();
		Journey journey_object;
		if(user != null) {
			ArrayList<Document> journeys = (ArrayList<Document>)(user.get("journeys"));
			for (Document journey : journeys) {
				//TO DO changer ceci avec un getFromDoc
				journey_object = Journey.fromDoc(journey);
				ArrayList<Coordinate> meeting_poin = journey_object.getPath();
				for (Coordinate c : meeting_poin){
					System.out.println(c);
				}
				ArrayList<Document> doc_meeting_point = (ArrayList<Document>)journey.get("meeting_point");
				for (Document doc_point : doc_meeting_point) {
					data.append("datetime: ");
					data.append((String)doc_point.get("time") + "\n");
					ArrayList<Number> coordinates = (ArrayList<Number>)doc_point.get("position");
					data.append("latitude: ");
					data.append(coordinates.get(0).toString() + "\n");
					data.append("longitude: ");
					data.append(coordinates.get(1).toString() + "\n\n");
				}
			}
			return ok(data.toString());
		}
		//Encrypt the a_user so use the a_user encrypted 
		if (users.find(eq("user",a_user_E)).first() == null){
			return ok("user doesn't exist");		
		}

		return ok("incorrect pasword");
	}
}










