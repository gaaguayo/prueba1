package es.uam.eps.ir.split.baseset;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public class BSUser<U,I,C extends ContextIF> implements BaseSetGeneratorIF<U,I,C>{

    @SuppressWarnings("CallToThreadDumpStack")
    public Collection<ModelIF<U, I, C>> getBaseSets(ModelIF<U, I, C> model) {
        Collection<ModelIF<U, I, C>> _collection = new ArrayList<ModelIF<U, I, C>>();
        for (U user:model.getUsers()){
            Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)model.getPreferencesFromUser(user);
            ModelIF<U, I, C> userModel=null;
            try {
                userModel = model.getClass().newInstance();
            }
            catch (Exception e){
                System.err.println("problem instantiating userModel");
                e.printStackTrace();
                return _collection;
            }
            for (PreferenceIF<U,I,C> pref:prefs){
                userModel.addPreference(user, pref.getItem(), pref.getValue(), pref.getContext());
            }
            _collection.add(userModel);
        }
        return _collection;
    }
    
    @Override
    public String toString(){
        return "userCentric";
    }
}
