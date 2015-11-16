package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import java.util.Calendar;

/**
 *
 * @author pedro
 */
public class PeriodOfDayAttributeComputer1<C extends ContinuousTimeContextIF> extends AbstractTimeContextAttributeComputer<C> implements CategoricalContextComputerIF<C>{

    public String getAttributeName() {
        return "PeriodOfDay1";
    }

    public String getAttributeType() {
        return "Night,Morning,Afternoon";
    }
    
    public double getAttributeValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
            int hod = cal.get(Calendar.HOUR_OF_DAY);
            int pod;
            if (hod < 7 ) { // night
                pod=0;
            } else if (hod < 12) { // morning
                pod=1;
            } else if (hod < 21) { // afternoon
                pod=2;
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
        } else if (hod < 21) { // afternoon
            pod=2;
        } else { // night
            pod=0;
        }

        switch (pod){
            case 0:
                return "Night";
            case 1:
                return "Morning";
            case 2:
                return "Afternoon";
            default:
                return null;
        }
    }
    
}
