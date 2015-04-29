package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.cars.inferred.ContextualAttributeComputerIF;
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
//        List<String> _segments=new ArrayList<String>();
//        
//        if (contexts.size() == 1){
//            ContextualAttributeComputerIF computer = ContinuousTimeContextComputerBuilder.getContextComputer(contexts.get(0));
//            _segments.addAll(Arrays.asList(computer.getAttributeType().split(",")));
//        }
//        else if (contexts.size() == 2){
//            ContextualAttributeComputerIF computer0 = ContinuousTimeContextComputerBuilder.getContextComputer(contexts.get(0));
//            ContextualAttributeComputerIF computer1 = ContinuousTimeContextComputerBuilder.getContextComputer(contexts.get(1));
//            for (String ctx0 : Arrays.asList(computer0.getAttributeType().split(","))){
//                for (String ctx1 : Arrays.asList(computer1.getAttributeType().split(","))){
//                    _segments.add(ctx0 + "_" + ctx1);
//                }            
//            }
//        }
//        else if (contexts.size() == 3){
//            ContextualAttributeComputerIF computer0 = ContinuousTimeContextComputerBuilder.getContextComputer(contexts.get(0));
//            ContextualAttributeComputerIF computer1 = ContinuousTimeContextComputerBuilder.getContextComputer(contexts.get(1));
//            ContextualAttributeComputerIF computer2 = ContinuousTimeContextComputerBuilder.getContextComputer(contexts.get(2));
//            for (String ctx0 : Arrays.asList(computer0.getAttributeType().split(","))){
//                for (String ctx1 : Arrays.asList(computer1.getAttributeType().split(","))){
//                    for (String ctx2 : Arrays.asList(computer2.getAttributeType().split(","))){
//                        _segments.add(ctx0 + "_" + ctx1 + "_" + ctx2);
//                    }
//                }            
//            }
//        }
        
        List<String> _segments = this.getCombination(contexts.size()-1);
//        ContextualAttributeComputerIF[] computer = new ContextualAttributeComputerIF[contexts.size()];
//        for (int i = 0; i < contexts.size(); i++){
//            computer[i] = ContinuousTimeContextComputerBuilder.getContextComputer(contexts.get(i));
//            
//        }

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
            ContextualAttributeComputerIF computer = ContinuousTimeContextComputerBuilder.getContextComputer(tCtx);
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
            ContextualAttributeComputerIF computer = ContinuousTimeContextComputerBuilder.getContextComputer(contexts.get(i));
            theList.addAll(Arrays.asList(computer.getAttributeType().split(",")));
        }
        else{
            List<String> oldList = getCombination(i-1);
            ContextualAttributeComputerIF computer = ContinuousTimeContextComputerBuilder.getContextComputer(contexts.get(i));
            for (String prefix : oldList){
                for (String ctx : Arrays.asList(computer.getAttributeType().split(","))){
                    theList.add(prefix + "_" + ctx);
                }
            }
        }
        
        return theList;
    }
}
