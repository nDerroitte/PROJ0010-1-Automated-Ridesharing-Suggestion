
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import services.ComputeHabit;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

import akka.dispatch.forkjoin.ThreadLocalRandom;

import static org.junit.Assert.*;
import services.CircularDist;

public class TestComputeHabit {

    //all in minit
    public ArrayList<Long> new_data(int period, int spread, double reliability, int offset,int noise, long range){
        long scale = 1000*60;
        ArrayList<Long> out = new ArrayList<Long>();
        long base_date = new Date().getTime();
        long curent_date = base_date + offset * scale;
        while(curent_date < base_date + scale*range){
            double proba = Math.random();
            if(proba < reliability){
                long spread_noise = java.util.concurrent.ThreadLocalRandom.current().nextLong(spread*scale);
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
    
    public void testCircDist(){
        int period = 44640;
        double[] a = {5754};
        double[] b = {1435};
        CircularDist c = new CircularDist(period);
        System.out.println(c.compute(a,b));
    }

    @Test
    public void testSignal() throws IOException{

        //generate fake data:
        long range = 10080*10;
        ArrayList<Long> raw_data = new ArrayList<Long>();
        for(int i=0; i < 5; i++){
            raw_data.addAll(new_data(10080,30,0.8,10080/7*i,10,range));
        }

        ComputeHabit c = new ComputeHabit(raw_data);
        System.out.println(c.getSignal());
        System.out.println("computing period...");
        c.getHabit();

    }
}
