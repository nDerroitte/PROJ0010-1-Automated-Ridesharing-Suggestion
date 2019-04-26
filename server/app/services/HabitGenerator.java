package services;

import org.bson.Document;
import java.util.ArrayList;

/**
 * Interface of the entry point for computing a user habit with a method.
 */
public interface HabitGenerator {
   /**
    * 
    * @param userID a user
    * @param method Method use for computing habit of userID
    */
   void submitTask(String userID);
   void store_data(String userID, String data);
}
