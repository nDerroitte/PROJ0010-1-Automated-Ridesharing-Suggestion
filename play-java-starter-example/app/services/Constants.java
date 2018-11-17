import java.util.Arrays;
import java.util.List;

public final class Constants
{
    private Constants() {
        System.err.println("This class should never be instantiate.");
        System.exit(1);
    }
    public static final List<String> DAY_LIST = Arrays.asList("SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY");
}