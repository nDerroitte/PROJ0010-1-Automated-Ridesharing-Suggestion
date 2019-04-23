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
public class SignIn extends Controller {
	
	private final MongoDatabase database;

	@Inject
	public SignIn  (MongoInterface db){
		this.database = db.get_database();
	}

	public Result sign_in(String a_user, String a_password) {
		MongoCollection<Document> users = database.getCollection("users");
		String key = UUID.randomUUID().toString();
		UpdateResult updateresult = users.updateOne(and(eq("user", a_user),eq("password", a_password)),set("key",key));
		if(updateresult.getModifiedCount() == 1) {
			response().setCookie(Cookie.builder("user",key).build());
			return ok("success");
		}
		if (users.find(eq("user",a_user)).first() == null){
			return ok("user doesn't exist");		
		}

		return ok("incorrect pasword");

	}
}










