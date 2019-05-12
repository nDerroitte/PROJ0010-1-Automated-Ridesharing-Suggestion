package controllers;

import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;

import services.Point;
import java.io.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import com.fasterxml.jackson.databind.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import javax.json.*;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import java.util.Optional;
import java.util.Calendar;
import services.*;
import java.lang.Math;

@Singleton
public class IntegrateGeolife extends Controller {

    private final MongoDatabase database;
    private final HabitGenerator hb;

    @Inject
    public IntegrateGeolife(MongoInterface db, HabitGenerator habit_generator) {
        this.database = db.get_database();
        this.hb = habit_generator;
    }

    /**
     * This controller integrate the geolife database into mongodb. We use a
     * controller because of lack of privileges on spem2, and no time to wait for an
     * answer. The code is kept for eventual re-use, but the corresponding route is
     * probably already deleted.
     */
    public Result integrate_geolife() throws Exception,UnsupportedEncodingException {
        String username;
        String password = "geolife_1_3";
        String email = "hellogoodbye1853@gmail.com";
        Document cur_user;
        File trajectory_dir;
        String[] cur_line;
        Calendar cal = null, prev_cal;
        double lat, prev_lat;
        double lon, prev_lon;
        Coordinate coord;
        Point cur_point;
        ArrayList<Point> point_list;
        ArrayList<Document> journey_list;
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        MongoCollection<Document> users = database.getCollection("users");
        final File data_folder = new File("D:\\cedri\\Documents\\Geolife Trajectories 1.3\\Data");
        for (final File user_dir : data_folder.listFiles()) {
            username = user_dir.getName();
            System.out.println(username);
            trajectory_dir = new File(user_dir.getPath() + "/Trajectory");
            journey_list = new ArrayList<Document>();
            for (final File journey : trajectory_dir.listFiles()) {
                prev_cal = null;
                prev_lat = -500; // -500 is out of the ranges
                prev_lon = -500;
                point_list = new ArrayList<Point>();
                fis = new FileInputStream(journey.getPath());
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);
                while (br.ready()) {
                    cur_line = br.readLine().split(",");
                    if (!cur_line[0].contains(".")) {
                        continue;
                    }
                    cur_line[6] = cur_line[6].replace(':', '-');
                    try {
                        cal = Constants.stringToCalendar(cur_line[5] + " " + cur_line[6]);
                    } catch (java.text.ParseException e) {
                        return badRequest("Bad format for calendar");
                    }
                    lat = Double.parseDouble(cur_line[0]);
                    lon = Double.parseDouble(cur_line[1]);
                    if (prev_cal == null || (Math.abs(cal.getTime().getTime() - prev_cal.getTime().getTime()) >= 150000
                            && distance(lat, prev_lat, lon, prev_lon, 0, 0) >= 1000)) {
                        coord = Constants.CoordinateTransformation(lat, lon);
                        cur_point = new Point(cal, coord);
                        point_list.add(cur_point);
                        prev_cal = cal;
                        prev_lat = lat;
                        prev_lon = lon;
                    }
                }
                if (point_list.size() > 1) {
                    journey_list.add(new Journey(point_list).toDoc());
                }
                br.close();
                isr.close();
                fis.close();
            }
            ArrayList<Byte> a_user_E = MongoDB.aes.encrypt(username);
            ArrayList<Byte> a_password_E = MongoDB.aes.encrypt(password);
            ArrayList<Byte> email_E = MongoDB.aes.encrypt(email);

            Document new_user = new Document("user", a_user_E).append("password", a_password_E).append("email", email_E)
                    .append("journeys", journey_list);
            users.insertOne(new_user);
        }
        return ok();
    }

    /**
     * Calculate distance between two points in latitude and longitude taking into
     * account height difference. If you are not interested in height difference
     * pass 0.0. Uses Haversine method as its base.
     * 
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters el2
     * End altitude in meters
     * 
     * @return Distance in Meters credits :
     *         https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
     */
    public static double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);

    }
}
