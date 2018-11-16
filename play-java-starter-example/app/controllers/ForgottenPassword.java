package controllers;

import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
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
public class ForgottenPassword extends Controller {
	@Inject MailerClient mailerClient;
	
	 private final MongoDatabase database ;

	@Inject
	public ForgottenPassword  (MongoInterface db){
		this.database = db.get_database();	
	}

	public Result forgotten_password(String a_user, String a_email){
		MongoCollection<Document> users = database.getCollection("users");
		String key = UUID.randomUUID().toString();
		UpdateResult updateresult = users.updateOne(eq("user", a_user),set("key",key));
		
		if(updateresult.getModifiedCount() == 1) {
			response().setCookie(Cookie.builder("user",key).build());

			if (users.find(and(eq("user", a_user), eq("email", a_email))).first() != null) {
				Email email = new Email()
					.setSubject("Demande de récupération du mot de passe")
					.setFrom("Covoituliège <Proj00102018Covoituliege@gmail.com>")
					.addTo(a_email)
					.setBodyText("Votre mot de passe est " + users.find(eq("user", a_user)).first().get("password") + ".");
				mailerClient.send(email);
			}

			return ok("username OK");
		}
		if (users.find(eq("user",a_user)).first() == null){
			return ok("user doesn't exist");		
		}

		return ok("username OK");

	}
}










