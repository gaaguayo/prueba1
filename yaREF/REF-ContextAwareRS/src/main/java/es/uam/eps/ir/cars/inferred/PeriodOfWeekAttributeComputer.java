package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import java.util.Calendar;

/**
 *
 * @author pedro
 */
public class PeriodOfWeekAttributeComputer<C extends ContinuousTimeContext> extends AbstractTimeContextAttributeComputer<C> implements CategoricalContextComputerIF<C>{

    public String getAttributeName() {
        return "PeriodOfWeek";
    }

    public String getAttributeType() {
        return "Weekend,Workday";
    }
    
    public double getAttributeValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int dow = cal.get(Calendar.DAY_OF_WEEK);
            int pow;
            if (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) {
                pow = 0;
            } else {
                pow = 1;
            }
        return (pow - 0.01);
    }
    
    public String getAttributeNominalValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        int pow;
        if (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) {
            pow = 0;
        } else {
            pow = 1;
        }
        
        switch (pow){
            case 0:
                return "Weekend";
            case 1:
                return "Workday";
            default:
                return null;
        }
            
//        return "" + pow;
    }
    
}
