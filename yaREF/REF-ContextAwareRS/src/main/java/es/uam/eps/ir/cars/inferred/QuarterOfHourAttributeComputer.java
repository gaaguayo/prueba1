package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import java.util.Calendar;

/**
 *
 * @author pedro
 */
public class QuarterOfHourAttributeComputer<C extends ContinuousTimeContext> extends AbstractTimeContextAttributeComputer<C> implements ContextualAttributeComputerIF<C>{

    public String getAttributeName() {
        return "QuarterOfHour";
    }

    public String getAttributeType() {
        return "0,1,2,3";
    }
    
    public double getAttributeValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int moh = cal.get(Calendar.MINUTE);
            int qoh;
            if (moh < 15 ) {
                qoh=0;
            } else if (moh < 30) {
                qoh=1;
            } else if (moh < 45) {
                qoh=2;
            } else {
                qoh=3;
            }
        return (qoh - 0.01);
    }
    
    public String getAttributeNominalValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int moh = cal.get(Calendar.MINUTE);
            int qoh;
            if (moh < 15 ) {
                qoh=0;
            } else if (moh < 30) {
                qoh=1;
            } else if (moh < 45) {
                qoh=2;
            } else {
                qoh=3;
            }
        return "" + qoh;
    }
    
}
