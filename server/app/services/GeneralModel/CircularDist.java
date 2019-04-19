package services;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
/**
 * Compute the distance between point living in a circular space.
 */
public class CircularDist implements DistanceMeasure{
    int period = -1;

    /**
     * @param period: cardinality of the circular space.
     * 
     */
    public CircularDist(int period){
        this.period = period;
    }

    /**
     * 
     * @param a first point with positive coordinate
     * @param b second point with positive coordinate
     * @return the circular distance between a and b
     * 
     * Exemple: period = 10; a = 2; b = 9; return 3
     */
    @Override
    public double compute(double[] a, double[] b){
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

 