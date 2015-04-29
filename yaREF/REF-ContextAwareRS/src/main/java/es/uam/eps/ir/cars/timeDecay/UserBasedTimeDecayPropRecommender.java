package es.uam.eps.ir.cars.timeDecay;

import es.uam.eps.ir.cars.model.ContinuousTimeUtils;
import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;

/**
 *
 * @author Pedro G. Campos
 */
public class UserBasedTimeDecayPropRecommender<U,I,C extends ContinuousTimeContextIF> extends UserBasedTimeDecayRecommender<U,I,C> {
    protected double TrProp=0.5;

    public UserBasedTimeDecayPropRecommender(NeighborhoodIF nearestNeighborsComputer, double halfLife, long trTimespan, int minCommonRatings) {
        super(nearestNeighborsComputer, (int)(ContinuousTimeUtils.toDays(trTimespan) * halfLife), minCommonRatings);
        TrProp = halfLife;
    }
    
    @Override
    public String toString(){
        return "UserSimTimeDecay[Prop:" + TrProp + "_T0:" +  T0 + "_NeighborsComp:" + nearestNeighborsComputer + "_" + "MCR:" + minCommRatings + "]";
    }   
    
}
