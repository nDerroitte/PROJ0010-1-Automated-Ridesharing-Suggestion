package services;

import org.apache.commons.math3.transform.FastFourierTransformer;
import java.util.Arrays;
import java.io.IOException;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization ;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.FastMath;
/**
 * Compute the autocorrelation of a signal.
 * 
 * The autocorrelation is computed with two FFT in O(nlog(n))
 * See <a href="https://en.wikipedia.org/wiki/Autocorrelation#Efficient_computation">wikipedia</a>
 */
public class Autocorr{

    /**
     * Fourier transform used for computeing the autocorrelation.
     */
    private FastFourierTransformer fourier = new FastFourierTransformer(DftNormalization.STANDARD);
/**
 * 
 * @param data: Input signal
 * @param max_lag: A time delay in range [0 signal.length]
 * @return double[max_lag], the autocorrelation function for a delay of 0 up to max_lag
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
