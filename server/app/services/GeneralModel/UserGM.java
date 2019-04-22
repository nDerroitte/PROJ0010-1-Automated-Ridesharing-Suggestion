package services;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
import java.io.PrintWriter;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

import com.mongodb.client.MongoCollection;
import java.text.ParseException;

import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
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
     * Extract the data of user_id and set the method used for computing habits.
     * @param user_id ID of the user
     * @param database Entry point to DB containt user journey.
     * @param mode Tell which method to use for computing all habit.
     * @throws ParseException if the document from the DB cannot be instance in a java class.
     */
    public UserGM(String user_id, MongoCollection<Document> database, int mode) throws ParseException {
        this.user_id = user_id;
        this.mode = mode;
        this.unused_journeys = new ArrayList<>();

        //get user journey from database
        Document user = database.find(eq("user", user_id)).first();
        if (user == null ) {
            System.err.println("User: " + user_id + " not in DB");
        }
        else{
            ArrayList<Document> journeys = (ArrayList<Document>)(user.get("journeys"));
            for(Document journey : journeys){
                unused_journeys.add(Journey.fromDoc(journey));
            }
        }
    }

    /**
     * Launch the computation of the habits.
     */
    public void createHabits() {

        // Compute habit on all data
        HashMap<JourneyPath, ArrayList<Journey>> sorted_journey = sortJourneyByPath();
        Iterator it = sorted_journey.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            LinkedList<Habit> habits = new LinkedList<>();
            ArrayList<Journey> data = (ArrayList<Journey>) pair.getValue();
            if (mode == 0) {
                ComputeHabit computer = new ComputeHabit(journeyToLong(data),1440);
                habits.addAll(computer.getHabit());
            }
            if (mode == 1){
                ComputeHabit computer = new ComputeHabit(journeyToLong(data),1440*7);
                habits.addAll(computer.getHabit());                
            }
            // Compute habit on day subset.
            else {
                HashMap<Integer, ArrayList<Journey>> journey_by_day = sortJourneyByDay(data);
                Iterator byday = journey_by_day.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    ArrayList<Journey> databyday = (ArrayList<Journey>) entry.getValue();
                    ComputeHabit computer = new ComputeHabit(journeyToLong(databyday),1440);
                    habits.addAll(computer.getHabit());
                }
            }
            for(Habit habit : habits){
                habit.firstLocation = (JourneyPath) pair.getKey().start;
                habit.lastLocation = (JourneyPath) pair.getKey().end;
            }                    
            habitsTofile(habits, (JourneyPath) pair.getKey());
            System.out.println("User: " + user_id + "done");
        }
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
     * @param path Path of the habit.
     */
    public void habitsTofile(LinkedList<Habit> habits, JourneyPath path) {
        try {
            File root = new File("C:/Users/cedri/PI/server/user_habit/" + user_id + "/" + mode);
            root.mkdirs();
            int i= 0;
            for (Habit habit : habits) {
                File file = new File(root.getPath(),"habit "+ i +" " + path.toString() + ".habit");
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write( habit.toString() + "\n" );
                i++;            
                writer.close();
            }
        } catch (Exception e) {
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

}
