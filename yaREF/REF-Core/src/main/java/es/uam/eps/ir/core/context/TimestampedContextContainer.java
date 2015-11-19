package es.uam.eps.ir.core.context;

import java.util.ArrayList;

/**
 *
 * @author pedro
 */
public class TimestampedContextContainer extends ContextContainer implements ContextIF, ContinuousTimeContextIF {
    Long time;

    public TimestampedContextContainer(Long time) {
        this.time = time;
        contexts = new ArrayList<ContextIF>();
    }
    
    public long getTimestamp() {
        return time;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.time != null ? this.time.hashCode() : 0);
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
        final TimestampedContextContainer other = (TimestampedContextContainer) obj;
        if (this.time != other.time && (this.time == null || !this.time.equals(other.time))) {
            return false;
        }
        return true;
    }
    
    
}
