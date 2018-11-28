package controllers;

import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;
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
import services.MongoInterface;
import services.HabitGenerator;
import services.Point;
import services.Constants;
import services.Coordinate;
import services.Journey;



@Singleton
public class StoreData extends Controller {
	
	 private final MongoDatabase database ;
	 private final HabitGenerator hb;

	@Inject
	public StoreData  (MongoInterface db, HabitGenerator habit_generator){
		this.database = db.get_database();	
		this.hb = habit_generator;
	}

	//En cours de devellopement.
	public Result store_data(){
		System.err.println("request: " + request().body().asText());
		String[] data = request().body().asText().split("data_splitter");
		JsonReader reader;
		JsonObject dataUnit;
		Optional cookie = request().header("cookie");
		if (!cookie.isPresent()) {
			return badRequest("Cookie required");
		}
		String cookieValue = cookie.get().toString().split(";")[0];
		if (!cookieValue.split("=")[0].equals("user") || cookieValue.split("=").length != 2) {
			return badRequest("Cookie badly set");
		}

		for (String jSonString : data) {
			reader = Json.createReader(new StringReader(jSonString));
			dataUnit = reader.readObject();
			reader.close();
			MongoCollection<Document> users = database.getCollection("users");
			Document user = users.find(eq("user", dataUnit.getString("UserId"))).first();
			if (user == null || !user.get("key").equals(cookieValue.split("=")[1])) {
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
			Journey current_journey = new Journey(point_list);
			ArrayList<Document> journeys = (ArrayList<Document>)(user.get("journeys"));
			journeys.add(Journey.toDoc(current_journey));
			users.updateOne(eq(user.get("user")),set("journeys", journeys));
			hb.submitTask((String)(user.get("user")), journeys);
		}
		return ok();
	}
}















