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
 
public class RemoveUser extends Controller {
	
	 private final MongoDatabase database ;

	@Inject
	public RemoveUser  (MongoInterface db){
		this.database = db.get_database();	
	}
	
	public Result remove_user(String a_user,String a_password){
		MongoCollection<Document> users = database.getCollection("users");
		DeleteResult delete_result = users.deleteMany(and(eq("user",a_user),eq("password",a_password)));
		if (delete_result.getDeletedCount() == 0){
			return badRequest("user: " + a_user + " not registred or incorrect password");
		}
		return ok("user data succesfully removed");	
	}




}
