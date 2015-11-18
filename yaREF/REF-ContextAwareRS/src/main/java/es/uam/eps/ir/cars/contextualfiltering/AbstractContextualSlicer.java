package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.model.impl.GenericExplicitModel;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;

/**
 * Super class for implementers of {@link ContextualSlicerIF}.
 *
 * @author Pedro G. Campos <pcampossoto@gmail.com>
 * Creation date: 19-feb-2012
 */
public abstract class AbstractContextualSlicer<U,I,C extends ContextIF> implements ContextualSlicerIF<U,I,C> {
    protected String[] segmentNames;
    
    public ModelIF<U,I,C> getSegmentData(ModelIF<U,I,C> fullData, ContextualSegmentIF segment){
        ModelIF<U,I,C> segmentData = new GenericExplicitModel<U,I,C>();
        for (U user:fullData.getUsers()){
            for (PreferenceIF<U,I,C> pref: fullData.getPreferencesFromUser(user)){
                if (isInSegment(pref, segment)){
                        segmentData.addPreference(user, pref.getItem(), pref.getValue(), pref.getContext());
                    }
                
            }
        }
//        ContextualModelUtils<U,I,C> eModel = new ContextualModelUtils<U,I,C>(segmentData);
        //eModel.getMeanRating();
        return segmentData;
    }
    
    public abstract boolean isInSegment(PreferenceIF<U,I,C> pref, ContextualSegmentIF segment);

    @Override
    public String toString(){
        return this.getClass().getSimpleName();
    }
}
