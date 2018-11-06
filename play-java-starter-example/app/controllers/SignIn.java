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
import services.MongoInterface;
 

@Singleton
public class SignIn extends Controller {
	
	 private final MongoDatabase database ;

	@Inject
	public SignIn  (MongoInterface db){
		this.database = db.get_database();	
	}

	public Result sign_in(String a_user, String a_password){
		MongoCollection<Document> users = database.getCollection("users");
		Document registred_user = users.find(and(eq("user", a_user),eq("password", a_password))).first();
		if (registred_user == null){
			//user not suscribe yet.
			return badRequest("Incorrect pseudo and/or password");	
		}
		return(ok("sign in "));			
	}
}
