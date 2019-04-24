import services.*;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

/**
 * test the circularDist class
 * @see [[CircularDist]]
 */
public class TestCircularDist {

    /**
     * test the distance between two point in a circular space is well performed.
     */
    @Test 
    public void testCircDist(){
        int period = 44640;
        double[] a = {5754};
        double[] b = {1435};
        double[] c = {44650};
        double[] d = {44630};
        CircularDist computer = new CircularDist(period);
        System.out.println(computer.compute(a,b) + " " + computer.compute(b,a) + " : " + (5754-1435));
        assertTrue(computer.compute(a,b) == 5754-1435 && computer.compute(b,a) == 5754-1435);
        assertTrue(computer.compute(a,c) == 5754-10 && computer.compute(c,a) == 5754-10);
        assertTrue(computer.compute(a,d) == 5754+10 && computer.compute(d,a) == 5754+10);

    }
}
