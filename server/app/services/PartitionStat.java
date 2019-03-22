package services;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import services.ComputeHabit;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.List;
import java.util.Iterator;

public class PartitionStat {
    List<Cluster<DoublePoint>> partition;
    private double[] mean;
    private double[] std;
    private double[] reliability;
    private double noise;
    private double log_likelihood;
    private double period;
    private int point_in_clusters;
    private int realisation;
    private int total_point;

    public PartitionStat(List<Cluster<DoublePoint>> p, int r, double period, int total_point) {
        partition = p;
        realisation = r;
        mean = new double[partition.size()];
        std = new double[partition.size()];
        reliability = new double[partition.size()];
        this.period = period;
        this.total_point = total_point;
        log_likelihood = 0;
    }

    double[] getMeans() {
        return mean;
    }

    double[] getStd() {
        return std;
    }

    public double getlogLikelihood() {
        if (log_likelihood != 0) {
            return log_likelihood;
        }
        Iterator<Cluster<DoublePoint>> ite = partition.iterator();
        if (!ite.hasNext()) {
            //System.out.println("no cluster ofr period " + period/1440);
            return Double.NEGATIVE_INFINITY;
        }
        log_likelihood = 0;
        point_in_clusters = 0;
        for (int i = 0; ite.hasNext(); i++) {
            Cluster<DoublePoint> c = ite.next();
            double[] tmp = Stat.clusterStat(c, (int) period);
            mean[i] = tmp[0];
            std[i] = tmp[1];
            reliability[i] = (double) c.getPoints().size() / (double) realisation;
            if (reliability[i] > 1) {
                //System.out.println("reliability > 1 " + reliability[i] + " for a period of: " + period/1440);
                //System.out.println("realisation: " + realisation + " cluster point " + c.getPoints().size());
                reliability[i] = 1;
            }
            point_in_clusters += c.getPoints().size();
            if (std[i] > 0) {
                NormalDistribution normal = new NormalDistribution(tmp[0], tmp[1]);
                Iterator<DoublePoint> ite2 = c.getPoints().iterator();
                while (ite2.hasNext()) {
                    DoublePoint next = ite2.next();
                    double pt_likelihood = 0;
                    double x = next.getPoint()[0];
                    pt_likelihood = normal.density(x+period) + normal.density(x) + normal.density(x-period);                   
                    if(Math.pow(pt_likelihood,1.0/(double)realisation) == 0){
                       // System.out.println("1/real: " + (1.0/realisation));
                        //System.out.println("mean: " + tmp[0] + " std: " + tmp[1] + " point: " + next.getPoint()[0]);
                    }
                    log_likelihood += Math.log(Math.pow(pt_likelihood,1.0/(double)realisation));
                }
            } else {
                Iterator<DoublePoint> ite2 = c.getPoints().iterator();
                while (ite2.hasNext()) {
                    log_likelihood += Math.log(1);
                    ite2.next();
                }
            }
            if (1 - reliability[i] > 0) {
                log_likelihood += Math.log(Math.pow(1 - reliability[i], 1 - c.getPoints().size()/(double) realisation));
            }
            log_likelihood += Math.log(Math.pow(reliability[i], (double) c.getPoints().size()/(double) realisation));
        }
        noise = (total_point - point_in_clusters) / total_point;
        if(noise > 1){
            //System.out.println("noise > 1: " + noise);
        }
        if (noise > 0) {
            log_likelihood += Math.log(Math.pow(noise, (double) (total_point - point_in_clusters)/(double) realisation));
        }
        return log_likelihood;
    }

}