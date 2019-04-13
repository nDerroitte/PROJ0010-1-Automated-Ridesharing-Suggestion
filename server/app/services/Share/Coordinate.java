package services;
public final class Coordinate
{
    private final long x;
    private final long y;

    public Coordinate(long first, long second) {
        this.x = first;
        this.y = second;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public boolean isSame(Coordinate other)
    {
        long other_x = other.getX();
        long other_y = other.getY();
        if((other_x-Constants.COORDINATE_ERROR_ACCEPTED <= this.x
                || this.x <= other_x+Constants.COORDINATE_ERROR_ACCEPTED)
                &&(other_y-Constants.COORDINATE_ERROR_ACCEPTED <= this.y
                || this.x <= other_y+Constants.COORDINATE_ERROR_ACCEPTED))
            return true;
        return false;
    }

    public String toString(){
        return x + "," + y;
    }

}
