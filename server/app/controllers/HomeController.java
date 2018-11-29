package controllers;

import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;
import java.io.*;
/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(index.render("Your new application is ready."));
    }
	
	public Result write(){
		String path = "texte.txt";
		RequestBody body = request().body();
		String textBody = body.asText();
		File file = new File(path);

        try {		
			file.createNewFile();
            FileWriter fr = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fr);
			bw.write(textBody);
			bw.newLine();
			bw.close();
        } catch (IOException e) {
            e.printStackTrace();
			return internalServerError("Io exception occured");
        }
		catch (SecurityException e){
        e.printStackTrace();
		}
		return ok("successful write in file");
    }		
}



