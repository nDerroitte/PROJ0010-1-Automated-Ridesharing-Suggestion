import services.Autocorr;

import java.util.Arrays;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestAutocorr{

    @Test //autocorr on periodic data.   
    public void periodic(){
       double[] data = {0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1};
       Autocorr a = new Autocorr();
       double[] out = a.compute(data,100);
       out[0] = -1;
       boolean expected_out = true;
       double max = -1;
       int max_index = 0;
       for(int i=0; i < out.length; i++){
            if(i % 4 == 0){
                expected_out &= (0.99 < out[i] && out[i] < 1.01) ;
            }
       }
       System.out.println(Arrays.toString(out));
       assertTrue(max_index %4 == 0);
    }

}