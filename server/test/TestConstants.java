//package test;
import services.Constants;

import org.junit.Test;


public class TestConstants{

    @Test
    public void CoordinateTransfo(){
        System.out.println(Constants.CoordinateTransformation(0, 100).toString());
        System.out.println(Constants.CoordinateTransformation(0, 10).toString());
        System.out.println(Constants.CoordinateTransformation(0, 1).toString());
        System.out.println(Constants.CoordinateTransformation(0, 0.1).toString());
        System.out.println(Constants.CoordinateTransformation(0, 0.01).toString());
        System.out.println(Constants.CoordinateTransformation(0, 0.001).toString());
        System.out.println(Constants.CoordinateTransformation(0, 0.0001).toString());
        System.out.println(Constants.CoordinateTransformation(0, 0.00001).toString());
        System.out.println(Constants.CoordinateTransformation(0, 0.000001).toString());
        System.out.println(Constants.CoordinateTransformation(0, 0.0000001).toString());       
    }
}