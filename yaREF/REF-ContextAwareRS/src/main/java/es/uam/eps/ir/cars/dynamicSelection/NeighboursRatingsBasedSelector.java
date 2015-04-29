package es.uam.eps.ir.cars.dynamicSelection;

import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.cars.neighborhood.SimilarityDatumIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author pedro
 */
public class NeighboursRatingsBasedSelector<U,I,C extends ContextIF> extends DynamicSelector<U,I,C>{
    protected NeighborhoodIF nearestNeighborsComputer;

    public NeighboursRatingsBasedSelector(NeighborhoodIF nearestNeighborsComputer, RecommenderIF<U,I,C> knn, RecommenderIF<U,I,C> timeDecay) {
        this.nearestNeighborsComputer = nearestNeighborsComputer;
        recommenders = new HashMap<String, RecommenderIF<U,I,C>>();
        recommenders.put("KNN", knn);
        recommenders.put("TimeDecay", timeDecay);        
    }
    
    protected String select(U user, I item, C context){
        List<PreferenceIF<U,I,C>> neighborhoodPrefs = new ArrayList<PreferenceIF<U,I,C>>();
        ModelIF<U,I,C> model = nearestNeighborsComputer.getModel();
        if (model.getPreferencesFromUser(user) != null && model.getPreferencesFromItem(item) != null){
            List<SimilarityDatumIF> neighborhood=nearestNeighborsComputer.getNeighbors(user);
            for (SimilarityDatumIF data:neighborhood){
                U neighborKey=(U)data.getKey();
                Collection<? extends PreferenceIF<U,I,C>> neighborPrefs = model.getPreferences(neighborKey, item);
                if (neighborPrefs != null) {
                    neighborhoodPrefs.addAll(neighborPrefs);
                }
            }
            // neighborhood' ratings SD
            double mean = 0.0;
            int count = 0;
            for (PreferenceIF<U,I,C> pref: neighborhoodPrefs){
                mean += pref.getValue();
                count++;
            }
//            mean /= (double)count;
//            double sd = 0.0;
//            for (PreferenceIF<U,I,C> pref: neighborhoodPrefs){
//                sd += Math.pow(pref.getValue() - mean, 2.0);
//            }
//            sd = Math.sqrt(sd / (double)count);

            if (count > 90) return "TimeDecay";
//            if (sd > 0.92) return "TimeDecay";

        }
        
        return "KNN";
    }

    public ModelIF<U, I, C> getModel() {
        return ((RecommenderIF<U,I,C>)recommenders.get("KNN")).getModel();
    }

    public I getMostRelevant(U user, I item1, I item2, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Float predict(U user, I item, C context) {
        return recommenders.get(select(user, item, context)).predict(user, item, context);
    }

    public List<I> recommend(U user, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
