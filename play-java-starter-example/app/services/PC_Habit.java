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
