import java.util.Date;

public class Point
{
    private final Date time;
    private final String position;

    public Point(Date time, String position)
    {
        this.time = time;
        this.position = position;
    }

    public Date getTime()
    {
        return time;
    }

    public String getPosition()
    {
        return position;
    }
}
