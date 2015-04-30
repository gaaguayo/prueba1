package es.uam.eps.ir.cars.timeDecay;

import es.uam.eps.ir.cars.dynamicSelection.NeighboursRatingsBasedSelector;
import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.core.similarity.PearsonWeightedSimilarity;
import es.uam.eps.ir.core.similarity.SimilarityComputerIF;
import es.uam.eps.ir.cars.neighborhood.UserNeighborhoodComputer;
import es.uam.eps.ir.cars.recommender.GenericUserSimilarityBasedRecommender;
import es.uam.eps.ir.cars.recommender.RecommenderBuilderIF;
import es.uam.eps.ir.cars.recommender.RecommenderBuilderIF.RECOMMENDER_METHOD;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author pedro
 */
public class DynamicSelectorBuilder<U,I,C extends ContinuousTimeContextIF> implements RecommenderBuilderIF<U,I,C>{
    
    protected ModelIF<U,I,C> model;
    protected int _neighbors = 100; // default value
    protected int _minCommonRatingsForPrediction = 3;

    public DynamicSelectorBuilder(ModelIF<U,I,C> model){
        this.model=model;
    }
    
    public DynamicSelectorBuilder<U,I,C> neighbors(int _neighbors){
        this._neighbors=_neighbors;
        return this;
    }
    
    public DynamicSelectorBuilder<U,I,C> minCommonRatingsForPrediction(int _minCommonRatingsForPrediction){
        this._minCommonRatingsForPrediction = _minCommonRatingsForPrediction;
        return this;
    }
    
    public RecommenderIF<U, I, C> buildRecommender(RECOMMENDER_METHOD recommenderMethod) {
        return instantiateRecommender();
    }
    
    protected RecommenderIF<U,I,C> instantiateRecommender(){
        SimilarityComputerIF<U, I, C> simComputer;
        NeighborhoodIF<U, I, C> neighborhoodComputer;
        simComputer = new PearsonWeightedSimilarity<U, I, C>();
        neighborhoodComputer = new UserNeighborhoodComputer<U, I, C>(model, _neighbors, simComputer);

        RecommenderIF<U,I,C> knn = new GenericUserSimilarityBasedRecommender<U,I,C>(neighborhoodComputer);
        RecommenderIF<U,I,C> timeDecay = new UserBasedTimeDecayRecommender<U,I,C>(neighborhoodComputer, _minCommonRatingsForPrediction);
        RecommenderIF<U,I,C> dynamic = new NeighboursRatingsBasedSelector(neighborhoodComputer, knn, timeDecay);
        return dynamic;
    }
}
