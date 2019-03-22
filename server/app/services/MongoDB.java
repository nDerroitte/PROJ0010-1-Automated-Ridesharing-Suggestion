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
	String connection = "mongodb://team2:team2@covoituliege-shard-00-00-kisp4.gcp.mongodb.net:27017,covoituliege-shard-00-01-kisp4.gcp.mongodb.net:27017,covoituliege-shard-00-02-kisp4.gcp.mongodb.net:27017/test?ssl=true&replicaSet=covoituliege-shard-0&authSource=admin&retryWrites=true";
	MongoClientURI uri = new MongoClientURI(connection);
	MongoClient mongoClient = new MongoClient(uri);
	MongoDatabase database = mongoClient.getDatabase("covoituliege");

	@Override
	public MongoDatabase get_database() {
       	return this.database;
    }
}
