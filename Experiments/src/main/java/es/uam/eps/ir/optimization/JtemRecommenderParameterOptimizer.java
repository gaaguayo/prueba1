package es.uam.eps.ir.optimization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.cars.bias.ModelBasedRecommender;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.optimization.jtem.NelderMead;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pedro
 */
public class JtemRecommenderParameterOptimizer<U,I,C extends ContextIF> extends RecommenderParameterOptimizer{
    private double bestValue = Double.POSITIVE_INFINITY;
    private int repetitions = 3;

    public JtemRecommenderParameterOptimizer(AbstractObjectiveFunction function, SplitIF split, int features, int bins, int maxIterations) {
        super(function, split, features, bins, maxIterations);
    }

    @Override
    public ModelBasedRecommender<U,I,C> getOptimizedRecommender(){
        double[] startPoint = function.getStartPoint();
        double[] bestPoint = Arrays.copyOf(startPoint, startPoint.length);
//        List<Double> stepSizes = Arrays.asList(0.05, 0.5, 0.1, 1.0, 5.0, 10.0, 50.0);
        List<Double> stepSizes = Arrays.asList(50.0, 10.0, 5.0, 1.0, 0.5, 0.1, 0.05);
        
        for (int i = 0; i < repetitions; i++){
            System.out.println("Optimization repetition " + (i+1));
            for (Double stepSize: stepSizes){
                double value = NelderMead.search(startPoint, Double.MIN_VALUE, function, stepSize);
                if (value < bestValue){
                    bestValue = value;
                    bestPoint = Arrays.copyOf(startPoint, startPoint.length);
                }
            }
        }
        totalEvaluations = function.executedIterations;
        return function.getRecommender(split, new ContextualModelUtils(split.getTrainingSet()), function.getBestIteration(), bestPoint, features, bins);
    }
    
    @Override
    public String toString(){
        return "JtemNelderMeadSimplex[evaluations:"+totalEvaluations+", bestValue:+" + bestValue + "]";
    }
}
