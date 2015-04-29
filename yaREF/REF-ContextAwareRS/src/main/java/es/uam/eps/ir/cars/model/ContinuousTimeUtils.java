package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.split.SplitIF;

/**
 *
 * @author pedro
 */
public class ContinuousTimeUtils {
    public static long trainingTimespan(SplitIF split){
        ContextualModelUtils<Object, Object, ContinuousTimeContextIF> eTrain = new ContextualModelUtils<Object, Object, ContinuousTimeContextIF>(split.getTrainingSet());
        ContextualModelUtils<Object, Object, ContinuousTimeContextIF> eTest = new ContextualModelUtils<Object, Object, ContinuousTimeContextIF>(split.getTestingSet());
        return trainingTimespan(eTrain, eTest);
    }
    
    public static long trainingTimespan(ContextualModelUtils eTrain, ContextualModelUtils eTest){
        long timespan = 0l;
        
        try{
            long minTime  = eTrain.getMinDate().getTime();
            long maxTime = eTest.getMinDate().getTime();
            timespan = maxTime - minTime;
        }
        catch (NullPointerException e){}
        return timespan;        
    }
    
    public static int toDays(long timestamp){
        return ((Long)(timestamp / 86400000L)).intValue();
    }
}
