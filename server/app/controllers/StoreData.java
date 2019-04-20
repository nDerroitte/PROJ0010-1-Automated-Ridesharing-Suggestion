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

@Singleton
public class StoreData extends Controller {
	
	 private final MongoDatabase database ;
	 private final HabitGenerator hb;

	@Inject
	public StoreData  (MongoInterface db, HabitGenerator habit_generator){
		this.database = db.get_database();	
		this.hb = habit_generator;
	}

	// This function is called when a client send a set of journeys to the server.
	// Each journey is parsed independently, so that this is easier for the client
	// to handle wifi unavailability, and is stored in the database in a Journey format (cf class).
	@BodyParser.Of(BodyParser.TolerantText.class)
	public Result store_data() throws Exception{
		String out = "";
		int nb_journey = 0;
		String[] data = request().body().asText().split("data_splitter");
		JsonReader reader;
		JsonObject dataUnit;
		Http.Cookie cookie = request().cookies().get("user");
		if (cookie == null) {
			return badRequest("Cookie required");
		}
		String cookieValue = cookie.value();
		Document user = null;
		for (String jSonString : data) {
			reader = Json.createReader(new StringReader(jSonString));
			dataUnit = reader.readObject();
			reader.close();
			MongoCollection<Document> users = database.getCollection("users");
			user = users.find(eq("user", dataUnit.getString("UserId"))).first();
			if (user == null || !user.get("key").equals(cookieValue)) {
				out += "invalid cookie/user";
				continue;
			}
			ArrayList<Point> point_list = new ArrayList<>();
			for (JsonValue point : dataUnit.getJsonArray("Points")) {				
				JsonObject _point = (JsonObject)(point);
				Calendar cal;
				try {
					cal = Constants.stringToCalendar(_point.getString("calendar"));
				} catch (java.text.ParseException e) {
					return badRequest("Bad format for calendar");
				}

				double lat = Double.parseDouble(_point.getString("lat"));
				double lon = Double.parseDouble(_point.getString("long"));
				Coordinate coord = Constants.CoordinateTransformation(lat,lon);
				Point current_point = new Point(cal,coord);
				point_list.add(current_point);
			}
			if (point_list.size() <= 1) {	// A journey with only one point has no meaning : this is a measurement error
				continue;
			}
			Journey current_journey = new Journey(point_list);
			ArrayList<Document> journeys = (ArrayList<Document>)(user.get("journeys"));
			journeys.add(current_journey.toDoc());
			users.updateOne(eq("user", user.get("user")),set("journeys", journeys));
			nb_journey ++;
		}	
		if(user != null){
			hb.submitTask((String)(user.get("user")),0);
		}		
		return ok(out + " " + nb_journey + "data length: " + data.length);
	}
}
