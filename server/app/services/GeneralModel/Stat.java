package services;

import java.util.Iterator;
import java.util.List;
import java.util.Arrays;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

// Static class for computing some statistic
public class Stat 
{ 
	public static int count(double arr[], int x, int n) 
	{ 
    Arrays.sort(arr);
    
	// index of first occurrence of x in arr[0..n-1]	 
	int i; 
		
	// index of last occurrence of x in arr[0..n-1] 
	int j; 
		
	/* get the index of first occurrence of x */
	i = first(arr, 0, n-1, x, n); 
	
	/* If x doesn't exist in arr[] then return -1 */
	if(i == -1) 
		return i; 
		
	/* Else get the index of last occurrence of x. 
		Note that we are only looking in the 
		subarray after first occurrence */
	j = last(arr, i, n-1, x, n);	 
		
	/* return count */
	return j-i+1; 
	} 
	
	/* if x is present in arr[] then returns the 
	index of FIRST occurrence of x in arr[0..n-1], 
	otherwise returns -1 */
	static public int first(double arr[], int low, int high, int x, int n) 
	{ 
	if(high >= low) 
	{ 
		/*low + (high - low)/2;*/
		int mid = (low + high)/2; 
		if( ( mid == 0 || x > arr[mid-1]) && arr[mid] == x) 
		return mid; 
		else if(x > arr[mid]) 
		return first(arr, (mid + 1), high, x, n); 
		else
		return first(arr, low, (mid -1), x, n); 
	} 
	return -1; 
	} 
	
	/* if x is present in arr[] then returns the 
	index of LAST occurrence of x in arr[0..n-1], 
	otherwise returns -1 */
	public static int last(double arr[], int low, int high, int x, int n) 
	{ 
	if(high >= low) 
	{ 
		/*low + (high - low)/2;*/	
		int mid = (low + high)/2; 
		if( ( mid == n-1 || x < arr[mid+1]) && arr[mid] == x ) 
		return mid; 
		else if(x < arr[mid]) 
		return last(arr, low, (mid -1), x, n); 
		else
		return last(arr, (mid + 1), high, x, n);	 
	} 
	return -1; 
    } 

    //return (mean,std) of array.
    public static double[] meanStd(int[] array){
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
    //return mean, var,reliability of cluster.
    public static double[] clusterStat(Cluster<DoublePoint> c,int period){
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
    public static double mean(double[] array){
        double mean = 0;
        for(int i=0; i < array.length;i++){
            mean+= array[i];
        }
        return mean/array.length;
    }
} 
