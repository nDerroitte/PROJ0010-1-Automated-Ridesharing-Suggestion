
import java.util.Calendar;
import java.util.Date;
import services.Point;
import java.io.IOException;
import org.junit.Test;
import play.*;
import play.test.Helpers.*;
import play.test.*;
import play.mvc.Http.RequestBuilder;
import play.mvc.Http;
import play.mvc.Http.Status;
import play.mvc.Result;
import play.test.Helpers;
import services.Coordinate;
import services.*;
import org.apache.commons.math3.distribution.NormalDistribution;
import java.util.*;
import static play.mvc.Http.Status.OK;
import org.junit.*;
import play.twirl.api.Content;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletionStage;
import java.util.Optional;
import play.libs.ws.*;
import java.util.Date;

public class IntegrateTest {

    TestServer server;
    WSClient ws;
    String user = "minh";
    String password = "minh";
    Optional<play.libs.ws.WSCookie> cookie;

    /**
     * run the server and create a ws client.
     */
    @Before
    public void set_up() {
        this.server = Helpers.testServer();
        Helpers.start(this.server);
        ws = WSTestClient.newClient(19001);
    }

    /**
     * 
     * Make integrate test. almost test all server API.
     */
    @Test
    public void integrate_test() throws Exception {
        erase_user(false);
        sign_up();
        sign_in();
        store_data();
        TimeUnit.SECONDS.sleep(600); // let the time to store data.
        get_habit();
        //erase_user(true);
    }

    /**
     * release ressource use for testing;
     */
    @After
    public void clear() throws Exception {
        ws.close();
        server.stop();
    }

    /**
     * Create a new user.
     */
    public void sign_up() throws Exception {
        CompletionStage<WSResponse> completionstage = ws
                .url("/sign_up?user=" + user + "&password=" + password + "&email=dd@pl").get();
        WSResponse result = completionstage.toCompletableFuture().get();
        assertEquals(OK, result.getStatus());
        assertTrue(result.getBody().contains("user successfully recorded"));
    }

    /**
     * sign in the user
     */
    public void sign_in() throws Exception {
        CompletionStage<WSResponse> completionstage = ws.url("/sign_in?user=" + user + "&password=" + password).get();
        WSResponse result = completionstage.toCompletableFuture().get();
        assertEquals(OK, result.getStatus());
        assertTrue(result.getBody().contains("connection OK"));
        cookie = result.getCookie("user");
    }

    /**
     * erase the user
     */
    public void erase_user(boolean assertion) throws Exception {
        CompletionStage<WSResponse> completionstage = ws.url("/remove_user?user=" + user + "&password=" + password)
                .get();
        WSResponse result = completionstage.toCompletableFuture().get();
        if (assertion) {
            assertEquals(OK, result.getStatus());
            assertTrue(result.getBody().contains("user succesfully removed"));
        }
    }

    /**
     * store user data
     */
    public void store_data() throws Exception {
        String body = json();
        CompletionStage<WSResponse> completionstage = ws.url("/store_data?user=" + user + "&password=" + password)
                .addCookie(cookie.get()).post(body);
        WSResponse result = completionstage.toCompletableFuture().get();
        assertEquals(OK, result.getStatus());
    }

    /**
     * Compute the habit of user and write them in a file.
     */
    public void get_habit() throws Exception {
        CompletionStage<WSResponse> completionstage = ws.url("/compute_habit?user=" + user).get();
        WSResponse result = completionstage.toCompletableFuture().get();
        assertEquals(OK, result.getStatus());
        assertTrue(result.getBody().contains("computing"));
    }

    /**
     * 
     * @param base_value value without noise
     * @param noise      maximum noise added/subtract to base_value
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
     * @return all point in the journey are in a straight line from start to end.
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
        for (int i = 0; i < (int) (r_max / r_step); i += 1) {
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

    /**
     * Generate the habit as the GSM will do it.
     * 
     * @param period
     * @param spread
     * @param reliability
     * @param base_date
     * @param noise
     * @param range
     * @param start
     * @param end
     * @return
     * 
     * 
     */
    public String GenerateHabit(int period, int spread, double reliability, long base_date, int noise, long range,
            Coordinate start, Coordinate end) {
        String out = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        ArrayList<Long> dates = new TestComputeHabit().new_data(period, spread, reliability, base_date, noise, range);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        for (long date : dates) {
            out += "{\"UserId\": \"" + user + "\",\"Points\" : [";
            ArrayList<Point> journey = journey(start, end, date);
            boolean first = true;
            for (Point point : journey) {
                if (!first) {
                    out += ",";
                }
                try {
                    if (date > sdf.parse("2021-00-00 00-00-00").getTime()) {
                        System.err.println("TOO LARGE !" + new Date(date) + " for generating habit between "
                                + start + " and " + end);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                out += "{\"calendar\": \"" + sdf.format(date) + "\",";
                out += "\"lat\":\"" + point.getPosition().getX() + "\",";
                out += "\"long\":\"" + point.getPosition().getY() + "\"}";
                first = false;
            }
            out += "]}";
            out += "data_splitter";
        }
        return out;
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public String json() throws Exception {
        String out = "";
        int period = 10080;
        int spread = 5;
        double reliability = 8.0 / 15;
        int noise = 0;
        long range = 10080 * 15;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Coordinate home = new Coordinate(50.5732, 5.5400);
        Coordinate academy = new Coordinate(50.5916, 5.4962);
        Coordinate rcae = new Coordinate(50.577, 5.569);
        Coordinate montef = new Coordinate(50.5858, 5.5591);
        Coordinate market = new Coordinate(50.5697, 5.550);
        Coordinate restaurant = new Coordinate(50.5661, 5.54655);
        Coordinate cinema = new Coordinate(50.6433, 5.56789);

        //18 habit described;
        
        // go to academy the Wednesday at 18:35
        reliability = 8.0 / 15; // -4 for hoolyday -3 dont go.
        spread = 5;
        long base_date = sdf.parse("2019-01-09 18-35-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, home, academy);

        // go back at 19:40
        base_date = sdf.parse("2019-01-09 19-40-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, academy, home);

        // go to acadamy the Thursday at 18:15
        base_date = sdf.parse("2019-01-10 18-15-00").getTime();
        reliability = 9.0 / 15; // -4 for hoolyday -2 dont go.
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, home, academy);

        // go back between 19:00-19:20
        spread = 10;
        base_date = sdf.parse("2019-01-10 19-10-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, academy, home);

        // go to rcae Friday at 18:45
        spread = 5;
        reliability = 11.0 / 15;
        base_date = sdf.parse("2019-01-11 18-45-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, home, rcae);

        // go back at 21:00
        base_date = sdf.parse("2019-01-11 21-00-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, rcae, home);

        // go to PI at 13:30 or 14:00
        reliability = 5.0 / 15;
        spread = 15;
        base_date = sdf.parse("2019-01-07 13-45-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, home, montef);

        // go back at 16:00 +- 1h
        spread = 60;
        base_date = sdf.parse("2019-01-07 16-00-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, montef, home);

        // go to compiler
        spread = 10;
        reliability = 11.0 / 15;
        base_date = sdf.parse("2019-01-08 14-00-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, home, montef);

        // go back
        spread = 30;
        base_date = sdf.parse("2019-01-08 16-30-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, montef, home);

        // goto ODM
        reliability = 8.0 / 15;
        spread = 10;
        base_date = sdf.parse("2019-01-09 14-00-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, home, montef);

        // goback
        spread = 30;
        base_date = sdf.parse("2019-01-09 16-30-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, montef, home);

        // goto AML and TIC
        spread = 10;
        reliability = 8.0 / 15;
        base_date = sdf.parse("2019-01-10 9-00-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, home, montef);

        // go back
        spread = 30;
        base_date = sdf.parse("2019-01-10 16-30-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, montef, home);

        // go to and back DL
        base_date = sdf.parse("2019-01-11 09-00-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, home, montef);

        //goback
        base_date = sdf.parse("2019-01-11 11-30-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, montef, home);

        // go to hypermarket the weekend
        base_date = sdf.parse("2019-01-06 00-00-00").getTime();
        spread = 1440;
        reliability = 0.9;
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, home, market);

        // go back
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, market, home);

        // go to restaurant one week over two
        base_date = sdf.parse("2019-01-12 00-19-00").getTime();
        spread = 60;
        reliability = 0.6;
        period = 2 * 10080;
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, home, restaurant);
        // goback
        base_date = sdf.parse("2019-01-12 00-21-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, restaurant, home);

        // go to cinema once a month
        base_date = sdf.parse("2019-01-13 00-18-00").getTime();
        period = 31 * 1440;
        reliability = 1;
        spread = 3*1440;
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, home, cinema);
        // goback
        base_date = sdf.parse("2019-01-13 00-21-00").getTime();
        out += GenerateHabit(period, spread, reliability, base_date, noise, range, cinema, home);
        return out;
    }

}
