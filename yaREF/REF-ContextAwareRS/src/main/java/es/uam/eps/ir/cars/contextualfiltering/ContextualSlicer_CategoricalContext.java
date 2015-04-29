package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.core.context.CategoricalContext;
import es.uam.eps.ir.core.context.ContextContainer;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class creates Contextual segments (slices) from a {@link ModelIF}
 * based on the Weekday or Weekend time property.
 *
 * @author Pedro G. Campos <pcampossoto@gmail.com>
 * Creation date: 19-feb-2012
 */
public class ContextualSlicer_CategoricalContext<U,I,C extends ContextIF> extends AbstractContextualSlicer<U,I,C> implements ContextualSlicerIF<U,I,C>{
    protected List<ContextDefinition> ctxDefs;
    
    public ContextualSlicer_CategoricalContext(List<ContextDefinition> ctxDefs) {
//        this.contextName = ctxDef.getName();
//        this.segmentNames = (String[])ctxDef.getNominalValues().toArray();
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
        List<String> _segments=new ArrayList<String>();
        
        if (ctxDefs.size() == 1){
            _segments.addAll(ctxDefs.get(0).getNominalValues());
        }
        else if (ctxDefs.size() == 2){
            for (String ctx1 : ctxDefs.get(0).getNominalValues()){
                for (String ctx2 : ctxDefs.get(1).getNominalValues()){
                    _segments.add(ctx1 + "_" + ctx2);
                }            
            }
        }
        else if (ctxDefs.size() == 3){
            for (String ctx1 : ctxDefs.get(0).getNominalValues()){
                for (String ctx2 : ctxDefs.get(1).getNominalValues()){
                    for (String ctx3 : ctxDefs.get(2).getNominalValues()){
                        _segments.add(ctx1 + "_" + ctx2 + "_" + ctx3);
                    }
                }            
            }
        }

        List<ContextualSegmentIF> segments=new ArrayList<ContextualSegmentIF>();
        for(String s: _segments){
            ContextualSegmentIF cs = new StringContextualSegment(s);
            segments.add(cs);
        }
        return segments;
    }

    public ContextualSegmentIF getSegment(C context){
        ContextualSegmentIF segment=null;
        
        ContextContainer container = (ContextContainer)context;
        List<ContextIF> ctxs = container.getContexts();
        
        String contextSegment = null;
        for (ContextDefinition ctxDef : ctxDefs){
            for (ContextIF ctx : ctxs){
                String name = ((CategoricalContext)ctx).getName();
                if (ctxDef.getName().equalsIgnoreCase(name)){
                    if (contextSegment == null){
                        contextSegment = ((CategoricalContext)ctx).getNominalValue();
                    }
                    else{
                        contextSegment = contextSegment + "_" + ((CategoricalContext)ctx).getNominalValue();                        
                    }
                }
            }
        }
        segment = new StringContextualSegment(contextSegment);
        return segment;
    }
    
}
