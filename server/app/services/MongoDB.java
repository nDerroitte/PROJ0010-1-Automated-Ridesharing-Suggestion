package services;

import com.mongodb.MongoClientURI ;
import com.mongodb.MongoClient;

import com.mongodb.ServerAddress;
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

import javax.inject.*;

@Singleton
public class MongoDB implements MongoInterface{
	//String connection = "";
	//MongoClientURI uri = new MongoClientURI(connection);
	MongoClient mongoClient = new MongoClient();
	MongoDatabase database = mongoClient.getDatabase("covoituliege");

	@Override
	public MongoDatabase get_database() {
       	return this.database;
    }
}
