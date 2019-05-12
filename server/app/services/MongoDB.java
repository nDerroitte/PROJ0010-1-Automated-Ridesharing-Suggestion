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
import services.Keystore;
import javax.inject.*;
import java.security.KeyStore;
import services.EncryptionException;
import javax.crypto.SecretKey;
/**
 * Entry point to the database.
 */
@Singleton
public class MongoDB implements MongoInterface {
	private static String connection; // = "mongodb://localhost:27017";
	private static MongoClientURI uri; // = new MongoClientURI(connection);
	private static MongoClient mongoClient;// = new MongoClient();
	private static MongoDatabase database;// = mongoClient.getDatabase("covoituliege");
	private static SecretKey key;
	public static AES aes;
	/**
	 * Give entry point to the database.
	 */

	@Override
	public MongoDatabase get_database() {
       	return this.database;
    }
	public MongoDB() throws EncryptionException, Exception{
		this.connection = "mongodb://localhost:27017";
		this.uri = new MongoClientURI(connection);
		this.mongoClient = new MongoClient();
		this.database = mongoClient.getDatabase("covoituliege");
		try{
			KeyStore ks = Keystore.createKeystore();
			char[] password = "9dh4gkd8".toCharArray();
			this.key = (SecretKey) Keystore.gettingKey(ks, "keyAlias1", password);
		}catch(EncryptionException e){
			e.printStackTrace();
            throw new EncryptionException("Error during the creation of the keystore.");
		}
		this.aes = new AES(this.key);
	}
}
