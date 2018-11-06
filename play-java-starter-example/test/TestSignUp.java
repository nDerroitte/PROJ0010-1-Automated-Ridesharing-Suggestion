import org.junit.Test;
import play.*;
import play.test.Helpers;
import play.mvc.Http.RequestBuilder;
import play.mvc.Http;
import play.mvc.Result;
//import static  play.test.Helpers;
public class TestSignUp {
	
  @Test
  public void SIGNUP() throws Exception {
	Application fakeApp = Helpers.fakeApplication();
    RequestBuilder request = Helpers.fakeRequest("GET","/sign_up?user=minh&password=0");
    Result result = Helpers.route(fakeApp,request);
	 request = Helpers.fakeRequest("GET","/sign_up?user=schils&password=1");
     result = Helpers.route(fakeApp,request);
  }
}