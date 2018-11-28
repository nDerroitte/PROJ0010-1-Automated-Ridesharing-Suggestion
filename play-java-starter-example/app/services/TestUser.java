package services;
import java.util.*;

public class TestUser {

    public static void main(String args[]){
        System.out.println("hello");
        long start = new Date().getTime();
        User test = new User();
        ArrayList<Long> dates = new ArrayList<Long>();
        Random noise = new Random();
        int nb_noise = 500;
        long bound = nb_noise*100;
        System.out.println(bound/11);
        System.out.println(bound/7);
        for(long i=0; i < bound;i+=bound/11){
            dates.add(i);
        }
        for(long i=0; i < bound; i+= bound/7){
            dates.add(i);
        }
        Iterator<Long> ite = noise.longs(nb_noise,0,bound).iterator();
        while(ite.hasNext()){
            dates.add(ite.next());
        }

        ArrayList<Habits> habit = test.getHabits(dates,0);
        Iterator<Habits> ite2 = habit.iterator();
        while(ite2.hasNext()){
            ite2.next().print();
        }
        //System.out.println("goodbye");
        //for (int i=0; i < dates.size()-1;i++){
        //    System.out.print(dates.get(i) + " ");
        //}
        System.out.println("elapsed time in millisecond: " + (new Date().getTime() - start));
    }
}