package services;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import services.ComputeHabit;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.List;
import java.util.Iterator;
/**
 * Class for making stat on a partition. The Stat have only sense if the partition come from ComputeHabit.
 * 
 * A partition is a list of cluster. Each cluster contains the date when an habit occur.
 * @see [[habit]]
 * @see [[computeHabit]]
 */
class PartitionStat {
    List<Cluster<DoublePoint>> partition; //the partition on which stat are made
    private double[] mean; // the circular mean of each cluster of the partition
    private double[] std; //the circular standard deviation of each cluster of the partition
    private double[] reliability; //the reliability of an habit by the cluster
    private double noise;
    private double period; //the period of the habit represented by the cluster.
    private int point_in_clusters; //the total number of point in all cluster.
    private int realisation; //the number of time an habit should occur.
    private int total_point; //the number of date the habit is calculated from.

    /**
     * 
     * @param p the partition
     * @param r the number of realisation of an habit in a cluster.
     * @param period the period of the habit in a cluster
     * @param total_point the total number of point on which the habit is computed by ComputeHabit.
     */
    public PartitionStat(List<Cluster<DoublePoint>> p, int r, double period, int total_point) {
        partition = p;
        realisation = r;
        mean = new double[partition.size()];
        std = new double[partition.size()];
        reliability = new double[partition.size()];
        this.period = period;
        this.total_point = total_point;
    }
/**
 * 
 * @return the circular mean of each cluster
 */
    double[] getMeans() {
        return mean;
    }
/**
 * 
 * @return the circular standard deviation of each cluster.
 */
    double[] getStd() {
        return std;
    }
/**
 * 
 * @return the reliability of each habit represented by a cluster.
 */
    double[] getReliability() {
        return reliability;
    }
/**
 * 
 * @return the number of date the habit explained over all date on which the habit is calculated from.
 */
    double getNoise() {
        return noise;
    }

    public String toString() {
        return "period:" + period + " noise: " + noise + " total_point: " + total_point + " point in cluster: "
                + point_in_clusters + " realisation " + realisation + "\n average reliability: "
                + Stat.mean(reliability) + " average std: " + Stat.mean(std);
    }
/**
 * compute all stat of the partition. Should be call before calling a getter to have the rigth information.
 */
    public void compute() {
        Iterator<Cluster<DoublePoint>> ite = partition.iterator();
        if (!ite.hasNext()) {
            return;
        }
        int point_explained = 0;
        for (int i = 0; ite.hasNext(); i++) {
            Cluster<DoublePoint> c = ite.next();
            double[] tmp = Stat.clusterStat(c, (int) period);
            mean[i] = tmp[0];
            std[i] = tmp[1];
            reliability[i] = Math.max(1,(double) c.getPoints().size() / (double) realisation);
            point_explained += Math.min(c.getPoints().size(), realisation);
        }
        this.noise = (total_point - point_explained) / (double) total_point;
        return;
    }
}