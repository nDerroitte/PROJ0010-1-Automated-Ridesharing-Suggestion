package services;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import java.io.PrintWriter;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.text.ParseException;

import services.EncryptionException;
import services.Decrypt;
import services.Encrypt;
import services.AES;

/**
 * Compute all habit of an user
 */
public class UserGM {
    /**
     * All journeys of the user.
     */
    private ArrayList<Journey> unused_journeys;
    /**
     * id of the user
     */
    private String user_id;
    /**
     * Tell which method to use when computing the habits
     */
    private int mode;

    /**
     * entry point to the database
     */
    private MongoCollection<Document> db;

    /**
     * Extract the data of user_id and set the method used for computing habits.
     * @param user_id ID of the user
     * @param database Entry point to DB containt user journey.
     * @param mode Tell which method to use for computing all habit.
     * @throws ParseException if the document from the DB cannot be instance in a java class.
     */
    public UserGM(String user_id, MongoCollection<Document> database, ArrayList<Journey> journeys) throws ParseException {
        this.unused_journeys = journeys;
        this.user_id = user_id;
        this.db = database;
    }

    /**
     * Launch the computation of the habits.
     */
    public void createHabits() throws EncryptionException{

        // Compute habit on all data
        HashMap<JourneyPath, ArrayList<Journey>> sorted_journey = sortJourneyByPath();
        Iterator it = sorted_journey.entrySet().iterator();
        LinkedList<Habit> habits = new LinkedList<>();
        while (it.hasNext()) {
            LinkedList<Habit> new_habit = new LinkedList<>();
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<Journey> data = (ArrayList<Journey>) pair.getValue();
            ComputeHabit computer = new ComputeHabit(journeyToLong(data),1440);
            new_habit=computer.getHabit();
            for(Habit habit : new_habit){
                habit.firstLocation = ((JourneyPath) pair.getKey()).start;
                habit.lastLocation = ((JourneyPath) pair.getKey()).end;
            }          
            habits.addAll(new_habit);
                 
        }
        habitsTofile(habits);
        habitToDB(habits);
    }

    /**
     * Print a list of habits.
     * @param habits
     */
    public void printHabits(LinkedList<Habit> habits) {
        for (Habit habit : habits) {
            System.out.println(habit.toString());
        }
    }

    /**
     * Write a list of habit into file.
     * @param habits List of habits
     */
    public void habitsTofile(LinkedList<Habit> habits) {
        // Writing in files
        try
        {
            String folderString = "user_habit/"+user_id;
            File newFile = new File(folderString);
            if(!newFile.exists())
                newFile.mkdir();
            File method1 = new File(folderString+"/2");
            method1.mkdir();
            FileWriter fw = new FileWriter(folderString+"/2/habits.txt", false);
            PrintWriter writer = new PrintWriter(fw);
            writer.printf("User %s.\n\n", user_id);
            for(Habit h : habits)
            {
                writer.println(h);
                writer.printf("========================================================================================\n");
            }
            writer.close();
        }
        catch(IOException e)
        {
            System.err.println("Error writing in file.");
            e.printStackTrace();
        }
    }
    /**
     * Sort journey by day.
     * @param journeys: list of journey to sort.
     * @return An hash set. Each value store the journey that start at same week day.
     * The mapping key,day is given by the class Calendar.
     * @see Calendar
     */
    HashMap<Integer, ArrayList<Journey>> sortJourneyByDay(ArrayList<Journey> journeys) {
        HashMap<Integer, ArrayList<Journey>> out = new HashMap<>();
        for (Journey journey : journeys) {
            Calendar date_journey = journey.getFirstPointTime();
            int key = date_journey.get(Calendar.DAY_OF_WEEK) - 1;
            if (out.containsKey(key)) {
                out.get(key).add(journey);
            } else {
                ArrayList<Journey> array = new ArrayList<>();
                array.add(journey);
                out.put(key, array);
            }
        }
        return out;
    }

    /**
     * Extract the date the journey begin as a long.
     * @param journeys List of journey.
     * @return List of long, each long represent the date at which the journey begin.
     */
    ArrayList<Long> journeyToLong(ArrayList<Journey> journeys) {
        ArrayList<Long> out = new ArrayList<Long>();
        for (Journey journey : journeys) {
            out.add(journey.getFirstPointTime().getTimeInMillis());
        }
        return out;
    }

    /**
     * Sort journey by the GPS coordinate of their start and end point.
     * @return An hashmap. Each value is list of journey which start an end at a similar GPS point.
     */
    public HashMap<JourneyPath, ArrayList<Journey>> sortJourneyByPath() {
        HashMap<JourneyPath, ArrayList<Journey>> out = new HashMap<>();
        for (Journey journey : unused_journeys) {
            JourneyPath key = new JourneyPath(journey.getPath());
            if (out.containsKey(key)) {
                out.get(key).add(journey);
            } else {
                ArrayList<Journey> array = new ArrayList<>();
                array.add(journey);
                out.put(key, array);
            }
        }
        return out;
    }

    public void habitToDB(LinkedList<Habit> new_habits) throws EncryptionException{
        //Encrypt user id
        ArrayList<Byte> user_id_E = Encrypt.encrypt(user_id);
        Document user = db.find(eq("user", user_id_E)).first();
        //Decrypt
        //laisser les habits cryptée
        ArrayList<Document> habits = (ArrayList<Document>)(user.get("habits"));
        if(habits == null){
            habits = new ArrayList<Document>();
        }
        for(Habit h : new_habits){
            //ce sera deja crypter ici 
            habits.add(h.toDoc());
        }
        //Encrypt user id  
        db.updateOne(eq("user",user_id_E),set("habits", habits));
    }
}
