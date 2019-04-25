package services;
import java.util.Calendar;
import java.util.ArrayList;
import org.bson.Document;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import services.EncryptionException;
import services.Decrypt;
import services.Encrypt;
import services.AES;

/**
 * Class correspinding to a Point of a journey in term of time and Coordinate
 */
public class Point
{
    /**
     * Time perpective of the point
     */
    private final Calendar time;
    /**
     * Coordinate perpective of the point
     */
    private final Coordinate position;

    /**
     * Coonstructor
     * @param time the Calendar corresponding to the point
     * @param position the Coordinate object corresponding to the point
     */
    public Point(Calendar time, Coordinate position)
    {
        this.time = time;
        this.position = position;
    }

    /**
     * Getter of the time (Calender)
     * @return the Calender corresponding to the point
     */
    public Calendar getTime() {
        return (Calendar) time.clone();
    }

    /**
     * Getter of the Coordinate
     * @return the Coordinate corresponding to the point
     */
    public Coordinate getPosition() {
        return position;
    }

     /**
     * Allow to transform this object to a Document. Used to store the point in the database
     * @return a Document object corresponding to this class
     */
    public Document toDoc() throws EncryptionException
    {
        //Encrypt
        Document doc = new Document();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String string_time = sdf.format(this.time.getTime());

        /*ArrayList<Double> coordinates = new ArrayList<>();

        coordinates.add(position.getX());
        coordinates.add(position.getY());*/

        ArrayList<ArrayList<Byte> > coordinates = new ArrayList<>();
        ArrayList<Byte> x_E = Encrypt.encrypt(Double.toString(position.getX()));
        ArrayList<Byte> y_E = Encrypt.encrypt(Double.toString(position.getY()));
        coordinates.add(x_E);
        coordinates.add(y_E);

        ArrayList<Byte> string_time_E = Encrypt.encrypt(string_time);

        doc.put("time",string_time_E);
        doc.put("position",coordinates);
        return doc;
    }

    /**
     * Create a Point from a Document object reprensenting it. Used to read a journey from
     * the database
     * @param doc: the Document object to read from
     * @return  a Point object corresponding to the Document.
     */
    static public Point FromDoc(Document doc) throws ParseException, EncryptionException
    {
        //Decrypt
        String string_time_D = Decrypt.decrypt((ArrayList<Byte>)doc.get("time"));
        //String string_time = (String)doc.get("time");
        Calendar time = Constants.stringToCalendar(string_time_D);

        ArrayList<ArrayList<Byte> > coordinates = (ArrayList<ArrayList<Byte> >) doc.get("position");

        //ArrayList<Double> coordinates = (ArrayList<Double>)doc.get("position");
        Coordinate position = new Coordinate(Double.parseDouble(Decrypt.decrypt(coordinates.get(0))),Double.parseDouble(Decrypt.decrypt(coordinates.get(1))));

        return new Point(time,position);

    }

    /**
     * Override the equals method to check if two journeys re similar
     * @return  true if they are similar. False otherwise
     */
    @Override
    public boolean equals(Object o){
        if(o instanceof Point){
            Point p = (Point) o;
            if(time.getTime().after(p.time.getTime())){
                return position.isSame(p.position) && (time.getTime().getTime() - p.time.getTime().getTime() < 1000);
            }
            return position.isSame(p.position) && (p.time.getTime().getTime() - time.getTime().getTime() < 1000);
        }
        return false;
    }

    /**
     * Override the toString method
     * @return  the string correspinding to this class
     */
    @Override
    public String toString(){
        return "time: " + time.toString() + " position: " + position.toString();
    }
}

