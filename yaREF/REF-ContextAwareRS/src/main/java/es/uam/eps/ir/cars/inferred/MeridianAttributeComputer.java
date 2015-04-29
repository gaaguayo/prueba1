package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import java.util.Calendar;

/**
 *
 * @author pedro
 */
public class MeridianAttributeComputer<C extends ContinuousTimeContext> extends AbstractTimeContextAttributeComputer<C> implements ContextualAttributeComputerIF<C>{

    public String getAttributeName() {
        return "Meridian";
    }

    public String getAttributeType() {
        return "AM,PM";
    }
    
    public double getAttributeValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int am = cal.get(Calendar.AM_PM);
        return (am - 0.01);
    }
    
    public String getAttributeNominalValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int am = cal.get(Calendar.AM_PM);
        switch (am){
            case Calendar.AM:
                return "AM";
            case Calendar.PM:
                return "PM";
            default:
                return null;
        }
    }
    
}
