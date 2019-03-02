package services;

import java.io.IOException;
import java.util.ArrayList;
import services.ComputeHabit;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import static org.junit.Assert.*;
import services.ComputeHabit;

public class TestComputeHabit {

    @Test
    public void testSignal() throws IOException{

        ArrayList<Long> raw_data = new ArrayList<Long>();
        raw_data.add((long) 1440);
        raw_data.add((long) 2 * 1440);
        raw_data.add((long) 3 * 1440);
        raw_data.add((long) 5 * 1440);
        raw_data.add((long) 4 * 1440 + 10);
        raw_data.add((long) 6 * 1440);
        raw_data.add((long) 7 * 1440);
        raw_data.add((long) 7 * 1440);
        raw_data.add((long) 5 * 1440 + 2);
        raw_data.add((long) 4 * 1440 + 3);
        raw_data.add((long) 6 * 1440 + 4);

        ComputeHabit c = new ComputeHabit(raw_data, 1);
        System.out.println(c.getSignal());
        System.out.println("computing period...");
        c.getHabit();

    }
}
