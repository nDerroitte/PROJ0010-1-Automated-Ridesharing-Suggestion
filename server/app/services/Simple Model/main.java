import java.util.ArrayList;
import java.util.Calendar;

public class main {
    public static void main(String [] args)
    {
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(System.currentTimeMillis());
        System.out.println(cal.getTime());

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2019, 1,18,12,29,50);

        Point p = new Point(cal, new Coordinate(55, 5));
        ArrayList<Point> m_p = new ArrayList<>();
        m_p.add(p);
        Journey j = new Journey(m_p);

        Point p2 = new Point(cal2, new Coordinate(55, 5));
        ArrayList<Point> m_p2 = new ArrayList<>();
        m_p2.add(p2);
        Journey j2 = new Journey(m_p2);

        ArrayList<Journey> u_j = new ArrayList<>();
        u_j.add(j);
        u_j.add(j2);
        User u = new User("0", u_j);
        u.createHabits();

        u.printHabits();

    }
}
