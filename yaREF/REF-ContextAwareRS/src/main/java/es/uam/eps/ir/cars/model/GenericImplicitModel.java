package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.model.impl.ImplicitPreference;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public class GenericImplicitModel<U, I, C extends ContextIF> extends GenericExplicitModel<U,I,C> implements ModelIF<U, I, C>, ImplicitFeedbackIF  {

    public GenericImplicitModel() {
        super();
    }

    @Override
    protected PreferenceIF<U, I, C> getPreferenceInstance(U user, I item, C context, Float pref){
        return new ImplicitPreference<U,I,C>(user, item, context, pref);
    }
    
    @Override
    protected Float getPreferenceValue(Collection<InternalPreference<C,Float>> itemPreferences, C context){
        if (context == null){
            Float rating = new Float(0);
            for (InternalPreference<C, Float> itemPref : itemPreferences){
                rating += itemPref.getPref();
            }
            return rating;
        }
        else{
            for (InternalPreference<C, Float> itemPref : itemPreferences){
                C valueContext = itemPref.getContext();
                if (valueContext == null) {
                    return null;
                }
                else if (itemPref.getContext().compareTo(context) == 0){
                    return itemPref.getPref();
                }
            }
        }        
        return null;
    }
}
