package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import java.util.Calendar;

/**
 *
 * @author pedro
 */
public class HolidayAttributeComputer<C extends ContinuousTimeContextIF> extends AbstractTimeContextAttributeComputer<C> implements CategoricalContextComputerIF<C>{

    public String getAttributeName() {
        return "Holiday";
    }

    public String getAttributeType() {
        return "Holiday,non-Holiday";
    }
    
    public double getAttributeValue(C context) { 
        return (computeValue(context) - 0.01);
    }
    
    public String getAttributeNominalValue(C context) {
        int poy = new Double(computeValue(context)).intValue();
        switch (poy){
            case 0:
                return "Holiday";
            case 1:
                return "non-Holiday";
            default:
                return null;
        }
//        return "" + new Double(computeValue(context)).intValue();
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    private double computeValue(C context){
       cal.setTimeInMillis(context.getTimestamp());
        int moy = cal.get(Calendar.MONTH);
        int poy = 0;
        switch (moy){
            case 6: // July
            case 7: // August
                poy = 0; // Holiday
                break;
            case 0: // January
            case 1: // February
            case 2: // March
            case 3: // April
            case 4: // May
            case 5: // June
            case 8: // September
            case 9: // October
            case 10: // November
            case 11: // December
                poy = 1; // non-Holiday
                break;
            default:
                System.err.println("Incorrect value for month of year: " + moy + " in Context" + context);
                Thread.dumpStack();
                System.exit(1);
                break;
        }
        return poy;
    }
}
