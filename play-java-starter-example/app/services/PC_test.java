import java.util.*;
import PC_Habit;
public class PC_test{

    //must find a period of 100 and 90
    static long [] data = {9,10,18,20,27,30,36,40,45,50,54,60,63,72,80,81,90,99,100,110,120};

    static boolean search(long x,long tolerance){
        int down = 0;
        int up = data.length-1;

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

    static boolean isRedundant(HashSet<PC_Habit> habits,PC_Habit candidate){
        Iterator<PC_Habit> ite = habits.iterator();
        while(ite.hasNext()){
            if(ite.next().equivalent(candidate)){
                return true;
            }
        }
        return false;
    }

    public static void main(String args[]){

        HashSet<PC_Habit> habits = new HashSet<>();
        System.out.println(new PC_Habit(50,20).equivalent(new PC_Habit(50,70)));
        System.out.println("Hello !");  
        for(int i=data.length - 1; i > 0; i--){
           for(int j = i-1; j > 0; j--){
               long periode = data[i] - data[j];
               //System.out.println("current period is : " + periode +  " current offset is: " + data[i]);
               float hit = 0;
               float total = 0;
               float hit_rate = 1;
               long cur_date = data[i] - periode;
                
               if(isRedundant(habits,new PC_Habit(periode,cur_date))){
                   System.out.println("HABIT ALREADY FIND !");
                   continue;
               }

               while((total < 2 || hit_rate > 0.8) && cur_date > data[0]) { 
                   //System.out.println("cur_date: " + cur_date);
                   if(search(cur_date,0)){
                       hit ++;
                   }
                   total ++;
                   hit_rate = hit/total;
                   cur_date -= periode;
               }
               cur_date = data[i] + periode;
               while((total < 2 || hit_rate > 0.8) && cur_date < data[data.length-1]) { 
                //System.out.println("cur_date: " + cur_date);
                if(search(cur_date,0)){
                    hit ++;
                }
                total ++;
                hit_rate = hit/total;
                cur_date += periode;
                }
               if(hit > 3 && hit_rate > 0.8){
                    habits.add(new PC_Habit(periode,cur_date));
                    System.out.println("find period of: " + periode +  " with an offset of " + data[i]%periode + " hit rate:" + hit_rate);
                    System.out.println("hit : " + hit + " total: " + total);
               }
           }
        }
        System.out.println("Goodbye !");   
    }
}