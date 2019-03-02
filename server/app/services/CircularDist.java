package services;

import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class CircularDist implements DistanceMeasure{
    int period = -1;
    public CircularDist(int period){
        this.period = period;
    }

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

 