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
    
}
