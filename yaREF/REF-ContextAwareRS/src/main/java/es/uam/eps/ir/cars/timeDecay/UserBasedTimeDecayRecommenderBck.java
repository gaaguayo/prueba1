package es.uam.eps.ir.cars.timeDecay;

import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.cars.neighborhood.SimilarityDatumIF;
import es.uam.eps.ir.cars.recommender.AbstractRecommender;
import es.uam.eps.ir.cars.recommender.PreferenceSimilarityFunction;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.PreferenceAggregationFunction;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class UserBasedTimeDecayRecommenderBck<U,I,C extends ContinuousTimeContextIF> extends AbstractRecommender<U,I,C> implements RecommenderIF<U,I,C>{
    protected static final Logger logger = Logger.getLogger("ExperimentLog");
    protected NeighborhoodIF nearestNeighborsComputer;
    protected int T0=200;
    protected final int minCommRatings = 3;
    protected final PreferenceAggregationFunction paf;
    protected final PreferenceSimilarityFunction psf;

    public UserBasedTimeDecayRecommenderBck(NeighborhoodIF nearestNeighborsComputer) {
        this.nearestNeighborsComputer = nearestNeighborsComputer;
        this.model = nearestNeighborsComputer.getModel();
        this.eModel = new ContextualModelUtils<U,I,C>(model);
        paf = new AdhocAggregationFunction();
        psf = new TimeDecaySimilarityFunction(T0);
    }
    
    public UserBasedTimeDecayRecommenderBck(NeighborhoodIF nearestNeighborsComputer, int halfLifeDays) {
        this(nearestNeighborsComputer);
        this.T0 = halfLifeDays;
    }
    
    public Float predict(U user, I item, C context) {
        float prediction = 0;
        float similaritySum = 0;
        int count=0;
        
        if (eModel.getUserRatingCount(user) > 0 && eModel.getItemRatingCount(item) > 0){
            List<SimilarityDatumIF> neighborhood=nearestNeighborsComputer.getNeighbors(user);
            
            for (SimilarityDatumIF data:neighborhood){
                U neighborKey=(U)data.getKey();

                if (model.getPreferences(neighborKey, item) != null) {
                    PreferenceIF<U,I,C> pref = paf.getAggregatedValue(model.getPreferences(neighborKey, item));
                    float rating = pref.getValue();
                    float similarity = psf.getSimilarity(context, data, pref);
                    prediction += ( rating - eModel.getUserMeanRating(neighborKey) ) * similarity;
                    similaritySum += similarity;
                    count++;                                        
                }
            }
            if (count > minCommRatings){
                prediction = eModel.getUserMeanRating(user) + ( prediction / similaritySum );
            }
            else {
                prediction = Float.NaN;
            }   
        }
        else{
            prediction = Float.NaN;
        }
        return (float)prediction;
    }

    public I getMostRelevant(U user, I item1, I item2, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<I> recommend(U user, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString(){
        return "UserSimTimeDecay[T0:" + T0 + "_NeighborsComp:" + nearestNeighborsComputer + "_" + "MCR:" + minCommRatings + "]";
    }
}
