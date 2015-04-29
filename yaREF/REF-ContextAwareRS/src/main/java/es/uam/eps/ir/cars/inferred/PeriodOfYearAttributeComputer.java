package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import java.util.Calendar;

/**
 *
 * @author pedro
 */
public class PeriodOfYearAttributeComputer<C extends ContinuousTimeContext> extends AbstractTimeContextAttributeComputer<C> implements ContextualAttributeComputerIF<C>{

    public String getAttributeName() {
        return "PeriodOfYear";
    }

    public String getAttributeType() {
        return "Winter,Spring,Summer,Autum";
    }
    
    public double getAttributeValue(C context) { 
        return (computeValue(context) - 0.01);
    }
    
    public String getAttributeNominalValue(C context) {
        int poy = new Double(computeValue(context)).intValue();
        switch (poy){
            case 0:
                return "Winter";
            case 1:
                return "Spring";
            case 2:
                return "Summer";
            case 3:
                return "Autum";
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
            case 11: // December
            case 0: // January
            case 1: // February
                poy = 0; // Winter
                break;
            case 2: // March
            case 3: // April
            case 4: // May
                poy = 1; // Spring
                break;
            case 5: // June
            case 6: // July
            case 7: // August
                poy = 2; // Summer
                break;
            case 8: // September
            case 9: // October
            case 10: // November
                poy = 3; // Autum
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
