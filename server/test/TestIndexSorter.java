import services.*;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * test the IndexSorter class
 * @see [[IndexSorter]]
 */
public class TestIndexSorter {

    @Test 
    public void testsort(){
        double[] input = {1,5,2,4,3};
        IndexSorter sorter = new IndexSorter(input);
        sorter.sort(true);      
        Integer[] exp_index = {0,2,4,3,1};
        assertTrue(Arrays.equals(sorter.getIndexes(), exp_index)) ;
        sorter.sort(false);
        List<Integer> exp_indexList = Arrays.asList(exp_index);
        Collections.reverse(exp_indexList);
        assertTrue(Arrays.equals(sorter.getIndexes(), exp_indexList.toArray()));
    }

}