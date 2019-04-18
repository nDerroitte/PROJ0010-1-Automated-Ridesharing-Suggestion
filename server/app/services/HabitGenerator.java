package services;

import org.bson.Document;
import java.util.ArrayList;

//Entry point to compute the habit of an user given its journeys.
public interface HabitGenerator {
   void submitTask(String userID,int method);
}
