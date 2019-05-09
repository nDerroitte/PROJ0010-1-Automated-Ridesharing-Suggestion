package services;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import org.bson.Document;
import java.util.ArrayList;
import services.EncryptionException;
import services.Decrypt;
import services.Encrypt;
import services.AES;

/**
 * Coordinate corresponds to the class representing the lattitude(x)
 * longitude(y) position into Java elements.
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
     * 
     * @param first  Latitude coordinate rounded
     * @param second Longitude coordinate rounded
     */
    public Coordinate(double first, double second) {

        this.x = first;
        this.y = second;
    }

    /**
     * Getter of the lattitude coordinate rounded
     * 
     * @return the lattitude coordinate rounded
     */
    public double getX() {
        return x;
    }

    /**
     * Getter of the longitude coordinate rounded
     * 
     * @return the longitude coordinate rounded
     */
    public double getY() {
        return y;
    }

    /**
     * Check if another coordinates object is the same as this one with the
     * precision given in Constants.COORDINATE_ERROR_ACCEPTED
     * 
     * @param other : other coordinate object
     * @return true if they are similar. False otherwise
     */
    public boolean isSame(Coordinate other) {
        double x = other.getX();
        double y = other.getY();
        if (Math.abs(x - this.x) < Constants.COORDINATE_ERROR_ACCEPTED
                && Math.abs(y - this.y) < Constants.COORDINATE_ERROR_ACCEPTED)
        {
            return true;
        }
        return false;
    }

    /**
     * Overwrite the toString method
     * 
     * @return The string correspinding to this class
     */
    @Override
    public String toString() {
        NumberFormat formatter = DecimalFormat.getInstance(Locale.ENGLISH);
        return "[" + formatter.format(x) + ";" + formatter.format(y) + "]";
    }

    public Document toDoc() throws EncryptionException {
        Document doc = new Document();
        ArrayList<Byte> x_E = Encrypt.encrypt(Double.toString(x));
        ArrayList<Byte> y_E = Encrypt.encrypt(Double.toString(y));
        doc.put("lat", x_E);
        doc.put("long", y_E);
        return doc;
    }

    public Document toDocNotEncrypted() {

        Document doc = new Document();
        doc.put("lat", x);
        doc.put("long", y);
        return doc;
    }

    public static Coordinate fromDoc(Document doc) throws EncryptionException {
        String lat_D = Decrypt.decrypt((ArrayList<Byte>) doc.get("lat"));
        String lon_D = Decrypt.decrypt((ArrayList<Byte>) doc.get("long"));

        double lat = Double.parseDouble(lat_D);
        double lon = Double.parseDouble(lon_D);
        Coordinate c = new Coordinate(lat, lon);
        return c;
    }

}
