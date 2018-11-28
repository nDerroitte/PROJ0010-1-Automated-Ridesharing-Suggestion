package services;
import java.util.Calendar;

public class Point
{
    private final Calendar time;
    private final Coordinate position;

    public Point(Calendar time, Coordinate position)
    {
        this.time = time;
        this.position = position;
    }

    public Calendar getTime() {
        return time;
    }

    public Coordinate getPosition() {
        return position;
    }

    public long getTimeInMs() {return time.getTimeInMillis();}
}

