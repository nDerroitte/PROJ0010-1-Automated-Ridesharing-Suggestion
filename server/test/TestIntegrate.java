
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

public class TestIntegrate {
    
    Application fakeApp;
    String user = "minh";
    String password = "minh";

    /**
     * set up the fake app.
     */
    @Before 
    public void set_up(){
        this.fakeApp = Helpers.fakeApplication();
    }

    @Test 
    public void integrate_test(){
        sign_up();
        sign_in();
        erase_user();
    }    
    
    public void sign_up() {
        RequestBuilder request = Helpers.fakeRequest("GET","/sign_up?user="+user+"&password="+password+"&email=dd@pl");
        Result result = Helpers.route(fakeApp,request);
        assertEquals(OK, result.status());
        assertTrue(Helpers.contentAsString(result).contains("success"));
    }

    public void sign_in(){
        RequestBuilder request = Helpers.fakeRequest("GET","/sign_in?user="+user+"&password="+password);
        Result result = Helpers.route(fakeApp,request);
        assertEquals(OK, result.status());
        assertTrue(Helpers.contentAsString(result).contains("success"));
    }

    public void erase_user(){
        RequestBuilder request = Helpers.fakeRequest("GET","/remove_user?user="+user+"&password="+password);
        Result result = Helpers.route(fakeApp,request);
        assertEquals(OK, result.status());
        assertTrue(Helpers.contentAsString(result).contains("success"));
    }
    
    private double addnoise(double base_value,double noise){
        if(Math.random() < 0.5){
            return base_value - noise*Math.random();
        }   
        return base_value + noise*Math.random();     
    }

    public Coordinate fakeSpaceData(Coordinate point, double spread){
        NormalDistribution noise = new NormalDistribution(0,spread);
        double r = noise.sample();
        double alpha = Math.random() * Math.PI * 2;        
        double x = point.getX() + Math.cos(alpha) * r;
        double y = point.getY() + Math.sin(alpha) * r;
        return new Coordinate(x,y);
    }
  
    public ArrayList<Point> journey(Coordinate start_point, Coordinate end_point, long date_start){
        ArrayList<Point> out = new ArrayList<Point>();
        double delta_x = end_point.getX() - start_point.getX();
        double delta_y = end_point.getY() - start_point.getY();
        double alpha = 0;
        double r_max = Math.sqrt(delta_x*delta_x + delta_y*delta_y);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date_start));
        if(delta_x > 0){
            alpha = Math.atan(delta_y/delta_x);
        }
        else{
            alpha = Math.atan (delta_y/delta_x) + Math.PI;
        }
        double current_r = 0;
        double r_step = 0.01;
        double cur_x,cur_y,cur_r;
        for(int i=0; i < (int) r_max/r_step; i += 1){
            Calendar c = Calendar.getInstance();
            cur_x = start_point.getX() + Math.cos(alpha) * i * r_step;
            cur_y = start_point.getY() + Math.sin(alpha) * i * r_step;
            Coordinate cur_point = new Coordinate(cur_x,cur_y);
            cur_point = fakeSpaceData(cur_point,0.005/3 );
            c.setTime(new Date(date_start + i * 60000));
            out.add(new Point(c,cur_point));
        }
        cur_x = end_point.getX() ;
        cur_y = end_point.getY() ;
        Coordinate cur_point = new Coordinate(cur_x,cur_y);
        cur_point = fakeSpaceData(cur_point,0.005/3 );
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date_start + (long) (r_max/r_step +1) * 60000));
        out.add(new Point(c,cur_point));
        return out;
    }




}

