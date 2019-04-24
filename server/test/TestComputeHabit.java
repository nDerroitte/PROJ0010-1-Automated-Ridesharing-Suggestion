import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import services.ComputeHabit;
import services.Habit;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

import akka.dispatch.forkjoin.ThreadLocalRandom;
//import jdk.jfr.Timestamp;

import static org.junit.Assert.*;
import services.CircularDist;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.Cluster;

/**
 * Test the ComputeHabit classe
 */
public class TestComputeHabit {
/**
 * 
 * @param period the habit period
 * @param spread the habit spread
 * @param reliability the habit reliability
 * @param offset the first date the habit occur
 * @param noise the number of journey with the same start and end point which is not part of the habit
 * @param range a long 
 * @return the date at which the habit occur after collecting data during range minute plus noise date.
 * @see [[HabitGM]]
 */
    public ArrayList<Long> new_data(int period, int spread, double reliability, long base_date,int noise, long range){
        long scale = 1000*60;
        ArrayList<Long> out = new ArrayList<Long>();
        long curent_date = base_date;
        while(curent_date < base_date + scale*range){
            double proba = Math.random();
            if(proba < reliability){
                long spread_noise = 0;
                if(spread > 0){
                    spread_noise = java.util.concurrent.ThreadLocalRandom.current().nextLong(spread*scale);                    
                }
                if(Math.random() < 0.5){
                    out.add(curent_date + spread_noise);
                }
                else{
                    out.add(curent_date - spread_noise);
                }
            }
            curent_date += period*scale;
        }
        for(int i=0 ; i < noise; i++){
            out.add(base_date + java.util.concurrent.ThreadLocalRandom.current().nextLong(range*scale));
        }
        return out;
    }

    /**
     * Test if ComputeHabit crash on empty entry.
     */
    @Test 
    public void emptyInput(){
        ArrayList<Long> empty = new ArrayList<Long>();
        ComputeHabit ch = new ComputeHabit(empty,1440);
        assertTrue(ch.getHabit().size() == 0);
    } 

    /**
     * Check the internal signal rpresentation of the data.
     */
    @Test 
    public void signal(){
        ArrayList<Long> data = new ArrayList<>();
        data.add(Long.valueOf(6*60000));
        data.add(Long.valueOf(2*60000));
        data.add(Long.valueOf(8*60000));
        ComputeHabit ch = new ComputeHabit(data,1);
        double[] out = ch.getSignal();
        double[] expected_out = {1,0,0,0,1,0,1};
        assertTrue(Arrays.equals(expected_out,out));
    }

    /**
     * Check if the habit find is the expected one.
     */
    @Test 
    public void simpleHabit(){
        int period = 10080;
        int spread = 0;
        double reliability = 1;
        int noise = 0;
        long range = 5*period;
        int offset = 1440;
        Habit expected_out = new Habit();     
        ArrayList<Long> data = new_data(period,spread,reliability,offset,noise,range);
        expected_out.offset = data.get(0);
        expected_out.period = period/1440;
        expected_out.reliability = reliability;          

        ComputeHabit ch = new ComputeHabit(data,1440);
        LinkedList<Habit> habits = ch.getHabit(); 
        assertTrue(habits.size() == 1);
        assertEquals(expected_out,habits.getFirst());
    }

    /**
     * evaluate the computeHabit performence.
     * @throws IOException
     */
    @Test
    public void testSignal() throws IOException{

        int[] hit = new int[5];
        for(int j=1; j < hit.length; j++){
            for (int k = 0; k < 100; k++){
                //generate fake data:
                long range = 10080*15;
                ArrayList<Long> raw_data = new ArrayList<Long>();
                long base_date = new Date().getTime();
                for(int i=0; i < j; i++){
                    base_date += 1440;
                    raw_data.addAll(new_data(10080,60,0.7,base_date,0,range));
                }
                ComputeHabit c = new ComputeHabit(raw_data,1440);
                LinkedList<Habit> habits = c.getHabit();

                if(habits.size() > 0 && habits.getFirst().period % 7 == 0){
                    hit[j] ++;
                }                
            }          
        }
        System.out.println(Arrays.toString(hit));
    }
}
