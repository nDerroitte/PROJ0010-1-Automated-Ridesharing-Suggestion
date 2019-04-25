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
import java.util.Date;
import java.util.UUID;
import java.util.List;

import services.MongoInterface;
import services.Journey;
 
import services.EncryptionException;
import services.Decrypt;
import services.Encrypt;
import services.AES;

public class SignUp extends Controller {
	
	 private final MongoDatabase database ;

	@Inject
	public SignUp  (MongoInterface db){
		this.database = db.get_database();	
	}
	
	public Result sign_up(String a_user, String a_password,String email) throws EncryptionException{
		MongoCollection<Document> users = database.getCollection("users");
		 //Encrypt a user 
		ArrayList<Byte> a_user_E = Encrypt.encrypt(a_user);
		ArrayList<Byte> a_password_E = Encrypt.encrypt(a_password);
		ArrayList<Byte> email_E = Encrypt.encrypt(email);
		Document registred_user = users.find(eq("user", a_user_E)).first();
		if (registred_user == null){
			// write new user in database
			Document new_user = new Document("user", a_user_E).append("password",a_password_E).append("email", email_E).append("journeys",new ArrayList<Document>());
			users.insertOne(new_user);
			return ok("user successfully recorded");
		}
		return ok("pseudo already used");	
	}	
}
