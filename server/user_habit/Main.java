import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.DirectoryStream;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.nio.file.Paths;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Main {
    
    public class MethodStat {
        DescriptiveStatistics period;
        DescriptiveStatistics spread;
        DescriptiveStatistics point_explained;
        DescriptiveStatistics reliability;

        public String toString() {
            return period.toString() + "\n" + reliability.toString() + "\n" + spread.toString() + "\n"
                    + point_explained.toString() + "\n";
        }
    }

    public static void main(String[] args) {
        int nb_method = 3;
        Path root = Paths.get("C:\\Users\\cedri\\PI\\server\\user_habit");
        int[] nb_habit = new int[nb_method];
        MethodStat[] stats = new MethodStat[3];
        nb_journey = getFile("D:\\cedri\\Documents\\Geolife Trajectories 1.3\\Data");
        BufferedReader br = null;
        try {
            DirectoryStream<Path> users = Files.newDirectoryStream(root);
            for (Path user : users) {
                File f = user.toFile();
                if (f.isFile()) {
                    continue;
                }
                for (int i = 0; i < nb_method; i++) {
                    System.out.println("user: " + user.toString());
                    String habits_path = user.toAbsolutePath().toString() + "\\" + i;
                    System.out.println("habit path: " + habits_path);
                    DirectoryStream<Path> habits = Files.newDirectoryStream(Paths.get(habits_path));
                    for (Path habit : habits) {
                        nb_habit[i]++;
                        br = new BufferedReader(new FileReader(habit.toString()));
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (line.contains("period")) {
                                String[] split = line.split(":");
                                reliability[i] += Double.parseDouble(split[1]);
                                stats[i].reliability.addValue(Double.parseDouble(split[1]));
                            }
                            if (line.contains("reliability")) {
                                String[] split = line.split(":");
                                reliability[i] += Double.parseDouble(split[1]);
                                stats[i].reliability.addValue(Double.parseDouble(split[1]));
                            }
                            if (line.contains("spread")) {
                                String[] split = line.split(":");
                                spread[i] += Double.parseDouble(split[1]);
                                stats[i].spread.addValue(Double.parseDouble(split[1]));
                            }
                            if (line.contains("nb point")) {
                                String[] split = line.split(": ");
                                int nb_point = Integer.parseInt(split[1]);
                                line = br.readLine();
                                split = line.split(": ");
                                int realisation = Integer.parseInt(split[1]);
                                stats[i].point_explained.addValue(Math.min(nb_point, realisation));
                            }
                        }
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < nb_method; i++) {
            reliability[i] /= nb_habit[i];
            spread[i] /= nb_habit[i];
        }
        System.out.println("writing stats");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter("Method_Stat"));
            writer.println("reliability: " + Arrays.toString(reliability));
            writer.println("spread: " + Arrays.toString(spread));
            writer.println("journey_explained: " + Arrays.toString(journey_explained));
            writer.println("nb_journey: " + nb_journey);
            writer.println("habit find: " + Arrays.toString(nb_habit));
            for(int i=0; i < stats.length; i++){
                writer = new PrintWriter(new FileWriter("Method" + i));
                writer.print(stats[i].toString());
                writer.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    static int nb_method = 3;
    static int nb_journey = 0;
    static double[] spread = new double[nb_method];
    static double[] reliability = new double[nb_method];
    static int[] journey_explained = new int[nb_method];

    // return the number of file in a folder.
    private static int getFile(String dirPath) {
        int count = 0;
        File f = new File(dirPath);
        File[] files = f.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    count += getFile(file.getAbsolutePath());
                } else {
                    count++;
                }
            }
        }
        return count;
    }

}
