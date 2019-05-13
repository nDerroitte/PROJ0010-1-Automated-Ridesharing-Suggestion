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
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

import services.MongoInterface;
import services.EncryptionException;
import services.AES;

public class ClearDatabase extends Controller {

         private final MongoDatabase database ;

        @Inject
        public ClearDatabase (MongoInterface db){
                this.database = db.get_database();
        }

        public Result clear_database() throws EncryptionException{
                MongoCollection<Document> users = database.getCollection("users");
                BasicDBObject doc = new BasicDBObject();
                users.deleteMany(doc);
                return ok();
        }
}
