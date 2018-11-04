import org.junit.Test;
import play.*;
import play.test.Helpers;
import play.mvc.Http.RequestBuilder;
import play.mvc.Http;
import play.mvc.Result;
//import static  play.test.Helpers;
public class MyTests {
	
  @Test
  public void myPostActionTest() throws Exception {
	Application fakeApp = Helpers.fakeApplication();

	String fakeJson = "{ foo:bar}" ;
    RequestBuilder request = Helpers.fakeRequest("POST","/").bodyText(fakeJson);
    Result result = Helpers.route(fakeApp,request);
  }
}