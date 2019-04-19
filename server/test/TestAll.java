
import java.util.Calendar;
import java.util.Date;
import java.io.IOException;
import org.junit.Test;
import play.*;
import play.test.Helpers.*;
import play.mvc.Http.RequestBuilder;
import play.mvc.Http;
import play.mvc.Http.Status;
import play.mvc.Result;
import play.test.Helpers;
import services.*;
import org.apache.commons.math3.distribution.NormalDistribution;
import java.util.*;
import static play.mvc.Http.Status.OK;
import org.junit.*;
import play.twirl.api.Content;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.text.SimpleDateFormat;
public class TestAll {

    Application fakeApp;
    String user = "minh";
    String password = "minh";
    Http.Cookie cookie;

    /**
     * set up the fake app.
     */
    @Before
    public void set_up() {
        this.fakeApp = Helpers.fakeApplication();
    }

    @Test
    public void integrate_test() {
        erase_user(false);
        sign_up();
        sign_in();
        store_data();
        get_habit();
        erase_user(true);

    }

    /**
     * Create a new fake user.
     */
    public void sign_up() {
        RequestBuilder request = Helpers
                .fakeRequest("GET", "/sign_up?user=" + user + "&password=" + password + "&email=dd@pl")
                .header(Http.HeaderNames.HOST, "localhost:9000");
        System.out.println("/sign_up?user=" + user + "&password=" + password + "&email=dd@pl");
        Result result = Helpers.route(fakeApp, request);
        System.out.println(Helpers.contentAsString(result));
        assertEquals(OK, result.status());
        assertTrue(Helpers.contentAsString(result).contains("success"));
    }

    /**
     * sign in the fake user
     */
    public void sign_in() {
        RequestBuilder request = Helpers.fakeRequest("GET", "/sign_in?user=" + user + "&password=" + password)
                .header(Http.HeaderNames.HOST, "localhost:9000");
        Result result = Helpers.route(fakeApp, request);
        assertEquals(OK, result.status());
        assertTrue(Helpers.contentAsString(result).contains("success"));
        cookie = result.cookie("user");
    }

    /**
     * erase the fake user
     */
    public void erase_user(boolean assertion) {
        RequestBuilder request = Helpers.fakeRequest("GET", "/remove_user?user=" + user + "&password=" + password)
                .header(Http.HeaderNames.HOST, "localhost:9000");
        ;
        Result result = Helpers.route(fakeApp, request);
        System.out.println(Helpers.contentAsString(result));
        if(assertion){
            assertEquals(OK, result.status());
            assertTrue(Helpers.contentAsString(result).contains("succes"));            
        }

    }

    /**
     * 
     * @param base_value value without noise
     * @param noise      maximum noise added/substract to base_value
     * @return base_value +- uniform[-noise,+noise]
     */
    private double addnoise(double base_value, double noise) {
        if (Math.random() < 0.5) {
            return base_value - noise * Math.random();
        }
        return base_value + noise * Math.random();
    }

    /**
     * 
     * @param point  original 2Dpoint
     * @param spread noise radius
     * @return noisy point
     */
    public Coordinate fakeSpaceData(Coordinate point, double spread) {
        NormalDistribution noise = new NormalDistribution(0, spread);
        double r = noise.sample();
        double alpha = Math.random() * Math.PI * 2;
        double x = point.getX() + Math.cos(alpha) * r;
        double y = point.getY() + Math.sin(alpha) * r;
        return new Coordinate(x, y);
    }

    /**
     * 
     * @param start_point start point of the journey
     * @param end_point   end point of the journey
     * @param date_start  date of the begin of the journey
     * @return all point in the journey in straight line from start to end.
     */
    public ArrayList<Point> journey(Coordinate start_point, Coordinate end_point, long date_start) {
        ArrayList<Point> out = new ArrayList<Point>();
        double delta_x = end_point.getX() - start_point.getX();
        double delta_y = end_point.getY() - start_point.getY();
        double alpha = 0;
        double r_max = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date_start));
        if (delta_x > 0) {
            alpha = Math.atan(delta_y / delta_x);
        } else {
            alpha = Math.atan(delta_y / delta_x) + Math.PI;
        }
        double current_r = 0;
        double r_step = 0.01;
        double cur_x, cur_y, cur_r;
        for (int i = 0; i < (int) r_max / r_step; i += 1) {
            Calendar c = Calendar.getInstance();
            cur_x = start_point.getX() + Math.cos(alpha) * i * r_step;
            cur_y = start_point.getY() + Math.sin(alpha) * i * r_step;
            Coordinate cur_point = new Coordinate(cur_x, cur_y);
            cur_point = fakeSpaceData(cur_point, 0.005 / 3);
            c.setTime(new Date(date_start + i * 60000));
            out.add(new Point(c, cur_point));
        }
        cur_x = end_point.getX();
        cur_y = end_point.getY();
        Coordinate cur_point = new Coordinate(cur_x, cur_y);
        cur_point = fakeSpaceData(cur_point, 0.005 / 3);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date_start + (long) (r_max / r_step + 1) * 60000));
        out.add(new Point(c, cur_point));
        return out;
    }

    public String json() {
        String out = "";
        int period = 10080;
        int spread = 5;
        double reliability = 0.8;
        long base_date = new Date().getTime();
        int noise = 2;
        long range = 10080 * 1;

        ArrayList<Long> dates = new TestComputeHabit().new_data(period, spread, reliability, base_date, noise, range);
        base_date += 1440 - 20;
        reliability = 0.7;
        dates.addAll(new TestComputeHabit().new_data(period, spread, reliability, base_date, noise, range));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        for (long date : dates) {
            out += "{\"UserInfo\":{\"UserID\": " + user + "Data\": {\"Point\" : [";
            ArrayList<Point> journey = journey(new Coordinate(50.575, 5.5399), new Coordinate(50.592, 5.494), date);
            for (Point point : journey) {

                out += "{\"calendar\":" + sdf.format(date) + ",";
                out += "\"lat\":" + point.getPosition().getX() + ",";
                out += "\"lat\":" + point.getPosition().getY() + "}";
            }
            out += "]}}}";
            out += "data_splitter";
        }
        return out;
    }

    public void store_data() {
        String body = json();
        RequestBuilder request = Helpers.fakeRequest("POST", "/store_data?user=" + user + "&password=" + password)
                .header(Http.HeaderNames.HOST, "localhost:9000").cookie(cookie);
        request.bodyText(body);
        request.build();
        Http.Cookies cookies = request.cookies();
        for(Http.Cookie cookie : cookies){
            System.out.println(cookie.name() + " : " + cookie.value());
        }
        Result result = Helpers.route(fakeApp, request);
        System.out.println(Helpers.contentAsString(result));
        assertEquals(OK, result.status());
    }

    public void get_habit() {
        RequestBuilder request = Helpers.fakeRequest("GET", "/get_habit?user=" + user + "&method=" + 0)
                .header(Http.HeaderNames.HOST, "localhost:9000");
        Result result = Helpers.route(fakeApp, request);
        assertEquals(OK, result.status());
    }
}
