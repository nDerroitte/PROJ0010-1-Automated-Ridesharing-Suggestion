package services;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import ca.pfv.spmf.algorithms.timeseries.autocorrelation.AlgoLagAutoCorrelation;
import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import java.util.Iterator;

public class ComputeHabit{

    ArrayList<Long> raw_data;
    Long[] index;
    Long base;
    double[] signal;
    long scale;

    ComputeHabit(ArrayList<Long> array,long scale){ 
        Collections.sort(array);
        this.scale = scale;
        raw_data = array;
        base = array.get(0);
        int signal_size = Math.toIntExact((array.get(array.size()-1) - base) / scale) + 1;
        index = new Long[array.size()];
        Iterator<Long> ite = array.iterator(); 
        signal = new double[signal_size];
        int i = 0;
        int j = 0;

        //format data to a signal for the autocorrelation.       
        while(ite.hasNext()){
            index[i] = (ite.next() - base) / scale;
            System.out.println("i= " + index[i]);
            System.out.println("j= " + j);
            while(j < index[i]){
                signal[j] = 0;
                j++;
            }
            //skip repeated value in input signal => should never happend.
            if(i > 0 && index[i] == index[i-1] ){
                i++;
                continue;
            }
            signal[j] = 1;
            j++;
            i++;
        }
        System.out.println(Arrays.toString(signal));
    }
    
    public double[] getSignal(){
        return this.signal;
    }

    void getHabit() throws IOException{
        AlgoLagAutoCorrelation autocorr = new AlgoLagAutoCorrelation();
        TimeSeries result = autocorr.runAlgorithm(new TimeSeries(signal,"raw data"),signal.length/2);
        System.out.println(result.toString());
        Double[] results = new Double[result.data.length];
        for(int i=0; i<result.data.length;i++){
            results [i] = Double.valueOf(result.data[i]);
        }        
        IndexSorter<Double> is = new IndexSorter<Double>(results);
        is.sort();
        Integer[] ranked_period = is.getIndexes();
        System.out.println(Arrays.toString(ranked_period));

    }


}