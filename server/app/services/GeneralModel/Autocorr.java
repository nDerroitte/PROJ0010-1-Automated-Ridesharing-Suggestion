package services;

import org.apache.commons.math3.transform.FastFourierTransformer;
import java.util.Arrays;
import java.io.IOException;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization ;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.FastMath;

public class Autocorr{
    FastFourierTransformer fourier = new FastFourierTransformer(DftNormalization.STANDARD);

    public double[] compute(double[] data, int max_lag){
        double a = FastMath.log(2,data.length-0.00001);
        double next_2 = Math.pow(2,Math.ceil(a));
        double[] input = Arrays.copyOf(data,(int)next_2);
        double mean = 0;
        for(int i=0; i < input.length; i++){
            mean += input[i];
        }
        mean /= input.length;
        for(int i=0; i < input.length; i++){
            input[i] -= mean;
        }
        if(input.length == 0){
            input = new double[1];
        }
        Complex[] f1 = fourier.transform(input,TransformType.FORWARD);
        for(int i=0; i< f1.length; i++){
            f1[i] = f1[i].multiply(f1[i].conjugate());
        }
        Complex[] f2 = fourier.transform(f1,TransformType.INVERSE);
        double[] out = new double[max_lag];
        for(int i=0; i < Math.min(max_lag,input.length-1); i++){
            out[i] = f2[i].divide(f2[0]).getReal();
        }
        return out;
    }
}
