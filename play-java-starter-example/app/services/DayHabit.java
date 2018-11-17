import java.util.ArrayList;
import java.util.List;


public class DayHabit
{
    private boolean isStable;
    private String day;

    //Getting to work trip
    private String gtw_depart_position;
    private String gtw_arrive_position;
    private List<String> gtw_usual_journey_position;
    private int gtw_depart_offset;

    //Coming back home trip
    private String return_depart_position; // ==  gtw_arrive_position
    private String return_arrive_position;
    private List<String> return_usual_journey_position;
    private int return_depart_offset;


    public DayHabit(int day)
    {
        this.day = Constants.DAY_LIST.get(day);
        this.isStable = false;

    }

    public void completeHabit(ArrayList<Point> points)
    {
        //TODO with periodicity check etc.
    }

}
