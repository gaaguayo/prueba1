package es.uam.eps.ir.split.ratingorder;

import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.context.RatingPreferenceComparator;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Pedro G. Campos
 * @param <U> User ID
 * @param <I> Item ID
 * @param <C> Context information
 */
public class ROTime<U,I,C extends ContinuousTimeContextIF> implements RatingOrderIF<U,I,C>{

    public List<PreferenceIF<U, I, C>> getOrderedRatings(ModelIF<U, I, C> model) {
        final List<PreferenceIF<U, I, C>> prefsList = new ArrayList<PreferenceIF<U, I, C>>();
        for (U user:model.getUsers()){
            prefsList.addAll(model.getPreferencesFromUser(user));
        }
                
        // sort Preferences
//        Collections.sort(prefsList, Collections.reverseOrder(new RatingPreferenceComparator<U, I, C>()) );
        Collections.sort(prefsList, new RatingPreferenceComparator<U, I, C>() );
        
        return prefsList;
    }
    
    @Override
    public String toString(){
        return "timeOrder";
    }   
}
