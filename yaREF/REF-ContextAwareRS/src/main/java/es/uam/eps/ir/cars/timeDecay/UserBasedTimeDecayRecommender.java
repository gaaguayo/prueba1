package es.uam.eps.ir.cars.timeDecay;

import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.cars.recommender.GenericUserSimilarityBasedRecommender;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author pedro
 */
public class UserBasedTimeDecayRecommender<U,I,C extends ContinuousTimeContextIF> extends GenericUserSimilarityBasedRecommender<U,I,C> implements RecommenderIF<U,I,C>{
    protected int T0=200;

    public UserBasedTimeDecayRecommender(NeighborhoodIF nearestNeighborsComputer, int minCommonRatings) {
        super(nearestNeighborsComputer, minCommonRatings);
    }
    
    @Override
     protected void initializeFunctions(){
        paf = new AdhocAggregationFunction();
        psf = new TimeDecaySimilarityFunction(T0);
    }
     
   public UserBasedTimeDecayRecommender(NeighborhoodIF nearestNeighborsComputer, int halfLifeDays, int minCommonRatings) {
        this(nearestNeighborsComputer, minCommonRatings);
        this.T0 = halfLifeDays;
        psf = new TimeDecaySimilarityFunction(T0);
    }
    
    @Override
    public String toString(){
        return "UserSimTimeDecay[T0:" + T0 + "_NeighborsComp:" + nearestNeighborsComputer + "_" + "MCR:" + minCommRatings + "]";
    }
}
