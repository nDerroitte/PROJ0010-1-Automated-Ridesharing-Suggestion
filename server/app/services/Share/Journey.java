package services;

import java.util.ArrayList;
import java.util.Calendar;
import org.bson.Document;
import java.text.ParseException;
import services.EncryptionException;
import services.AES;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
/**
 * Journey class representing a journey of the habit (from a time and path perspective)
 */
public class Journey
{
    /**
     * ArrayList of meeting_points containing the Date and Coordinate of each point
     */
    private final ArrayList<Point> meeting_points;

    /**
     * Constructor
     * @param meeting_point the initial arrayist of meeting_points.
     */
    public Journey(ArrayList<Point> meeting_point)
    {
        this.meeting_points = meeting_point;
    }

    /**
     * Get the path of the journey
     * @return the Arralylist of Coordinate corresponding to the path of the habit
     */
    public ArrayList<Coordinate> getPath()
    {
        ArrayList<Coordinate> path = new ArrayList<>();
        for(int i =0 ; i < this.meeting_points.size();i++)
        {
            path.add(this.meeting_points.get(i).getPosition());
        }
        return path;
    }

    /**
     * Get the time of the first point
     * @return
     */
    public Calendar getFirstPointTime()
    {
        return meeting_points.get(0).getTime();
    }

    /**
     * Get the time of the last point
     * @return
     */
    public Calendar getLastPointTime()
    {
        int max_size = meeting_points.size() - 1;
        return meeting_points.get(max_size).getTime();
    }

    /**
     * Allow to transform this object to a Document. Used to store the Jounrey in the database
     * @return a Document object corresponding to this class
     */
    public  Document toDoc() throws EncryptionException, UnsupportedEncodingException
    {
        Document doc = new Document();
        ArrayList<Document> doc_meeting_point = new ArrayList<>();
        for(int i =0; i < this.meeting_points.size();i++)
            doc_meeting_point.add(meeting_points.get(i).toDoc());
        doc.put("meeting_point",doc_meeting_point);
        return doc;

    }

    /**
     * Create a Journey from a Document object reprensenting it. Used to read a journey from
     * the database
     * @param doc the Document object to read from
     * @return  a Jounrey object corresponding to the Document.
     */
    public static Journey fromDoc(Document doc) throws ParseException, EncryptionException, UnsupportedEncodingException, IOException
    {
        ArrayList<Document> doc_meeting_point = (ArrayList<Document>)doc.get("meeting_point");
        ArrayList<Point> meeting_point = new ArrayList<>();
        for(Document doc_point : doc_meeting_point)
            meeting_point.add(Point.FromDoc(doc_point));
        return new Journey(meeting_point);
    }

    /**
     * Override the equals method to check if two journeys are similar
     * @return True if they are similar. False otherwise
     */
    @Override
    public boolean equals(Object o){
        if(o instanceof Journey){
            Journey j = (Journey) o;
            return meeting_points.equals(j.meeting_points);
        }
        return false;
    }

    @Override
    public String toString(){
        String out = "";
        for(Point p : meeting_points){
           out += p.toString() + "\n";
        }
        out += "==============================================";
        return out;
    }
}
