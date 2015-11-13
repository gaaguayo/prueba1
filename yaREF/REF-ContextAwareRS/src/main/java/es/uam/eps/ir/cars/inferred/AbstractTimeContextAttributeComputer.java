package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import java.util.Calendar;
import weka.core.Attribute;
import weka.core.FastVector;

/**
 *
 * @author pedro
 */
public abstract class AbstractTimeContextAttributeComputer<C extends ContinuousTimeContext> implements CategoricalContextComputerIF<C>{
    protected final Calendar cal = Calendar.getInstance();
    protected final Attribute attribute;
    
    public abstract String getAttributeName();
//    protected abstract String getAttributeType();
    

    public AbstractTimeContextAttributeComputer() {
        if (getAttributeType().compareTo("NUMERIC")==0){
            attribute = new Attribute(getAttributeName());
        }
        else {
            FastVector nominalValues = new FastVector();
            String[] vals = getAttributeType().split(",");
            for (String val : vals){
                nominalValues.addElement(val);
            }
           
            attribute = new Attribute(getAttributeName(), nominalValues);
        }
    }

    public Attribute getAttribute() {            
        return attribute;
    }
    
}
