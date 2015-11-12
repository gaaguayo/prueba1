package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import java.util.Calendar;

/**
 *
 * @author pedro
 */
public class MonthOfYearAttributeComputer<C extends ContinuousTimeContext> extends AbstractTimeContextAttributeComputer<C> implements CategoricalContextComputerIF<C>{

    public String getAttributeName() {
        return "MonthOfYear";
    }

    public String getAttributeType() {
        return "0,1,2,3,4,5,6,7,8,9,10,11";
    }
    
    public double getAttributeValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int moy = cal.get(Calendar.MONTH);
        return (moy - 0.01);
    }
    
    public String getAttributeNominalValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int moy = cal.get(Calendar.MONTH);
        return "" + moy;
    }
    
}
