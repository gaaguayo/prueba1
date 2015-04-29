package es.uam.eps.ir.mahout;

import es.uam.eps.ir.core.model.impl.DefaultAggregationFunction;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

/**
 *
 * @author Pedro G. Campos
 */
public class DataModelFactory<U,I,C extends ContextIF> {
    public DataModel getDataModel(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> utils){
        
        FastByIDMap<PreferenceArray> mahoutPrefsMap = new FastByIDMap<PreferenceArray>(model.getUsers().size());
        for (U user : model.getUsers()){
            long userKey = ((Integer)utils.getUserIndex(user)).longValue();
            List<GenericPreference> mahoutUserPrefsList = new ArrayList<GenericPreference>();
            for (PreferenceIF<U,I,C> pref : (Collection<PreferenceIF<U,I,C>>)model.getUniquePreferencesFromUser(user, new DefaultAggregationFunction())){
                GenericPreference mahoutPref = new GenericPreference(
                        userKey,
                        ((Integer)utils.getItemIndex(pref.getItem())).longValue(),
                        pref.getValue());
                mahoutUserPrefsList.add(mahoutPref);
            }
            PreferenceArray mahoutUserPrefs = new GenericUserPreferenceArray(mahoutUserPrefsList);
            mahoutPrefsMap.put(userKey, mahoutUserPrefs);
        }
        
        DataModel mahoutModel = new GenericDataModel(mahoutPrefsMap);
        
        return mahoutModel;
    }
}
