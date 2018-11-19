import java.util.Date;

public class Habits
{
    private int period;
    private String depart_pos;
    private String arrive_pos;
    private int offset;

    public Habits(int period, String depart_pos, String arrive_pos, int offset)
    {
        this.period = period;
        this.depart_pos = depart_pos;
        this.arrive_pos = arrive_pos;
        this.offset = offset;
    }
    public String getWeekDays()
    {
        //TODO
        return "Sunday";
    }

}
