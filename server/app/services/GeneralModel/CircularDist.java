package services;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
/**
 * Compute the distance between point living in a circular space.
 */
public class CircularDist implements DistanceMeasure{
    int period = -1;

    /**
     * 
     * @param period a A positive integer. The circular space domain are the integer in [0,period]
     * @throws IllegalArgumentException if period is negative.
     */
    public CircularDist(int period) throws IllegalArgumentException{
        if(period < 0){
            throw new IllegalArgumentException("The argument must be positive");
        }
        this.period = period;
    }

    /**
     * 
     * @param a A double[] with one positive double
     * @param b Another double[] with one positive double
     * @return The circular distance between a and b
     * @throws IllegalArgumentException if a or b are negative.
     * Exemple: this.period = 10; a = 2; b = 9; return 3
     * 
     *
     */
    @Override
    public double compute(double[] a, double[] b) throws IllegalArgumentException{
        if(a[0] < 0 || b[0] < 0){
            throw new IllegalArgumentException();
        }
        double a_ = a[0] % period;
        double b_ = b[0] % period;
        if(a_ > b_){
            double c = a_;
            a_ = b_;
            b_ = c;
        }
        return Math.min(period+a_ - b_, b_- a_);
    }
}

 