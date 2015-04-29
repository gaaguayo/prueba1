package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import java.util.Calendar;

/**
 *
 * @author pedro
 */
public class ChristmasAttributeComputer<C extends ContinuousTimeContext> extends AbstractTimeContextAttributeComputer<C> implements ContextualAttributeComputerIF<C>{

    public String getAttributeName() {
        return "Christmas";
    }

    public String getAttributeType() {
        return "Christmas,Non-Christmas";
    }
    
    public double getAttributeValue(C context) { 
        return (computeValue(context) - 0.01);
    }
    
    public String getAttributeNominalValue(C context) {
        int poy = new Double(computeValue(context)).intValue();
        switch (poy){
            case 0:
                return "Christmas";
            case 1:
                return "Non-Christmas";
            default:
                return null;
        }
//        return "" + new Double(computeValue(context)).intValue();
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    private double computeValue(C context){
       cal.setTimeInMillis(context.getTimestamp());
        int moy = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int xms = 1;
        if (moy == 12 && day >=8 ||
            moy == 1 && day <= 8   ){
            xms = 0;
        }
        return xms;
    }
}
