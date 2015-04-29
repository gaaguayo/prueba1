package es.uam.eps.ir.cars.recommender;

import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.cars.neighborhood.SimilarityDatumIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceAggregationFunction;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.model.impl.DefaultAggregationFunction;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.List;

/**
 *
 * @author Pedro G. Campos
 */
public class GenericItemSimilarityBasedRecommender<U,I,C extends ContextIF> extends AbstractRecommender<U,I,C> implements RecommenderIF<U,I,C>{
    protected NeighborhoodIF nearestNeighborsComputer;
    protected int minCommRatings = 3;
    protected PreferenceAggregationFunction paf;
    protected PreferenceSimilarityFunction psf;

    public GenericItemSimilarityBasedRecommender(NeighborhoodIF nearestNeighborsComputer) {
        this.nearestNeighborsComputer = nearestNeighborsComputer;
        this.model = nearestNeighborsComputer.getModel();
        this.eModel = new ContextualModelUtils<U,I,C>(model);
        initializeFunctions();
    }

    public GenericItemSimilarityBasedRecommender(NeighborhoodIF nearestNeighborsComputer, int minCommRatings) {
        this(nearestNeighborsComputer);
        this.minCommRatings = minCommRatings;
    }

    protected void initializeFunctions(){
        paf = new DefaultAggregationFunction();
        psf = new DefaultSimilarityFunction();        
    }
    
    public Float predict(U user, I item, C context) {
        float prediction = 0;
        float similaritySum = 0;
        int count=0;

        if (eModel.getUserRatingCount(user) > 0 && eModel.getItemRatingCount(item) > 0){
            List<SimilarityDatumIF> neighborsList=nearestNeighborsComputer.getNeighbors(item);
            
            for (SimilarityDatumIF data:neighborsList){
                I neighborKey=(I)data.getKey();
                
                if (model.getPreferences(user, neighborKey) != null) {
                    PreferenceIF<U,I,C> pref = paf.getAggregatedValue(model.getPreferences(user, neighborKey));
                    float rating = pref.getValue();
                    float similarity = psf.getSimilarity(context, data, pref);
                    prediction += ( rating - eModel.getItemMeanRating(neighborKey) ) * similarity;
                    similaritySum += similarity;
                    count++;                    
                }
            }
            if (count > minCommRatings){
                prediction = eModel.getItemMeanRating(item) + ( prediction / similaritySum );
            }
            else {
                prediction = Float.NaN;
            }
        }
        else{
            prediction = Float.NaN;
        }        
        return prediction;
    }

    public List<I> recommend(U u, C c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public I getMostRelevant(U u, I i, I i1, C c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String toString(){
        return "ItemSim_MinCommRating(" + minCommRatings + ")[NeighborsComp:" + nearestNeighborsComputer + "]";
    }
}
