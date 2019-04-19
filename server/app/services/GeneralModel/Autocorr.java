package services;

import org.apache.commons.math3.transform.FastFourierTransformer;
import java.util.Arrays;
import java.io.IOException;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization ;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.FastMath;
/**
 * Class for computating the autocorrelation of a signal
 * 
 */
public class Autocorr{
    //the autocorrelation is computed with the Wiener–Khinchin theorem
    private FastFourierTransformer fourier = new FastFourierTransformer(DftNormalization.STANDARD);
/**
 * 
 * @param data: signal from which the autocorrelation is computed.
 * @param max_lag: The maximum time delay for which the autocorrelatin is computed.
 * max_lag must be an integer in the range [0 signal.length]
 * @return the autocorrelation function for lag = 0 up to max_lag
 */
    public double[] compute(double[] data, int max_lag){

        //find $a the closest power of two bigger or equal to data.length
        double a = FastMath.log(2,data.length-0.00001);
        double next_2 = Math.pow(2,Math.ceil(a));
        
        //zero pad the data to have an array of size $a
        double[] input = Arrays.copyOf(data,(int)next_2);
        double mean = 0;

        //normalize data to have a mean of zero.
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

        //map the signal to the frequency domain
        Complex[] f1 = fourier.transform(input,TransformType.FORWARD);

        //compute the magnitude.
        for(int i=0; i< f1.length; i++){
            f1[i] = f1[i].multiply(f1[i].conjugate());
        }

        //return back to time domain.
        Complex[] f2 = fourier.transform(f1,TransformType.INVERSE);
        double[] out = new double[max_lag];

        //normailze output
        for(int i=0; i < Math.min(max_lag,input.length-1); i++){
            out[i] = f2[i].divide(f2[0]).getReal();
        }
        return out;
    }
}
