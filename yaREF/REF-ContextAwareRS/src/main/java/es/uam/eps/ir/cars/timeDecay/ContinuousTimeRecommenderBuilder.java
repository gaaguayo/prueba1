package es.uam.eps.ir.cars.timeDecay;

import es.uam.eps.ir.cars.neighborhood.ItemNeighborhoodComputer;
import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.core.similarity.PearsonWeightedSimilarity;
import es.uam.eps.ir.core.similarity.SimilarityComputerIF;
import es.uam.eps.ir.cars.neighborhood.UserNeighborhoodComputer;
import es.uam.eps.ir.cars.recommender.NeighborBasedRecommenderBuilder;
import es.uam.eps.ir.cars.recommender.RecommenderBuilderIF;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author pedro
 */
public class ContinuousTimeRecommenderBuilder<U,I,C extends ContinuousTimeContextIF> implements RecommenderBuilderIF<U,I,C>{
    protected ModelIF<U,I,C> model;
    protected int _neighbors = 100; // default value
    protected int _minCommonRatingsForPrediction = 3;
    protected int _halfLifeDays = 90; // half life measured in days
    protected double _halfLifeProp = 0.5;
    protected long _trTimespan;

    public ContinuousTimeRecommenderBuilder(ModelIF<U,I,C> model){
        this.model=model;
    }
    
    public ContinuousTimeRecommenderBuilder<U,I,C> neighbors(int _neighbors){
        this._neighbors = _neighbors;
        return this;
    }

    public ContinuousTimeRecommenderBuilder<U,I,C> minCommonRatingsForPrediction(int _minCommonRatingsForPrediction){
        this._minCommonRatingsForPrediction = _minCommonRatingsForPrediction;
        return this;
    }
    
    public ContinuousTimeRecommenderBuilder<U,I,C> halfLifeDays(int days){
        this._halfLifeDays = days;
        return this;
    }

    public ContinuousTimeRecommenderBuilder<U,I,C> halfLifeProp(double halfLifeProp){
        this._halfLifeProp = halfLifeProp;
        return this;
    }

    public ContinuousTimeRecommenderBuilder<U,I,C> trTimespan(long trTimespan){
        this._trTimespan = trTimespan;
        return this;
    }

    public RecommenderIF<U,I,C> buildRecommender(RECOMMENDER_METHOD recommenderMethod){
        return instantiateRecommender(recommenderMethod);
    }
    
    protected RecommenderIF<U,I,C> instantiateRecommender(RECOMMENDER_METHOD recommenderMethod){
        SimilarityComputerIF<U, I, C> simComputer;
        NeighborhoodIF<U, I, C> neighborhoodComputer;

        RecommenderIF<U, I, C> recomm=null;
        switch(recommenderMethod){
            case TimeDecay_UserBased:
                simComputer = new PearsonWeightedSimilarity<U, I, C>();
                neighborhoodComputer = new UserNeighborhoodComputer<U, I, C>(model, _neighbors, simComputer);
                recomm = new UserBasedTimeDecayRecommender<U,I,C>(neighborhoodComputer, _halfLifeDays, _minCommonRatingsForPrediction);
                break;
            case TimeDecay_UserBased_TrProp:
                simComputer = new PearsonWeightedSimilarity<U, I, C>();
                neighborhoodComputer = new UserNeighborhoodComputer<U, I, C>(model, _neighbors, simComputer);
                recomm = new UserBasedTimeDecayPropRecommender<U,I,C>(neighborhoodComputer, _halfLifeProp, _trTimespan, _minCommonRatingsForPrediction);
                break;
            case TimeDecay_ItemBased:
                simComputer = new PearsonWeightedSimilarity<U, I, C>();
                neighborhoodComputer = new ItemNeighborhoodComputer<U, I, C>(model, _neighbors, simComputer);
                recomm = new ItemBasedTimeDecayRecommender<U,I,C>(neighborhoodComputer, _minCommonRatingsForPrediction);
                break;
        }
        return recomm;
    }
    
}
