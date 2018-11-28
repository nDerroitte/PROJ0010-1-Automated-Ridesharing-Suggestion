import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public Document toDoc()
    {
        Document doc = new Document();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String string_time = sdf.format(this.time.getTime());

        ArrayList<Long> coordinates = new ArrayList<>();
        coordinates.add(position.getX());
        coordinates.add(position.getY());

        doc.put("time",string_time);
        doc.put("position",coordinates);
        return doc;
    }
    public static Point FromDoc(Document) throws ParseException
    {
        String string_time = doc.get("time");
        Calendar time = Constants.stringToCalendar(string_time);

        ArrayList<Long> coordinates = doc.get("position");
        Coordinate position = new Coordinate(coordinates.get(0),coordinates.get(1));

        return new Point(time,position);

    }
}

