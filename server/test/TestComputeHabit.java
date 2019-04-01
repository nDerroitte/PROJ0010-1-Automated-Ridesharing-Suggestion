import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import services.ComputeHabit;
import services.Habits;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

import akka.dispatch.forkjoin.ThreadLocalRandom;
import jdk.jfr.Timestamp;

import static org.junit.Assert.*;
import services.CircularDist;

public class TestComputeHabit {

    //all in minutes; Generate fake data
    public ArrayList<Long> new_data(int period, int spread, double reliability, int offset,int noise, long range){
        long scale = 1000*60;
        ArrayList<Long> out = new ArrayList<Long>();
        long base_date = new Date().getTime();
        long curent_date = base_date + offset * scale;
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

    @Test 
    public void EmptyInput(){
        ArrayList<Long> empty = new ArrayList<Long>();
        ComputeHabit ch = new ComputeHabit(empty);
        assertTrue(ch.getHabit().size() == 0);
    } 

    @Test 
    public void simpleHabit(){
        int period = 10080;
        int spread = 0;
        double reliability = 1;
        int noise = 0;
        long range = 5*period;
        int offset = 1440;
        Habits expected_out = new Habits();     
        ArrayList<Long> data = new_data(period,spread,reliability,offset,noise,range);
        expected_out.offset = data.get(0);
        expected_out.period = period/1440;
        expected_out.spread = spread;
        expected_out.reliability = reliability;          

        ComputeHabit ch = new ComputeHabit(data);
        LinkedList<Habits> habits = ch.getHabit(); 
        assertTrue(habits.size() == 1);
        assertEquals(expected_out,habits.getFirst());
    }
    
    public void testCircDist(){
        int period = 44640;
        double[] a = {5754};
        double[] b = {1435};
        CircularDist c = new CircularDist(period);
        System.out.println(c.compute(a,b));
    }

    //realistic case.
    public void testSignal() throws IOException{

        int[] hit = new int[6];
        for(int j=1; j < hit.length; j++){
            for (int k = 0; k < 100; k++){
                //generate fake data:
                long range = 10080*3;
                ArrayList<Long> raw_data = new ArrayList<Long>();
                for(int i=0; i < j; i++){
                    raw_data.addAll(new_data(10080,60,0.7,10080/7*i,0,range));
                }
                ComputeHabit c = new ComputeHabit(raw_data);
                LinkedList<Habits> habits = c.getHabit();
                if(habits.size() > 0 && habits.getFirst().period % 10080 == 0){
                    hit[j] ++;
                }                
            }          
        }
        System.out.println(Arrays.toString(hit));
    }
}
