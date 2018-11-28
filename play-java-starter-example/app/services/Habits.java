public class Habits
{
    private int period;
    private Coordinate depart_pos;
    private Coordinate arrive_pos;
    private int offset;
    private int journey_ID;
    private long last_journey;

    public Habits(int period, Coordinate depart_pos, Coordinate arrive_pos, int offset)
    {
        this.period = period;
        this.depart_pos = depart_pos;
        this.arrive_pos = arrive_pos;
        this.offset = offset;
    }


}
