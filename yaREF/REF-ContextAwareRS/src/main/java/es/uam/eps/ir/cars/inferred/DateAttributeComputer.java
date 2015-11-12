package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContext;

/**
 *
 * @author pedro
 */
public class DateAttributeComputer<C extends ContinuousTimeContext> extends AbstractTimeContextAttributeComputer<C> implements CategoricalContextComputerIF<C>{

    public String getAttributeName() {
        return "Date";
    }

    public String getAttributeType() {
        return "NUMERIC";
    }
    
    public double getAttributeValue(C context) {
        int date = new Long(context.getTimestamp() / (long)(1000*60*60*24)).intValue();
        return date;
    }
    
    public String getAttributeNominalValue(C context) {
        int date = new Long(context.getTimestamp() / (long)(1000*60*60*24)).intValue();
        return "" + date;
    }
    
}
