package es.uam.eps.ir.cars.recommender;

import es.uam.eps.ir.cars.neighborhood.CosineSimilarity;
import es.uam.eps.ir.cars.neighborhood.ItemNeighborhoodComputer;
import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.cars.neighborhood.PearsonWeightedSimilarity;
import es.uam.eps.ir.cars.neighborhood.SimilarityComputerIF;
import es.uam.eps.ir.cars.neighborhood.UserNeighborhoodComputer;
import es.uam.eps.ir.cars.recommender.RecommenderBuilderIF.RECOMMENDER_METHOD;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author pedro
 */
public class NeighborBasedRecommenderBuilder<U,I,C extends ContextIF> implements RecommenderBuilderIF<U,I,C>{
    protected ModelIF<U,I,C> model;
    protected int _neighbors = 100; // default value
    protected int _minCommonRatingsForPrediction = 3;
    
    public NeighborBasedRecommenderBuilder(ModelIF<U,I,C> model){
        this.model=model;
    }
    
    public NeighborBasedRecommenderBuilder<U,I,C> neighbors(int _neighbors){
        this._neighbors = _neighbors;
        return this;
    }
    
    public NeighborBasedRecommenderBuilder<U,I,C> minCommonRatingsForPrediction(int _minCommonRatingsForPrediction){
        this._minCommonRatingsForPrediction = _minCommonRatingsForPrediction;
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
            case kNN_PearsonWeightSimilarity_UserBased:
                simComputer = new PearsonWeightedSimilarity<U, I, C>();
                neighborhoodComputer = new UserNeighborhoodComputer<U, I, C>(model, _neighbors, simComputer);
                recomm = new GenericUserSimilarityBasedRecommender<U,I,C>(neighborhoodComputer, _minCommonRatingsForPrediction);
                break;
            case kNN_PearsonWeightSimilarity_ItemBased:
                simComputer = new PearsonWeightedSimilarity<U, I, C>();
                neighborhoodComputer = new ItemNeighborhoodComputer<U, I, C>(model, _neighbors, simComputer);
                recomm = new GenericItemSimilarityBasedRecommender<U,I,C>(neighborhoodComputer, _minCommonRatingsForPrediction);
                break;
            case kNN_CosineSimilarity_UserBased:
                simComputer = new CosineSimilarity<U, I, C>();
                neighborhoodComputer = new UserNeighborhoodComputer<U, I, C>(model, _neighbors, simComputer);
                recomm = new GenericUserSimilarityBasedRecommender<U,I,C>(neighborhoodComputer, _minCommonRatingsForPrediction);
                break;
            case kNN_CosineSimilarity_ItemBased:
                simComputer = new CosineSimilarity<U, I, C>();
                neighborhoodComputer = new ItemNeighborhoodComputer<U, I, C>(model, _neighbors, simComputer);
                recomm = new GenericItemSimilarityBasedRecommender<U,I,C>(neighborhoodComputer, _minCommonRatingsForPrediction);
                break;
            case Raw_CosineSimilarity_UserBased:
                simComputer = new CosineSimilarity<U, I, C>();
                neighborhoodComputer = new UserNeighborhoodComputer<U, I, C>(model, _neighbors, simComputer);
                recomm = new RawUserSimilarityBasedRecommender<U,I,C>(neighborhoodComputer);
                break;
            case Raw_CosineSimilarity_ItemBased:
                simComputer = new CosineSimilarity<U, I, C>();
                neighborhoodComputer = new ItemNeighborhoodComputer<U, I, C>(model, _neighbors, simComputer);
                recomm = new RawItemSimilarityBasedRecommender<U,I,C>(neighborhoodComputer);
                break;
        }
        return recomm;
    }
}
