import org.junit.Test;
import play.*;
import play.test.Helpers;
import play.mvc.Http.RequestBuilder;
import play.mvc.Http;
import play.mvc.Result;
import static play.mvc.Http.Status.OK;
import play.mvc.Http.Status;
import org.junit.Assert;
import play.twirl.api.Content;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//import static  play.test.Helpers;
public class TestSignUp {

  @Test
  public void SIGNUP() throws Exception {
    Application fakeApp = Helpers.fakeApplication();

    RequestBuilder request = Helpers.fakeRequest("GET", "/remove_user?user=TestUserName&password=TestPassword")
        .header(Http.HeaderNames.HOST, "localhost:9000");
    Helpers.route(fakeApp, request);;

    // sign up new user
    request = Helpers.fakeRequest("GET", "/sign_up?user=TestUserName&password=TestPassword&email=fsa@uliege.be")
        .header(Http.HeaderNames.HOST, "localhost:9000");
    Result result = Helpers.route(fakeApp, request);
    System.out.println(Helpers.contentAsString(result));
    assertEquals(OK, result.status());
    assertTrue(Helpers.contentAsString(result).contains("succes"));

  }
}