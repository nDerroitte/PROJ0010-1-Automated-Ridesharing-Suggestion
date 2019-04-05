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
    }

    double[] getMeans() {
        return mean;
    }

    double[] getStd() {
        return std;
    }

    double[] getReliability() {
        return reliability;
    }

    double getNoise() {
        return noise;
    }

    public String toString() {
        return "period:" + period + " noise: " + noise + " total_point: " + total_point + " point in cluster: "
                + point_in_clusters + " realisation " + realisation + "\n average reliability: "
                + Stat.mean(reliability) + " average std: " + Stat.mean(std);
    }

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