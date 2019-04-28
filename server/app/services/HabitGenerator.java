package services;

import org.bson.Document;
import java.util.ArrayList;

/**
 * Interface of the entry point for computing a user habit with a method.
 */
public interface HabitGenerator {
   /**
    * Compute the habit of an user.
    * @param userID ID of the user
    */
   void submitTask(String userID);
   /**
    * Push the data of a user into the database. 
    * @param userID a user iD
    * @param data the data to be pushed.
    */
   void store_data(String userID, String data);
}
