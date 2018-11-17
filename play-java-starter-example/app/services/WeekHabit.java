import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeekHabit
{
    private ArrayList<DayHabit> day_habits_list = new ArrayList<>();
    public WeekHabit ()
    {
        for(int i =1; i<8;i++)
            this.day_habits_list.add(new DayHabit(i));
    }


    public void completeHabit(ArrayList<Point>  points)
    {
        //1 . Détecter le jour de la semaine
        Date currentDate =  points.get(0).getTime();

        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        // 1 = SUNDAY, ... , 7= SATURDAY

        //2. appeler la fonction correspondant .
        day_habits_list.get(dayOfWeek).completeHabit(points);

    }
}
