public class PC_Habit{
    private final long period;
    private final long offset;

    PC_Habit(long a_period,long an_offset){
        this.period = a_period;
        this.offset = an_offset % a_period;
    }

    public long getPeriod(){
        return this.period;
    }

    public long getOffset(){
        return this.offset;
    }

    //return true if x does not aport new information regarding this. 
    //(Ex: if this.period = 10 and this.offset = 0, x.period = 20 and x.offset = 0, x doesn't bring new information)
    public boolean equivalent(PC_Habit x){
        if(x.getPeriod() % period != 0){
            return false;
        }
        return x.getOffset() % period == offset;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        if (!(obj instanceof PC_Habit))   
            return false;
        if (obj == this)
            return true;
        return this.getOffset() == ((PC_Habit) obj).getOffset() && 
            ((PC_Habit) obj).getPeriod() == this.getPeriod();
    }

    @Override
    public int hashCode(){
        return Long.hashCode(this.offset) % (Integer.MAX_VALUE/2) + Long.hashCode(this.period) % (Integer.MAX_VALUE/2);
    }
}
