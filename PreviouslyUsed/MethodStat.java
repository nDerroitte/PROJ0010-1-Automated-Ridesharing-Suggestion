package controllers;

import play.mvc.*;
import javax.inject.Singleton;

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

@Singleton
public class MethodStat extends Controller {

    public class Stat {
        DescriptiveStatistics period = new DescriptiveStatistics();
        DescriptiveStatistics spread = new DescriptiveStatistics();
        DescriptiveStatistics point_explained = new DescriptiveStatistics();
        DescriptiveStatistics reliability = new DescriptiveStatistics();

        public String toString() {
            return "period: \n" + period.toString() + "\n reliability: \n " + reliability.toString() + "\n spread: \n "
                    + spread.toString() + "\n point_explained \n" + point_explained.toString() + "\n";
        }
    }

    public Result compute() {
        int nb_method = 3;
        int nb_journey = 0;
        double[] spread = new double[nb_method];
        double[] reliability = new double[nb_method];
        int[] journey_explained = new int[nb_method];

        Path root = Paths.get("C:\\Users\\cedri\\PI\\server\\user_habit");
        int[] nb_habit = new int[nb_method];
        Stat[] stats = new Stat[3];
        for (int i = 0; i < nb_method; i++) {
            stats[i] = new Stat();
        }
        System.out.println("EMPTY STAT: " + stats[0].toString());
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
                                stats[i].period.addValue(Double.parseDouble(split[1]));
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
                                stats[i].point_explained.addValue(Math.min(nb_point, realisation) / nb_point);
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
            for (int i = 0; i < stats.length; i++) {
                writer = new PrintWriter(new FileWriter("Method" + i));
                writer.print(stats[i].toString());
                writer.close();
            }

        } catch (Exception e) {
            System.err.print("unable to write stat");
            e.printStackTrace();
        } finally {
            writer.close();
        }
        return ok("done");
    }

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
