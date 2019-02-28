package services;
import java.io.IOException;
import java.util.ArrayList;
import services.ComputeHabit;

public class TestComputeHabit{
    
    public static void main(String[] args) throws IOException{
        ArrayList<Long> raw_data = new ArrayList<Long>();
        raw_data.add((long)50);
        raw_data.add((long)100);
        raw_data.add((long)100);
        raw_data.add((long)150);
        raw_data.add((long)200);
        raw_data.add((long)250);
        raw_data.add((long)50);

        ComputeHabit c = new ComputeHabit(raw_data,10);
        System.out.println(c.getSignal());
        System.out.println("computing period...");
        c.getHabit();
    }
}
