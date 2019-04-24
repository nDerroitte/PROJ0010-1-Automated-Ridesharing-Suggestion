package services;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import org.bson.Document;

/**
 * Coordinate corresponds to the class representing the lattitude(x) longitude(y) 
 * position into Java elements.
 */
public final class Coordinate {
    /**
     * Latitude coordinate rounded
     */
    private final double x;
    /**
     * Longitude coordinate rounded
     */
    private final double y;

    /**
     * Constructor
     * @param first Latitude coordinate rounded
     * @param second Longitude coordinate rounded
     */
    public Coordinate(double first, double second) {
        this.x = first;
        this.y = second;
    }

    /**
     * Getter of the lattitude coordinate rounded
     * @return the lattitude coordinate rounded
     */
    public double getX() {
        return x;
    }

    /**
     * Getter of the longitude coordinate rounded
     * @return the longitude coordinate rounded
     */
    public double getY() {
        return y;
    }

    /**
     * Check if another coordinates object is the same as this one with
     * the precision given in Constants.COORDINATE_ERROR_ACCEPTED
     * @param other : other coordinate object
     * @return true if they are similar. False otherwise
     */
    public boolean isSame(Coordinate other) {
        double x = other.getX();
        double y = other.getY();
        if (x - Constants.COORDINATE_ERROR_ACCEPTED <= this.x && x + Constants.COORDINATE_ERROR_ACCEPTED >= this.x
                && y - Constants.COORDINATE_ERROR_ACCEPTED <= this.y
                && y + Constants.COORDINATE_ERROR_ACCEPTED >= this.y)
                {
                    return true;
                }
        return false;
    }

    /**
     * Overwrite the toString method
     * @return The string correspinding to this class
     */
    @Override
    public String toString() {
        NumberFormat formatter = DecimalFormat.getInstance(Locale.ENGLISH);
        return "[" + formatter.format(x) + ";" + formatter.format(y) + "]";
    }

    public Document toDoc(){
        //Encrypt
        Document doc = new Document();
        doc.put("lat",x);
        doc.put("long",y);
        return doc;
    }

    public static Coordinate fromDoc(Document doc){
        //Decrypt
        double lat = (double) doc.get("lat");
        double lon = (double) doc.get("long");
        Coordinate c = new Coordinate(lat,lon);
        return c;
    }

}
