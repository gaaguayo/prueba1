package es.uam.eps.ir.cars.timeDecay;

import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.cars.recommender.GenericItemSimilarityBasedRecommender;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author pedro
 */
public class ItemBasedTimeDecayRecommender<U,I,C extends ContinuousTimeContextIF> extends GenericItemSimilarityBasedRecommender<U,I,C> implements RecommenderIF<U,I,C>{
    protected int T0=10;

    public ItemBasedTimeDecayRecommender(NeighborhoodIF nearestNeighborsComputer, int minCommonRatings) {
        super(nearestNeighborsComputer, minCommonRatings);
    }
    
    @Override
     protected void initializeFunctions(){
        paf = new AdhocAggregationFunction();
        psf = new TimeDecaySimilarityFunction(T0);
    }
     
   public ItemBasedTimeDecayRecommender(NeighborhoodIF nearestNeighborsComputer, int halfLifeDays, int minCommonRatings) {
        this(nearestNeighborsComputer, minCommonRatings);
        this.T0 = halfLifeDays;
        psf = new TimeDecaySimilarityFunction(T0);
    }
    
    @Override
    public String toString(){
        return "ItemSimTimeDecay[T0:" + T0 + "_NeighborsComp:" + nearestNeighborsComputer + "_" + "MCR:" + minCommRatings + "]";
    }
}
