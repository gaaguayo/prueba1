package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import java.util.Calendar;

/**
 *
 * @author pedro
 */
public class DayOfWeekAttributeComputer<C extends ContinuousTimeContextIF> extends AbstractTimeContextAttributeComputer<C> implements CategoricalContextComputerIF<C>{

    public String getAttributeName() {
        return "DayOfWeek";
    }

    public String getAttributeType() {
        return "Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday";
    }
    
    public double getAttributeValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        return (dow - 0.01);
    }
    
    public String getAttributeNominalValue(C context) {
        cal.setTimeInMillis(context.getTimestamp());
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        switch (dow){
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            default:
                return null;
        }
    }
    
}
