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

public class ComputeHabit {

    ArrayList<Long> raw_data;
    Long[] index;
    Long base;
    double[] signal;
    long scale;
    int minimal_period = 1440;

    public ComputeHabit(ArrayList<Long> array, long scale) {
        Collections.sort(array);
        System.out.println(array);
        this.scale = scale;
        raw_data = array;
        base = array.get(0);
        int signal_size = Math.toIntExact((array.get(array.size() - 1) - base) / scale) + 1;
        System.out.println("signal_size" + signal_size);
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
    }

    public double[] getSignal() {
        return this.signal;
    }

    public void getHabit() throws IOException {
        LinkedHashSet<Integer> periods = find_period();
        System.out.println(Arrays.toString(periods.toArray()));
        Iterator<Integer> ite = periods.iterator();
        ArrayList<ArrayList<DoublePoint>> indexes = new ArrayList<ArrayList<DoublePoint>>();
        ArrayList<int[]> counts = new ArrayList<int[]>();
        while (ite.hasNext()) {
            Integer p = ite.next();
            int[] count = new int[signal.length / p];
            indexes.add(format_signal(p, count));
            counts.add(count);
        }
        System.out.println(indexes.get(0).toString());
    }

    private double mean(int[] array) {
        int total = 0;
        for (int i = 0; i < array.length; i++) {
            total += i;
        }
        return (double) total / (double) array.length;
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
        result.data[0] = 0.0;
        IndexSorter is = new IndexSorter(result.data);
        is.sort(false);
        Integer[] ranked_period = is.getIndexes();
        LinkedHashSet<Integer> periods = new LinkedHashSet<Integer>();
        for (int i = 0; i < (int) ranked_period.length * 0.05 + 1; i++) {
            int candidate = Math.round(ranked_period[i] / minimal_period) * minimal_period;
            if (!periods.contains(candidate)) {
                periods.add(candidate);
            }
        }
        return periods;
    }

    // prepare the data for the clustering algorithm.
    // gives the index of one of the signal splitted at period.
    // Ex: this.signal = 1 0 0 1 0 0 0 1 1 0 0 1 and period = 4
    // splitted signal: 1 0 0 1 || 0 0 0 1 || 1 0 0 1 ||
    // out => 0 0 3 3 3

    // count is a zero vectorof size signal.length/period.
    // count will containt the number of non zero element in the split.
    // count => [2,1,2]
    private ArrayList<DoublePoint> format_signal(int period, int[] count) {
        ArrayList<DoublePoint> out = new ArrayList<DoublePoint>();
        for (int i = 0; i < period - 1; i++) {
            for (int j = 0; j < signal.length - period; j += period) {
                if (signal[i + j] == 1.0) {
                    System.out.println("coucou");
                    int[] p = { i };
                    out.add(new DoublePoint(p));
                    count[j / period]++;
                }
            }
        }
        return out;
    }

    // return the cluster partitionning of index for a given period.
    private List<Cluster<DoublePoint>> cluster(ArrayList<DoublePoint> index, int period, int[] count) {
        LinkedList<Cluster<DoublePoint>> out = new LinkedList<Cluster<DoublePoint>>();
        // impose a minimum reliability of 50%
        Integer min_point = signal.length / (2 * period);

        // try to get an idea of a good epsilon value for dbscan.
        ArrayRealVector original = toRealVector(index);
        ArrayRealVector shift = (ArrayRealVector) original.getSubVector(1, original.getDimension() - 2);
        shift = (ArrayRealVector) shift.append(original.getEntry(0) + period);
        ArrayRealVector diff = (ArrayRealVector) shift.subtract(original);
        double nb_cluster = mean(count);
        IndexSorter is = new IndexSorter(diff.toArray());
        is.sort(true);
        Integer[] sorted_id = is.getIndexes();
        int eps_id = (int) ((min_point) * Math.round(nb_cluster));
        double epsilon = diff.getEntry(sorted_id[eps_id]);

        // format input
        DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(epsilon, min_point);
        return dbscan.cluster(index);
    }

}