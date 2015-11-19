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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.time != null ? this.time.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContinuousTimeContext other = (ContinuousTimeContext) obj;
        if (this.time != other.time && (this.time == null || !this.time.equals(other.time))) {
            return false;
        }
        return true;
    }
    
    
    
}
