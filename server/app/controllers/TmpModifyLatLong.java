package controllers;

import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;
import java.io.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import services.HabitGenerator;
import services.MongoInterface;
import services.Journey;
import services.Point;
import services.Coordinate;


@Singleton
public class TmpModifyLatLong extends Controller {
	
	private final MongoDatabase database;
    private final HabitGenerator hb;

	@Inject
	public TmpModifyLatLong (MongoInterface db, HabitGenerator habit_generator){
        this.database = db.get_database();
		this.hb = habit_generator;
	}

	public Result tmp_modify_lat_long() {
		MongoCollection<Document> users = database.getCollection("users");
		MongoCursor<Document> cursor = users.find().iterator();
		ArrayList<Document> new_journeys;
		Journey cur_journey;
		Point cur_point;
		ArrayList<Point> cur_points;
		Coordinate cur_coord;
		String prev_datetime;
		Long prev_lat;
		Long prev_lon;
		Double new_lat;
		Double new_lon;
		Calendar cal;
		Date date;
		SimpleDateFormat sdf;
		try {
			while (cursor.hasNext()) {
				Document user = cursor.next();
				new_journeys = new ArrayList<>();
				ArrayList<Document> prev_journeys = (ArrayList<Document>)(user.get("journeys"));
				for (Document journey : prev_journeys) {
					ArrayList<Document> doc_meeting_point = (ArrayList<Document>)journey.get("meeting_point");
					cur_points = new ArrayList<>();
					for (Document doc_point : doc_meeting_point) {
						prev_datetime = (String)doc_point.get("time");
						ArrayList<Long> coordinates = (ArrayList<Long>)doc_point.get("position");
						try {
							prev_lat = coordinates.get(0);
							prev_lon = coordinates.get(1);
						} catch (ClassCastException e) {
							break;
						}
						new_lat = (double)(500000 + prev_lat) / 10000;
						new_lon = (double)(500000 + prev_lon) / 10000;
						sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
					    date = sdf.parse(prev_datetime);
					    cal = Calendar.getInstance();
					    cal.setTime(date);
					    cur_point = new Point(cal, new Coordinate(new_lat, new_lon));
					    cur_points.add(cur_point);
					}
					cur_journey = new Journey(cur_points);
					new_journeys.add(cur_journey.toDoc());
				}
				users.updateOne(eq("user", user.get("user")),set("journeys", new_journeys));
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			cursor.close();
		}
		return ok("computing...");
	}
}










