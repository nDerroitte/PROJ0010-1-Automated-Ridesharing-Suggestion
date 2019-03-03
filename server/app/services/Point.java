package services;
import java.util.Calendar;
import java.util.ArrayList;
import org.bson.Document;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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

    static public Point FromDoc(Document doc) throws ParseException
    {
        String string_time = (String)doc.get("time");
        Calendar time = Constants.stringToCalendar(string_time);

        ArrayList<Long> coordinates = (ArrayList<Long>)doc.get("position");
        Coordinate position = new Coordinate(coordinates.get(0),coordinates.get(1));

        return new Point(time,position);

    }
}

