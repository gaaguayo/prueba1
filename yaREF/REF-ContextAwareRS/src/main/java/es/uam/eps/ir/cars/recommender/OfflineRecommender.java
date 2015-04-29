package es.uam.eps.ir.cars.recommender;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Pedro G. Campos
 */
public class OfflineRecommender<U,I,C extends ContextIF> implements RecommenderIF<U,I,C> {
    protected Map<U,Map<I,Float>> userItemRating;

    public OfflineRecommender() {
        userItemRating = new HashMap<U,Map<I,Float>>();
    }
    
    public void addPreference(U user, I item, Float pref, C context){
        Map<I,Float> itemRating = userItemRating.get(user);
        if (itemRating == null) {
            itemRating = new HashMap<I,Float>();
        }
        itemRating.put(item, pref);
        userItemRating.put(user, itemRating);
    }
    
    public Float predict(U user, I item, C context) {
        Map<I,Float> itemRating = userItemRating.get(user);
        if (itemRating == null) { return Float.NaN; }
        Float prediction = itemRating.get(item);
        if (prediction == null) { return Float.NaN; }
        return prediction;
    }

    public List<I> recommend(U user, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public I getMostRelevant(U user, I item1, I item2, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ModelIF<U, I, C> getModel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
