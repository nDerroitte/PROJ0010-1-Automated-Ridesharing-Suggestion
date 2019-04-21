package services;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.Cluster;

/**
 * Guess characteristics of a periodic stochastic stationnary process given the
 * observation of a noisy signal.
 *
 * The binary signal is represented by a list of non-zero index. The algorithm
 * only consider period that are multiple of minimal_period. The time unit is the minute.
 * 
 * The stochastic process has the following characterisitc:
 * <ul>
 * <li>Period</li>
 * <li>Reliability</li>
 * <li>Spread</li>
 * <li>Offset</li>
 * </ul>
 * 
 * The signal has a probabilty $reliability to have a one in the range $[offset
 * + n_i * period - spread , offset + n_i * period + spread], where n_i are all positive integer.
 * Otherwise, signal equal 0 but can contains some noisy 1.
 * 
 */
public class ComputeHabit {

    /**
     * scale millisecond to minut.
     */
    private long scale = 60000;

    /**
     * internal representation (R) of the input data (I). R = I/scale - I(0)
     */
    private Long[] index;

    /**
     * base = Lowest index in the input.
     */
    private Long base;

    /**
     * end = highest index in the input.
     */
    private Long end;

    /**
     * Explicit binary signal of the internal representation of the input.
     */
    private double[] signal;

    /**
     * The algorithm only considered period that are multiple of minimal_period.
     */
    private int minimal_period = 1440; // minimal period in minute

    /**
     * 
     * @param array      Input binary signal, represented by a list of non-zero
     *                   index.
     * @param min_period The algorithm only considered period that are multiple of
     *                   minimal_period.
     */
    public ComputeHabit(ArrayList<Long> array, int min_period) {

        // init some internal variable
        Collections.sort(array);
        int signal_size = 0;
        if (array.size() > 0) {
            base = array.get(0);
            end = array.get(array.size() - 1);
            signal_size = Math.toIntExact((array.get(array.size() - 1) - base) / scale) + 1;
        }
        this.minimal_period = min_period;
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

    /**
     * Return {@link signal signal}
     * @return The signal representation of the data
     */
    public double[] getSignal() {
        return this.signal.clone();
    }

    /**
     * Return the habits.
     * @return The habits computed from the date passed to the {@link ComputeHabit constructor}
     */
    public LinkedList<Habit> getHabit() {

        // inititalization
        List<List<Cluster<DoublePoint>>> partitions = new ArrayList<List<Cluster<DoublePoint>>>();
        double best_score = Double.NEGATIVE_INFINITY;
        int best_score_index = -1;
        int best_period = 0;
        LinkedList<Habit> habits = new LinkedList<Habit>();
        List<Cluster<DoublePoint>> best_partition = new LinkedList<>();

        // get the possible period.
        HashSet<Integer> periods = find_period();
        Iterator<Integer> ite = periods.iterator();

        // iterate over the finded period.
        for (int i = 0; ite.hasNext(); i++) {
            Integer period = ite.next();

            // format input for clustering
            ArrayList<DoublePoint> index = format_signal(period);

            // perform the clustering
            List<Cluster<DoublePoint>> partition = cluster(index, period);

            // evaluate the partition quality
            double score = partitionScore(partition, period, index.size());

            if (score > best_score) {

                // select the best partition.
                best_score = score;
                best_score_index = i;
                best_period = period;
                best_partition = partition;
            }
        }

        // if no habit detected, return an empty list.
        if (best_period == 0) {
            return habits;
        }

        // get the best partition
        Iterator<Cluster<DoublePoint>> part_ite = best_partition.iterator();

        // write the finded habits
        while (part_ite.hasNext()) {
            Cluster<DoublePoint> cluster = part_ite.next();
            HabitGM h = new HabitGM();
            h.period = (long) best_period / 1440;
            double[] mean_var = Stat.clusterStat(cluster, best_period);
            h.offset = base + Math.round(mean_var[0] * scale);
            h.reliability = Math.min(1, (double) cluster.getPoints().size() / (signal.length / best_period));
            h.spread = mean_var[1];
            h.point_in_habit = cluster.getPoints().size();
            h.end = end;
            habits.add(h);
        }
        return habits;
    }

    /**
     * Score a partition generated by {@link cluster cluster}
     * 
     * @param part The partition to score
     * @param period The parameter of the circular space of the partition.
     * @param nb_point The total number of point from which the partition is computed.
     * @return A score measuring the quality of a clustering.
     */
    private double partitionScore(List<Cluster<DoublePoint>> part, int period, int nb_point) {
        PartitionStat stat = new PartitionStat(part, signal.length / period, period, nb_point);
        stat.compute();
        return Stat.mean(stat.getReliability()) / ((stat.getNoise() + 0.0000001) * Stat.mean(stat.getStd()));
    }

    // convert an ArrayList of DoublePoint to an ArrayRealVector
    private ArrayRealVector toRealVector(ArrayList<DoublePoint> d) {
        double[] array = new double[d.size()];
        for (int i = 0; i < d.size(); i++) {
            array[i] = d.get(i).getPoint()[0];
        }
        return new ArrayRealVector(array);
    }

    /**
     * Find the top 0.001 most probable period.
     * @return An hashset containg all period in the top 0.001
     * All period are imposed to be a multiple of {@link minimal_period minimal_period}
     */
    private HashSet<Integer> find_period() {
        // perform the autocorrelation
        Autocorr autocorr = new Autocorr();
        if (signal == null) {
            return new HashSet<Integer>();
        }

        double[] result = autocorr.compute(signal, signal.length / 2);
        if (result.length == 0) {
            return new HashSet<Integer>();
        }
        // remove useless result
        result[0] = 0.0;

        // sort period from the most probable to the less one.
        IndexSorter is = new IndexSorter(result);
        is.sort(false);
        Integer[] ranked_period = is.getIndexes();
        HashSet<Integer> periods = new HashSet<>();

        // only consider the top 0.001 most probable period and forget the other.
        for (int i = 0; i < (int) ranked_period.length * 0.001 + 1; i++) {

            // round the period to the closest multiple of a day.
            int candidate = Math.round(ranked_period[i] / minimal_period) * minimal_period;
            if (candidate != 0 && !periods.contains(candidate)) {
                periods.add(candidate);
            }
        }
        return periods;
    }

    /**
     * Transform the data for the clustering algorithm.
     * 
     * @param period Parameter of the circular space.
     * @return The index of the input data in the circular space.
     * The circular space domain are the integer in [0,period], each integer r
     * are map to r % period.
     */
    private ArrayList<DoublePoint> format_signal(int period) {
        ArrayList<DoublePoint> out = new ArrayList<DoublePoint>();
        for (int i = 0; i < index.length; i++) {
            double[] p = { (double) index[i] % period };
            out.add(new DoublePoint(p));
        }
        return out;
    }

    /**
     * Perform the clustering.
     * 
     * @param index Input data
     * @param period Circular space parameter.
     * @return A partition of the point. Each cluster contains points that belong to the same habits.
     */
    private List<Cluster<DoublePoint>> cluster(ArrayList<DoublePoint> index, int period) {
        LinkedList<Cluster<DoublePoint>> out = new LinkedList<Cluster<DoublePoint>>();
        // impose a minimum reliability of 50%
        Integer min_point = Math.max(signal.length / (2 * period), 2);

        // try to get an idea of a good epsilon value for dbscan.
        ArrayRealVector original = toRealVector(index);
        ArrayRealVector shift = (ArrayRealVector) original.getSubVector(1, original.getDimension() - 1);
        shift = (ArrayRealVector) shift.append(original.getEntry(0) + period);
        ArrayRealVector diff = (ArrayRealVector) shift.subtract(original);
        double epsilon = Stat.mean(diff.toArray()) * 2;
        CircularDist measure = new CircularDist(period);

        // perform clustering
        DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(epsilon, min_point, measure);
        List<Cluster<DoublePoint>> result = dbscan.cluster(index);
        ListIterator<Cluster<DoublePoint>> ite = result.listIterator();

        // correct clustering output, apache dbscan cannot write several time the same
        // point in the same cluster.
        // if it has to cluster [10 10 11 10 2 3 1 2 ] => a possible output will be [[10
        // 11] [1 2 3]], but we want [[10 10 10 11][1 2 2 3]]
        while (ite.hasNext()) {
            Cluster<DoublePoint> cluster = ite.next();
            ListIterator<DoublePoint> ite2 = cluster.getPoints().listIterator();
            double[] array = original.toArray();
            while (ite2.hasNext()) {
                DoublePoint point = ite2.next();
                int c = Stat.count(array, (int) point.getPoint()[0], array.length) - 1;
                while (c > 0) {
                    ite2.add(new DoublePoint(point.getPoint()));
                    c--;
                }
            }
        }
        return result;
    }
}
