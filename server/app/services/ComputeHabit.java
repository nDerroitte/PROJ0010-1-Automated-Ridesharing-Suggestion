package services;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.Cluster;

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

        double best_score = Double.NEGATIVE_INFINITY;
        int best_score_index = 0;
        int best_period = 0;
        int i = 0;
        while (ite.hasNext()) {
            Integer period = ite.next();
            int[] count = new int[signal.length / period];
            ArrayList<DoublePoint> index = format_signal(period, count);
            List<Cluster<DoublePoint>> partition = cluster(index,period,count);
            double score = partitionScore(partition,period,index.size());
            //System.out.println(score);
            if(score > best_score){
                best_score = score;
                best_score_index = i;
                best_period = period;
                System.out.println("\n new best score: " + score + " for a period of " + period/1440 + " day with cluster: \n");
                printPartition(partition);
            }
            else{
                if(score != Double.NEGATIVE_INFINITY){
                    System.out.println("\n score of: " + score + " for a period of " + period/1440 + " day with cluster: \n");               
                }
            }
            //memorize process.
            partitions.add(partition);
            counts.add(count);
            i++;
        }            
        System.out.println("Best score is " + best_score + "with period of " + periods.toArray(new Integer[1])[best_score_index]/1440 + " day");

        //write habit with best score. .
        List<Cluster<DoublePoint>> partition = partitions.get(best_score_index);
        Iterator<Cluster<DoublePoint>> part_ite = partition.iterator();
        LinkedList<Habits> habits = new LinkedList<Habits>();
        Cluster<DoublePoint> cluster = null;
        if(part_ite.hasNext()){
            cluster = part_ite.next();
        }
        for( ; part_ite.hasNext(); cluster = part_ite.next()){
            Habits h = new Habits();
            h.period = (long) best_period;
            double[] mean_var = Stat.clusterStat(cluster,best_period); 
            h.offset = base + Math.round(mean_var[0]*scale);
            h.reliability = Math.max(1,(double)cluster.getPoints().size()/(index.length/best_period));
            h.spread = mean_var[1];
            habits.add(h);
        }

    }
    private double partitionScore(List<Cluster<DoublePoint>> part, int period,int nb_point){
        PartitionStat stat = new PartitionStat(part,signal.length/period ,period,nb_point);
        return stat.getlogLikelihood();
    }
    public static void printPartition(List<Cluster<DoublePoint>> c){
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
        System.out.println(" ");
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
        Autocorr autocorr = new Autocorr();
        double[] result = autocorr.compute(signal, signal.length / 2);
        result = autocorr.compute(result, signal.length / 2);
        result[0] = 0.0;
        IndexSorter is = new IndexSorter(result);
        is.sort(false);
        Integer[] ranked_period = is.getIndexes();
        LinkedHashSet<Integer> periods = new LinkedHashSet<Integer>();
        for (int i = 0; i < (int) ranked_period.length * 0.01 + 1; i++) {
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
        double epsilon = Stat.mean(diff.toArray())/2;
        CircularDist measure = new CircularDist(period);
 
        DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(epsilon, min_point,measure);
        List<Cluster<DoublePoint>> result = dbscan.cluster(index);
        ListIterator<Cluster<DoublePoint>> ite = result.listIterator();
        while(ite.hasNext()){
            Cluster<DoublePoint> cluster = ite.next();
            ListIterator<DoublePoint> ite2 = cluster.getPoints().listIterator();
            double[] array = original.toArray();
            while(ite2.hasNext()){
                DoublePoint point = ite2.next();
                int c = Stat.count(array, (int) point.getPoint()[0], array.length)-1;
                while(c >0){
                    ite2.add(new DoublePoint(point.getPoint()));
                    c--;
                }
            }
        }
        return result;
    }
}
