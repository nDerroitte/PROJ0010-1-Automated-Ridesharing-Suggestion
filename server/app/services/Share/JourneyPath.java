package services;
import java.util.ArrayList;

public class JourneyPath{
    Coordinate start;
    Coordinate end;
    public JourneyPath( ArrayList<Coordinate> path){
        double lat = path.get(0).getX();
        double lon = path.get(0).getY();
        start = Constants.CoordinateTransformation(lat, lon);
        lat = path.get(path.size()-1).getX();
        lon = path.get(path.size()-1).getY();
        end = Constants.CoordinateTransformation(lat, lon);
    }

    @Override
    public String toString(){
        return start.toString() + "___" + end.toString();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof JourneyPath){
            JourneyPath j = (JourneyPath) o;
            return j.end.isSame(end) && j.start.isSame(start);
        }
        return false;
    }

    @Override 
    public int hashCode(){
        return toString().hashCode();
    }
}