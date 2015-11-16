package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import java.util.Calendar;

/**
 *
 * @author pedro
 */
public class HourOfDayAttributeComputer<C extends ContinuousTimeContextIF> extends AbstractTimeContextAttributeComputer<C> implements CategoricalContextComputerIF<C>{

    public String getAttributeName() {
        return "HourOfDay";
    }

    public String getAttributeType() {
        return "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23";
    }
    
    public double getAttributeValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int hod = cal.get(Calendar.HOUR_OF_DAY);
        return (hod - 0.01);
    }
    
    public String getAttributeNominalValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int hod = cal.get(Calendar.HOUR_OF_DAY);
        return "" + hod;
    }
    
}
