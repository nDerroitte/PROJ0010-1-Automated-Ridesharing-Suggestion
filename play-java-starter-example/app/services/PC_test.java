import java.util.*;

public class PC_test{

    //must find a period of 100 and 90
    static long [] data = {9,18,27,36,45,54,63,72,81,90,99,100,110,120};

    static boolean search(long x,long tolerance,int down,int up){
        if(x < data[down]-tolerance || x > data[up] + tolerance){
            return false;
        }
        if(x >= data[down] - tolerance && x <= data[down] + tolerance){
            return true;
        }
        if(x >= data[up] - tolerance && x <= data[up] + tolerance){
            return true;
        }            
        while (down < up-1 ){
            //System.out.println("down: " + down + " up " + up);
            int pivot = down + (up-down)/2;
            if ( x >= data[pivot] - tolerance && x <= data[pivot] + tolerance ){
                return true;
            }
            if(x > data[pivot]){
                down = pivot;
            }
            else{
                up = pivot;
            }
        }
        return false;
    }
    
    public static void main(String args[]){
        LinkedList<Long> habit_periode  = new LinkedList<> () ;
        LinkedList<Long> habit_offset  = new LinkedList<> ();   
        System.out.println("Hello !");  
        for(int i=data.length - 1; i > 0; i--){
           for(int j = i-1; j > 0; j--){
               long periode = data[i] - data[j];
               //System.out.println("current period is : " + periode +  " current offset is: " + data[i]);
               float hit = 0;
               float total = 0;
               float hit_rate = 1;
               long cur_date = data[i] - periode;
               while((total < 2 || hit_rate > 0.8) && cur_date > data[0]) { 
                   //System.out.println("cur_date: " + cur_date);
                   if(search(cur_date,0,0,data.length-1)){
                       hit ++;
                   }
                   total ++;
                   hit_rate = hit/total;
                   cur_date -= periode;
               }
               cur_date = data[i] + periode;
               while((total < 2 || hit_rate > 0.8) && cur_date < data[data.length-1]) { 
                //System.out.println("cur_date: " + cur_date);
                if(search(cur_date,0,0,data.length-1)){
                    hit ++;
                }
                total ++;
                hit_rate = hit/total;
                cur_date += periode;
                }
               if(hit > 3 && hit_rate > 0.8){
                    habit_periode.add(periode);
                    habit_offset.add(data[i]);
                    System.out.println("find period of: " + periode +  " with an offset of " + data[i] + " hit rate:" + hit_rate);
                    System.out.println("hit : " + hit + " total: " + total);
               }
           }
        }
        System.out.println("Goodbye !");   
    }
}