package services;
import java.io.IOException;
import java.util.ArrayList;
import services.ComputeHabit;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.*;

public class TestComputeHabit{
    
    void testSignal(){
        ArrayList<Long> raw_data = new ArrayList<Long>();
        raw_data.add((long)50);
        raw_data.add((long)100);
        raw_data.add((long)100);
        raw_data.add((long)150);
        raw_data.add((long)50);
        double[] result = {1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0};
        ComputeHabit c = new ComputeHabit(raw_data,10);
        System.out.println(Arrays.toString(c.getSignal()));
    }
}
