package controllers;

import java.util.ArrayList;

import play.mvc.*;
import play.mvc.Http.*;
import services.*;
import javax.json.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import com.fasterxml.jackson.databind.*;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.List;

/**
 * Class that find all commons habit of two user.
 */
@Singleton
public class CompareUser extends Controller {

    /**
     * Entry point to the database.
     */
    private final MongoDatabase database;

    /**
     * 
     * @param db entry point to the database
     */
    @Inject
    public CompareUser(MongoInterface db) {
        this.database = db.get_database();
    }

    /**
     * Find all commons habit between user1 and user2 
     * 
     * @param user1 a user
     * @param user2 another user
     * @return Habit of user1 followed by a similar habit of user2
     */
    public Result compare_user(String user1, String user2) {
        String json = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserCompare comparator = new UserCompare(user1, user2, database.getCollection("users"));
            List<Habit> commons_habit = comparator.SimilarHabits();
            json = objectMapper.writeValueAsString(commons_habit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok(json);
    }
}