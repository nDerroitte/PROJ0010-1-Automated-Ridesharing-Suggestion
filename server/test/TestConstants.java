import static org.junit.Assert.*;
import org.junit.Test;

import services.Constants;

public class TestConstants{
    @Test
    public void transfo(){
        System.out.println(Constants.CoordinateTransformation(1,1+0.01));
        System.out.println(Constants.CoordinateTransformation(1,1+0.001));
        System.out.println(Constants.CoordinateTransformation(1,1+0.0001));
        System.out.println(Constants.CoordinateTransformation(1,1+0.00001));
        System.out.println(Constants.CoordinateTransformation(1,1+0.000001));
        System.out.println(Constants.CoordinateTransformation(1,1+0.0000001));






    }
}