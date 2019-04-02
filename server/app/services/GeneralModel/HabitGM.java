package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class HabitGM extends Habit
{
    public double spread = 0;

    @Override
    public String toString(){
        return super.toString() + " spread: " + spread;
    }

}