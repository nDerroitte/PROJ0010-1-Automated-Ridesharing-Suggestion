package services;

import ca.pfv.spmf.algorithms.timeseries.autocorrelation.AlgoLagAutoCorrelation;
import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

public class ComputeHabit {

    ArrayList<Long> raw_data;
    Long[] index;
    Long base;
    double[] signal;
    long scale = 60000;
    int minimal_period = 1440;

    public ComputeHabit(ArrayList<Long> array) {
        Collections.sort(array);
        //System.out.println(array);
        raw_data = array;
        base = array.get(0);
        int signal_size = Math.toIntExact((array.get(array.size() - 1) - base) / scale) + 1;
        //System.out.println("signal_size" + signal_size);
        index = new Long[array.size()];
        Iterator<Long> ite = array.iterator();
        signal = new double[signal_size];
        int i = 0;
        int j = 0;
        // format data to a signal for the autocorrelation.
        while (ite.hasNext()) {
            index[i] = (ite.next() - base) / scale;
            while (j < index[i]) {
                signal[j] = 0;
                j++;
            }
            // skip repeated value in input signal => should never happend.
            if (j < signal.length && (i == 0 || index[i] != index[i - 1])) {
                signal[j] = 1;
                j++;
            }
            i++;
        }
        System.out.println("index: " + Arrays.toString(index));
    }

    public double[] getSignal() {
        return this.signal;
    }

    public void getHabit() throws IOException {
        //get the possible period.
        LinkedHashSet<Integer> periods = find_period();
        System.out.println(Arrays.toString(periods.toArray()));
        Iterator<Integer> ite = periods.iterator();
        List<List<Cluster<DoublePoint>>> partitions = new ArrayList<List<Cluster<DoublePoint>>>();
        ArrayList<int[]> counts = new ArrayList<int[]>();

        double best_score = Double.POSITIVE_INFINITY;
        int best_score_index = 0;
        int i = 0;
        while (ite.hasNext()) {
            Integer period = ite.next();
            int[] count = new int[signal.length / period];
            ArrayList<DoublePoint> index = format_signal(period, count);
            List<Cluster<DoublePoint>> partition = cluster(index,period,count);
            double score = partitionScore(partition,period,count);
            //System.out.println(score);
            if(score < best_score){
                best_score = score;
                best_score_index = i;
                System.out.println("\n new best score: " + score + " for a period of " + period/1440 + " day with cluster: ");
                printPartition(partition);
            }
            else{
                if(score < Double.POSITIVE_INFINITY){
                    System.out.println("\n score of: " + score + " for a period of " + period/1440 + " day with cluster: ");
                    printPartition(partition);
                }
            }

            //memorize process.
            partitions.add(partition);
            counts.add(count);
            i++;
        }
        //write habit with best score. .
        List<Cluster<DoublePoint>> partition = partitions.get(best_score_index);
        Iterator<Cluster<DoublePoint>> part_ite = partition.iterator();
        LinkedList<Habits> habits = new LinkedList<Habits>();

    }

    private double partitionScore(List<Cluster<DoublePoint>> part, int period,int count[]){
        double score = 0;
        int nb_cluster = 0;
        double[] mean_std = meanStd(count);
        Iterator<Cluster<DoublePoint>> ite = part.iterator();
        int point_in_cluster = 0;
        while(ite.hasNext()){
            Cluster<DoublePoint> cluster = ite.next();
            score += clusterMeanVar(cluster,period)[1];
            nb_cluster++;
            point_in_cluster += cluster.getPoints().size();
        }
        if(nb_cluster == 0){
            return Double.POSITIVE_INFINITY;
        }
        if(nb_cluster > mean_std[0] - 2*mean_std[1] && nb_cluster < mean_std[0] + 2*mean_std[1]){
            //favorise partitioning that explain most of point.
            return score/(Math.pow(point_in_cluster, 3));
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }

    private double[] clusterMeanVar(Cluster c,int period){
        CircularDist comparator = new CircularDist(period);
        List<DoublePoint> l = c.getPoints();
        Iterator<DoublePoint> ite = l.iterator();
        Complex imean = new Complex(0,0);
        double var = 0;
        while(ite.hasNext()){
            double point = ite.next().getPoint()[0];
            double angle =  point * 2 * Math.PI / period;
            imean = imean.add(ComplexUtils.polar2Complex(1, angle)) ;
        }
        imean.divide(l.size());
        double theta = imean.getArgument();
        double mean = 0;
        if(theta < 0){
            mean = period + period * theta/(2* Math.PI);
        }
        else{
            mean = period * theta/(2* Math.PI);
        }
        ite = l.iterator();
        double[] mean_arr = {mean};
        while(ite.hasNext()){
            DoublePoint p  = ite.next();
            double dist = comparator.compute(mean_arr,p.getPoint());
            var += dist;
        }            
        var /= (l.size() -1 );
        double[] out = {mean,var};
        return out;
    }

    private double[] meanStd(int[] array){
        double mean = 0;
        double var = 0;
        for(int i=0; i < array.length;i++){
            mean += array[i];
        }
        mean /= array.length;
        for(int i=0; i < array.length; i++){
            var += (array[i]-mean) * (array[i]-mean);
        }
        var /= (array.length -1);
        double[] out = {mean,Math.sqrt(var)};
        return out;
    }

    private void printPartition(List<Cluster<DoublePoint>> c){
        Iterator< Cluster<DoublePoint>> ite = c.iterator();
        int i =0;
        System.out.println("printing partition ...");
        while(ite.hasNext()){
            System.out.println("\n Cluster: " + i);
            List<DoublePoint> l = ite.next().getPoints();
            Iterator<DoublePoint> p = l.iterator();
            while(p.hasNext()){
                DoublePoint d = p.next();
                System.out.print(Arrays.toString(d.getPoint()) + ",");
            }
            i++;
        }
    }
    private double mean(double[] array) {
        double total = 0;
        for (int i = 0; i < array.length; i++) {
            total += i;
        }
        return  total / array.length;
    }
    private ArrayRealVector toRealVector(ArrayList<DoublePoint> d){
        double[] array = new double[d.size()];
        for(int i=0;i<d.size();i++){
            array[i] = d.get(i).getPoint()[0];
        }
        return new ArrayRealVector(array);
    }

    private LinkedHashSet<Integer> find_period() throws IOException {
        // period detection with autocorrelation
        AlgoLagAutoCorrelation autocorr = new AlgoLagAutoCorrelation();
        TimeSeries result = autocorr.runAlgorithm(new TimeSeries(signal, "raw data"), signal.length / 2);
        result = autocorr.runAlgorithm(new TimeSeries(result.data, "pre-process data"), signal.length / 2);
        result.data[0] = 0.0;
        IndexSorter is = new IndexSorter(result.data);
        is.sort(false);
        Integer[] ranked_period = is.getIndexes();
        LinkedHashSet<Integer> periods = new LinkedHashSet<Integer>();
        for (int i = 0; i < (int) ranked_period.length * 0.05 + 1; i++) {
            int candidate = Math.round(ranked_period[i] / minimal_period) * minimal_period;
            if (!periods.contains(candidate) && candidate != 0) {
                periods.add(candidate);
            }
        }
        return periods;
    }

    /**  prepare the data for the clustering algorithm.
    gives the index of one of the signal splitted at period.
    Ex: this.signal = 1 0 0 1 0 0 0 1 1 0 0 1 and period = 4
    splitted signal: 1 0 0 1 || 0 0 0 1 || 1 0 0 1 ||
    out => 0 0 3 3 3

    count is a zero vectorof size signal.length/period.
    count will containt the number of non zero element in the split.
    count => [2,1,2]*/
    private ArrayList<DoublePoint> format_signal(int period, int[] count) {
        ArrayList<DoublePoint> out = new ArrayList<DoublePoint>();
        for (int i = 0; i < period - 1; i++) {
            for (int j = 0; j < signal.length - period; j += period) {
                if (signal[i + j] == 1.0) {
                    int[] p = { i };
                    out.add(new DoublePoint(p));
                    count[j / period]++;
                }
            }
        }
        //System.out.println("formatted index: " + out.toString());
        //System.out.println("count: " + Arrays.toString(count));
        return out;
    }

    // return the cluster partitionning of index for a given period.
    private List<Cluster<DoublePoint>> cluster(ArrayList<DoublePoint> index, int period, int[] count) {
        LinkedList<Cluster<DoublePoint>> out = new LinkedList<Cluster<DoublePoint>>();
        // impose a minimum reliability of 50%
        Integer min_point = Math.max(signal.length / (2 * period), 2);

        // try to get an idea of a good epsilon value for dbscan.
        ArrayRealVector original = toRealVector(index);
        ArrayRealVector shift = (ArrayRealVector) original.getSubVector(1, original.getDimension() - 1);
        shift = (ArrayRealVector) shift.append(original.getEntry(0)+period);
        ArrayRealVector diff = (ArrayRealVector) shift.subtract(original);
        IndexSorter is = new IndexSorter(diff.toArray());
        Arrays.sort(is.get_value());
        is.sort(true);
        //System.out.println(" \n to cluster: ");
        //System.out.println(index.toString());
        double epsilon = mean(is.get_value());
        //System.out.println("eps = " + epsilon + " min pt= " + min_point + " period " + period);
        CircularDist measure = new CircularDist(period);
 
        DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(epsilon, min_point,measure);
        List<Cluster<DoublePoint>> result = dbscan.cluster(index);
        //printPartition(result);
        return result;
    }

}