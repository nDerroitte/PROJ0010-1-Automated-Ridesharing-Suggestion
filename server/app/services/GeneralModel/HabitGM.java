package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.Date;

public class HabitGM extends Habit
{
    public double spread = 0;
    public int point_in_habit = 0;
    public long end;

    @Override
    public String toString(){
        return super.toString() + "\n spread: " + spread + "\n end: " 
            + new Date(end).toString() + "\n nb point: " + point_in_habit 
            + "\n nb_realisation: " + ((end-offset) /(period * 1440 * 60000));
    }

}