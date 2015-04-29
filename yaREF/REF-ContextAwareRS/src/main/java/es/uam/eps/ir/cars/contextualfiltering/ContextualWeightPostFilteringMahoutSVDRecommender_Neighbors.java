package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.logging.Logger;

/**
 * This class implements a {@link TimeAwarePredictionEngine} in the form of a 
 * Contextual post Filtering approach.
 * 
 * @see <a href="http://dl.acm.org/citation.cfm?doid=1055709.1055714">Adomavicius et al., 2005. Incorporating Contextual Information in Recommender Systems Using a Multidimensional Approach. ACM TOIS 23(1), pp. 103-145</a>
 * 
 * @see <a href="http://dl.acm.org/citation.cfm?doid=1639714.1639764">Panniello et al., 2009. Experimental Comparison of Pre- vs. Post-Filtering Approaches in Context-Aware Recommender Systems. RecSys'09, pp. 265-268.

 * 
 * @author Pedro G. Campos <pcampossoto@gmail.com>
 * Creation date: 19-feb-2012
 */
public class ContextualWeightPostFilteringMahoutSVDRecommender_Neighbors<U,I,C extends ContextIF> extends ContextualPostFilteringMahoutSVDRecommender_Neighbors{
    private static final Logger logger = Logger.getLogger("ExperimentLog");

    public ContextualWeightPostFilteringMahoutSVDRecommender_Neighbors(ModelIF<U,I,C> model, ContextualSlicerIF<U,I,C> slicer, int neighbors, int numFeatures, int numIterations) {
        super(model, slicer, neighbors, numFeatures, numIterations);
    }
    
    @Override
    protected float contextualizedPrediction(float prediction, double P){
        return (prediction * new Double(P).floatValue());
    }
    
    @Override
    public String toString(){
        String s="ContextualWPOF"+"_("+slicer+"_"+engine+")";
        return s;
    }

}
