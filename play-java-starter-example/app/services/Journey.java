import org.bson.Document;
import java.util.ArrayList;
import java.util.Calendar;

public class Journey
{
    private final ArrayList<Point> meeting_point;
    private Calendar start_time;
    private Calendar arrive_time;
    private Coordinate start_coordinates;
    private Coordinate arrive_coordinates;
    public Journey(ArrayList<Point> meeting_point)
    {
        this.meeting_point = meeting_point;
        this.start_time = meeting_point.get(0).getTime();
        this.start_coordinates = meeting_point.get(0).getPosition();
        int size_array = meeting_point.size();
        this.arrive_time = meeting_point.get(size_array-1).getTime();
        this.arrive_coordinates = meeting_point.get(size_array-1).getPosition();

    }
    public boolean sameJourney(Journey other)
    {
        for(int i = 0; i<this.meeting_point.size();i++)
        {
            if(other.meeting_point.get(i).getPosition().isSame(this.meeting_point.get(i).getPosition()))
                continue;
            else
                return false;
        }
        return true;
    }
    public long getFirstPointTime()
    {
        return meeting_point.get(0).getTimeInMs();
    }

    public  Document toDoc()
    {
        Document doc = new Document();
        ArrayList<Document> doc_meeting_point = new ArrayList<>();
        for(int i =0; i < this.meeting_point.size();i++)
            doc_meeting_point.add(meeting_point.get(i).toDoc());
        doc.put("meeting_point",doc_meeting_point);
        return doc;

    }
    public static Journey fromDoc(Document doc)
    {
        ArrayList<Point> meeting_point = doc.get("meeting_point");
        return new Journey(meeting_point);
    }
    public Point getIthMeetingPoint(int index)
    {
        return this.meeting_point.get(index);
    }
}
