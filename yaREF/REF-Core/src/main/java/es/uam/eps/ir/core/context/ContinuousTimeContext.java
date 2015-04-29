package es.uam.eps.ir.core.context;

/**
 *
 * @author pedro
 */
public class ContinuousTimeContext implements ContinuousTimeContextIF{
    Long time;

    public ContinuousTimeContext(Long time) {
        this.time = time;
    }

    public long getTimestamp() {
        return time;
    }

    public int compareTo(ContextIF o) {
        if (o instanceof ContinuousTimeContext){
            return time.compareTo(((ContinuousTimeContext)o).getTimestamp());
        }
        else{
            return Integer.MAX_VALUE;
        }
    }
    
    
    @Override
    public String toString(){
        return time.toString();
    }
    
}
