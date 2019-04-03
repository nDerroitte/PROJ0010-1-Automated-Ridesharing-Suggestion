package services;
public final class Coordinate
{
    private final double x;
    private final double y;

    public Coordinate(double first, double second) {
        this.x = first;
        this.y = second;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isSame(Coordinate other)
    {
        double other_x = other.getX();
        double other_y = other.getY();
        if((other_x-Constants.COORDINATE_ERROR_ACCEPTED <= this.x
                || this.x <= other_x+Constants.COORDINATE_ERROR_ACCEPTED)
                &&(other_y-Constants.COORDINATE_ERROR_ACCEPTED <= this.y
                || this.x <= other_y+Constants.COORDINATE_ERROR_ACCEPTED))
            return true;
        return false;
    }

    @Override
    public String toString(){
        return x + "," + y;
    }

}
