package services;
import java.util.ArrayList;
import java.util.Calendar;

public class Journey
{
    private final ArrayList<Point> meeting_points;

    public Journey(ArrayList<Point> meeting_point)
    {
        this.meeting_points = meeting_point;
    }

    public ArrayList<Coordinate> getPath()
    {
        ArrayList<Coordinate> path = new ArrayList<>();
        for(int i =0 ; i < this.meeting_points.size();i++)
        {
            path.add(this.meeting_points.get(i).getPosition());
        }
        return path;
    }


    public Calendar getFirstPointTime()
    {
        return meeting_points.get(0).getTime();
    }


    /*public  Document toDoc()
    {
        Document doc = new Document();
        ArrayList<Document> doc_meeting_point = new ArrayList<>();
        for(int i =0; i < this.meeting_point.size();i++)
            doc_meeting_point.add(meeting_point.get(i).toDoc());
        doc.put("meeting_point",doc_meeting_point);
        return doc;

    }
    public static Journey fromDoc(Document doc)throws ParseException
    {
        ArrayList<Document> doc_meeting_point = (ArrayList<Document>)doc.get("meeting_point");
        ArrayList<Point> meeting_point = new ArrayList<>();
        for(Document doc_point : doc_meeting_point)
            meeting_point.add(Point.FromDoc(doc_point));

        return new Journey(meeting_point);
    }*/
}
