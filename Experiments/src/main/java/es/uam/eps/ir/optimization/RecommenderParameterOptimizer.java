package es.uam.eps.ir.optimization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.cars.bias.ModelBasedRecommender;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;
import java.util.Arrays;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.direct.AbstractSimplex;
import org.apache.commons.math3.optimization.direct.NelderMeadSimplex;
import org.apache.commons.math3.optimization.direct.SimplexOptimizer;

/**
 *
 * @author pedro
 */
public class RecommenderParameterOptimizer<U,I,C extends ContextIF> {
    protected AbstractObjectiveFunction function;
    protected SplitIF<U,I,C> split;
    protected int features;
    protected int bins;
    protected final int maxIterations;
    
    protected int totalEvaluations = 0;;
    protected final int minEvaluations = 50;

    public RecommenderParameterOptimizer(AbstractObjectiveFunction function, SplitIF<U, I, C> split, int features, int bins, int maxIterations) {
        this.function = function;
        this.split = split;
        this.features = features;
        this.bins = bins;
        this.maxIterations = maxIterations;
    }

    public ModelBasedRecommender<U,I,C> getOptimizedRecommender(){
        double[] startPoint = function.getStartPoint();
        double factor = 1.0;
        AbstractSimplex simplex;
        SimplexOptimizer optimizer;
        PointValuePair bestP = new PointValuePair(startPoint, Double.POSITIVE_INFINITY);
        
        // general seach
        do {
            double[] steps = new double[startPoint.length];
            int i = 0;
            for (double point:startPoint){
                steps[i++]=point*factor;
            }
            simplex = new NelderMeadSimplex(steps);
            optimizer = new SimplexOptimizer();
            optimizer.setSimplex(simplex);
            PointValuePair p;
            p = optimizer.optimize(maxIterations, function, GoalType.MINIMIZE, startPoint);
            if (p.getValue() < bestP.getValue()){
                bestP = p;
            }
            totalEvaluations += optimizer.getEvaluations();
            factor *= 10.0;
        } while (Arrays.equals(startPoint, bestP.getPoint()) &&  totalEvaluations < minEvaluations/2);
//         fixes learning rates
        factor=0.1;
        do {
            double[] steps = new double[startPoint.length];
            int i = 0;
            for (double point:startPoint){
                steps[i]=point*factor;
                if (i%2 == 0){
                    steps[i]=point*0.01;
                }
                i++;
            }
            simplex = new NelderMeadSimplex(steps);
            optimizer = new SimplexOptimizer();
            optimizer.setSimplex(simplex);
            PointValuePair p;
            p = optimizer.optimize(maxIterations, function, GoalType.MINIMIZE, startPoint);
            if (p.getValue() < bestP.getValue()){
                bestP = p;
            }
            totalEvaluations += optimizer.getEvaluations();
            factor *= 10.0;
        } while (totalEvaluations < minEvaluations);
        
                
        return function.getRecommender(split, new ContextualModelUtils(split.getTrainingSet()), function.getBestIteration(), bestP.getPoint(), features, bins);
    }
    
    @Override
    public String toString(){
        return "NelderMeadSimplex[evaluations:"+totalEvaluations+"]";
    }
}
