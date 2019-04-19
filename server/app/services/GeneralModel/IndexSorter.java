package services;
/**
 * Class to sort the indexes of an array based upon their values. Note the array or Collection passed
 * into the constructor is not itself sorted. 
 * doubles, 
 * @author G, Cope
 *
 */

 //copy from: https://www.algosome.com/articles/sort-array-index-java.html.
 //Modified for dealing only with double[], and allows ascendant or descandant sorting.

import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;

public class IndexSorter implements Comparator<Integer> {

    private final double[] values;
    private final Integer[] indexes;

    /**
     * Constructs a new IndexSorter based upon the parameter array.
     * @param d
     */
    public IndexSorter(double[] d) {
        this.values = d;
        indexes = new Integer[this.values.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
    }

    /**
     * @param ascendant
     * if ascendant = True, the sorting is ascendant; otherwise descendant.
     * 
     * Sorts the underlying index array based upon the values provided in the
     * constructor. The underlying value array is not sorted.
     */
    public void sort(boolean ascendant) {
        if(ascendant){
            Arrays.sort(indexes,this);
        }
        else{
            Arrays.sort(indexes, Collections.reverseOrder(this));
        }
    }

    /**
     * Retrieves the indexes of the array. The returned array is sorted if this
     * object has been sorted.
     * 
     * @return The array of indexes.
     */
    public Integer[] getIndexes() {
        return indexes;
    }

    /**
     * Compares the two values at index arg0 and arg0
     * @param arg0 The first index
     * @param arg1 The second index
     * @return The result of calling compareTo on T objects at position arg0 and arg1
     */

    @Override
    public int compare(Integer arg0, Integer arg1) {
        Double d1 = values[arg0];
        Double d2 = values[arg1];
        return d1.compareTo(d2);
    }
}
