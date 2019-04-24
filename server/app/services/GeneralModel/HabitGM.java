package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.Date;

/**
 * Add time characteritic to the base class habit.
 * 
 * A user habit is assumed to be periodic in time.
 * 
 */
public class HabitGM extends Habit
{
    /**
     * Spread of the habit. Model the fact that an habit is not exactly periodic.
     */
    public double spread = 0;
    /**
     * Number of point that are supposed to belong to the habit.
     */
    public int point_in_habit = 0;
    /**
     * Last date for which a data is available for this habit.
     */
    public long end;

}