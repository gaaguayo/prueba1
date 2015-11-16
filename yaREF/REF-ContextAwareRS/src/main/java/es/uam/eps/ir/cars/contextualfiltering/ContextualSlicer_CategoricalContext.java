package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.cars.inferred.TimeContextDefinition;
import es.uam.eps.ir.core.context.CategoricalContext;
import es.uam.eps.ir.core.context.ContextContainer;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.List;

/**
 * This class creates Contextual segments (slices) from a {@link ModelIF}
 *
 * @author Pedro G. Campos <pcampossoto@gmail.com>
 * Creation date: 19-feb-2012
 */
public class ContextualSlicer_CategoricalContext<U,I,C extends ContextIF> extends AbstractContextualSlicer<U,I,C> implements ContextualSlicerIF<U,I,C>{
    protected List<ContextDefinition> ctxDefs;
    
    public ContextualSlicer_CategoricalContext(List<ContextDefinition> ctxDefs) {
        this.ctxDefs = ctxDefs;
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
        List<String> _segments = this.getCombination(ctxDefs.size()-1);

        List<ContextualSegmentIF> segments=new ArrayList<ContextualSegmentIF>();
        for(String s: _segments){
            ContextualSegmentIF cs = new StringContextualSegment(s);
            segments.add(cs);
        }
        return segments;
    }
    
    public ContextualSegmentIF getSegment(C context){
        ContextualSegmentIF segment=null;
        
        ContextContainer container = null;
        List<ContextIF> ctxs = new ArrayList();
        if (context instanceof ContextContainer){
            container = (ContextContainer)context;
            ctxs = container.getContexts();
        }
        
        String contextSegment = null;
        for (ContextDefinition ctxDef : ctxDefs){
            boolean isTimeContext = true;
            for (ContextIF ctx : ctxs){
                String name = ((CategoricalContext)ctx).getName();
                if (ctxDef.getName().equalsIgnoreCase(name)){
                    isTimeContext = false;
                    if (contextSegment == null){
                        contextSegment = ((CategoricalContext)ctx).getNominalValue();
                    }
                    else{
                        contextSegment = contextSegment + "_" + ((CategoricalContext)ctx).getNominalValue();                        
                    }
                }
            }
            if (isTimeContext){
                if (contextSegment == null){
                    contextSegment = ((TimeContextDefinition)ctxDef).getNominalValue((ContinuousTimeContextIF)context);
                }
                else{
                    contextSegment = contextSegment + "_" + ((TimeContextDefinition)ctxDef).getNominalValue((ContinuousTimeContextIF)context);
                }                
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
            theList.addAll(ctxDefs.get(i).getNominalValues());
        }
        else{
            List<String> oldList = getCombination(i-1);            
            for (String prefix : oldList){
                for (String ctx : ctxDefs.get(i).getNominalValues()){
                    theList.add(prefix + "_" + ctx);
                }
            }
        }        
        return theList;
    }

}
