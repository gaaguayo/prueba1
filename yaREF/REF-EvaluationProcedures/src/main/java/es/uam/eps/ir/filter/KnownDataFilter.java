package es.uam.eps.ir.filter;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.Collection;

/**
 *
 * @author pedro
 */
public class KnownDataFilter<U, I, C extends ContextIF> implements DatasetFilterIF<U, I, C> {
    private ModelIF<U,I,C> knownData;

    public KnownDataFilter(ModelIF<U, I, C> knownData) {
        this.knownData = knownData;
    }
    
    public ModelIF<U, I, C> filter(ModelIF<U, I, C> model) {
        ModelIF<U,I,C> filteredModel = null;
        try {
            filteredModel = model.getClass().newInstance();
        }
        catch(Exception e){
            System.err.println("Problem instantiating model");
            e.printStackTrace();
            System.exit(1);
        }
        
        Collection<U> knownUsers = knownData.getUsers();
        Collection<I> knownItems = knownData.getItems();
        
        for (U user: model.getUsers()){
            if (knownUsers.contains(user)){
                for (PreferenceIF<U,I,C> pref: model.getPreferencesFromUser(user)){
                    if (knownItems.contains(pref.getItem())){
                        filteredModel.addPreference(user, pref.getItem(), pref.getValue(), pref.getContext());
                    }
                }
            }
        }
        
        return filteredModel;
    }
    
}
