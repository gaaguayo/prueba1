package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.cars.inferred.CategoricalContextComputerIF;
import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder;
import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder.TimeContext;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * This class creates Contextual segments (slices) from a {@link ModelIF}
 * based on a (set of) given time property(ies).
 *
 * @author Pedro G. Campos <pcampossoto@gmail.com>
 * Creation date: 02-apr-2013
 */
public class ContextualSlicer_ContinuousTimeContext<U,I,C extends ContextIF> extends AbstractContextualSlicer<U,I,C> implements ContextualSlicerIF<U,I,C>{
    protected List<TimeContext> contexts;
    
    
    public ContextualSlicer_ContinuousTimeContext(List<TimeContext> contexts) {
        this.contexts = contexts;
    }
    
    @Override
    public boolean isInSegment(PreferenceIF<U,I,C> pref, ContextualSegmentIF segment) {
        ContextualSegmentIF prefContextSegment = this.getSegment(pref.getContext());
        if (prefContextSegment.toString().equalsIgnoreCase(segment.toString())){
            return true;
        }
        
        return false;
    }
        
    public List<ContextualSegmentIF> getSegments(){
        
        List<String> _segments = this.getCombination(contexts.size()-1);

        List<ContextualSegmentIF> segments=new ArrayList<ContextualSegmentIF>();
        for(String s: _segments){
            ContextualSegmentIF cs = new StringContextualSegment(s);
            segments.add(cs);
        }
        return segments;
    }

    public ContextualSegmentIF getSegment(C context){
        ContextualSegmentIF segment=null;

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( ((ContinuousTimeContextIF)context).getTimestamp() );
        
        String contextSegment = null;
        for (TimeContext tCtx : contexts){
            CategoricalContextComputerIF computer = ContinuousTimeContextComputerBuilder.getContextComputer(tCtx);
            if (contextSegment == null){
                contextSegment = computer.getAttributeNominalValue(context);
            }            
            else{
                contextSegment = contextSegment + "_" + computer.getAttributeNominalValue(context);
            }
        }
        
        segment = new StringContextualSegment(contextSegment);
        return segment;
    }
    
    List<String> getCombination(int i){
        if (i < 0){
            return null;
        }
        List<String> theList = new ArrayList<String>();
        if (i == 0){
            CategoricalContextComputerIF computer = ContinuousTimeContextComputerBuilder.getContextComputer(contexts.get(i));
            theList.addAll(Arrays.asList(computer.getAttributeType().split(",")));
        }
        else{
            List<String> oldList = getCombination(i-1);
            CategoricalContextComputerIF computer = ContinuousTimeContextComputerBuilder.getContextComputer(contexts.get(i));
            for (String prefix : oldList){
                for (String ctx : Arrays.asList(computer.getAttributeType().split(","))){
                    theList.add(prefix + "_" + ctx);
                }
            }
        }
        
        return theList;
    }
}
