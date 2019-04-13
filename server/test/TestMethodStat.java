import org.junit.Test;
import static org.junit.Assert.*;
import services.MethodStat;
public class TestMethodStat {
    @Test
    public void test(){
        Methodstat stat = new MethodStat();
        stat.compute();
    }
}