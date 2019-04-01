package services;
import java.util.ArrayList;
public class JourneyPath{
    Coordinate start;
    Coordinate end;
    JourneyPath( ArrayList<Coordinate> path){
        double lat = path.get(0).getX();
        double lon = path.get(0).getY();
        start = Constants.CoordinateTransformation(lat, lon);
        lat = path.get(path.size()-1).getX();
        lon = path.get(path.size()-1).getY();
        end = Constants.CoordinateTransformation(lat, lon);
    }
}