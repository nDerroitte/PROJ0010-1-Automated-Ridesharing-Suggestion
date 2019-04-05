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

public class UserGM {
    private ArrayList<Journey> unused_journeys;
    private String user_id;
    private int mode;

    public UserGM(String user_id, ArrayList<Journey> journeys, int mode) {
        this.unused_journeys = journeys;
        this.user_id = user_id;
        this.mode = mode;
    }

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
            habitsTofile(habits, (JourneyPath) pair.getKey());
            System.out.println("User: " + user_id + "done");
        }
    }

    public void printHabits(LinkedList<Habit> habits) {
        for (Habit habit : habits) {
            System.out.println(habit.toString());
        }
    }

    public void habitsTofile(LinkedList<Habit> habits, JourneyPath path) {
        try {
            File root = new File("user_habit/" + user_id + "/" + mode);
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

    ArrayList<Long> journeyToLong(ArrayList<Journey> journeys) {
        ArrayList<Long> out = new ArrayList<Long>();
        for (Journey journey : journeys) {
            out.add(journey.getFirstPointTime().getTimeInMillis());
        }
        return out;
    }

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
