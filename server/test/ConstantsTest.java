import services.Constants;
//import jdk.internal.jline.internal.TestAccessible;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import akka.dispatch.forkjoin.ThreadLocalRandom;
import jdk.jfr.Timestamp;
import services.Constants;

import static org.junit.Assert.*;

public class ConstantsTest{
    @Test
    public void CoordinateTransfo(){
        System.out.println(Constants.CoordinateTransformation(50.5786398, 5.552888).toString());
    }
}