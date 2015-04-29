package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import java.util.Calendar;

/**
 *
 * @author pedro
 */
public class PeriodOfDayAttributeComputer<C extends ContinuousTimeContext> extends AbstractTimeContextAttributeComputer<C> implements ContextualAttributeComputerIF<C>{

    public String getAttributeName() {
        return "PeriodOfDay";
    }

    public String getAttributeType() {
        return "Night,Morning,Noon,Evening";
    }
    
    public double getAttributeValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
            int hod = cal.get(Calendar.HOUR_OF_DAY);
            int pod;
            if (hod < 7 ) { // night
                pod=0;
            } else if (hod < 12) { // morning
                pod=1;
            } else if (hod < 15) { // noon
                pod=2;
            } else if (hod < 21) { // evening
                pod=3;
            } else { // night
                pod=0;
            }
        return (pod - 0.01);
    }
    
    public String getAttributeNominalValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int hod = cal.get(Calendar.HOUR_OF_DAY);
        int pod;
        if (hod < 7 ) { // night
            pod=0;
        } else if (hod < 12) { // morning
            pod=1;
        } else if (hod < 15) { // noon
            pod=2;
        } else if (hod < 21) { // evening
            pod=3;
        } else { // night
            pod=0;
        }

        switch (pod){
            case 0:
                return "Night";
            case 1:
                return "Morning";
            case 2:
                return "Noon";
            case 3:
                return "Evening";
            default:
                return null;
        }
    }
    
}
