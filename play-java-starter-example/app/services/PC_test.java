import java.util.*;

public class PC_test{

    //must find a period of 100 and 90
    static long [] data = {90,100,180,200,250,273,300,360,370,400,450,500,540,550,600,630,670,700,800,900,1000};

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
        System.out.println(" 196 " + search(200,0,0,data.length-1));
        System.out.println("88 " + search(670,0,0,data.length-1));
        System.out.println(" 674 " + search(800,0,0,data.length-1));
        System.out.println("95 " + search(90,0,0,data.length-1));
        System.out.println("57" + search(57,0,0,data.length-1));
        LinkedList<Long> habit_periode  = new LinkedList<> () ;
        LinkedList<Long> habit_offset  = new LinkedList<> ();   
        System.out.println("Hello !");  

        
        for(int i=data.length - 1; i > 0; i--){
           for(int j = i-1; j > 0; j--){
               long periode = data[i] - data[j];
               //System.out.println("current period is : " + periode +  " current offset is: " + data[i]);
               int hit = 0;
               int total = 0;
               float hit_rate = 1;
               long cur_date = data[i];
               while((total < 2 || hit_rate > 0.8) && cur_date > data[0]) { 
                   //System.out.println("cur_date: " + cur_date);
                   if(search(cur_date,5,0,data.length-1)){
                       hit ++;
                   }
                   total ++;
                   hit_rate = hit/total;
                   cur_date -= periode;
               }
               if(hit > 3 && hit_rate > 0.8){
                    habit_periode.add(periode);
                    habit_offset.add(data[i]);
                    System.out.println("find period of: " + periode +  " with an offset of " + data[i]);
                    System.out.println("hit : " + hit + " total: " + total);
               }
           }
        }
        System.out.println("Goodbye !");   
    }
}