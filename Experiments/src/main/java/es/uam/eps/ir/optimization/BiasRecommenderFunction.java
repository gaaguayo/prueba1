package es.uam.eps.ir.optimization;

import es.uam.eps.ir.cars.bias.ModelBasedRecommender;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;

/**
 *
 * @author pedro
 */
public class BiasRecommenderFunction<U,I,C extends ContextIF> extends AbstractObjectiveFunction<U,I,C> {
    public BiasRecommenderFunction(SplitIF<U, I, C> split, ContextualModelUtils<U, I, C> eModel, int iterations) {
        super(split, eModel, iterations, 0, 0);
    }

    @Override
    protected ModelBasedRecommender<U, I, C> getRecommender(SplitIF<U,I,C> theSplit, ContextualModelUtils<U,I,C> theEModel, int iterations, double[] p, int features, int bins) {
         // p[0] learnRate
         // p[1] lambda
        return new ModelBasedRecommender(theSplit.getTrainingSet(), theEModel, 0, iterations, p[0], p[1]);
    }

    @Override
    protected double[] getStartPoint() {
         // p[0] learnRate
         // p[1] lambda
        double[] startPoint={0.005, 0.02};
        return startPoint;
    }    
}
