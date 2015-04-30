package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.core.similarity.PearsonWeightedSimilarity;
import es.uam.eps.ir.core.similarity.SimilarityComputerIF;
import es.uam.eps.ir.cars.neighborhood.SimilarityDatumIF;
import es.uam.eps.ir.cars.neighborhood.UserNeighborhoodComputer;
import es.uam.eps.ir.cars.recommender.AbstractRecommender;
import es.uam.eps.ir.cars.recommender.GenericUserSimilarityBasedRecommender;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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
public class ContextualPostFilteringUserRatingRecommender<U,I,C extends ContextIF> extends AbstractRecommender<U,I,C> implements RecommenderIF<U,I,C>,ContextDependentIF{
    private static final Logger logger = Logger.getLogger("ExperimentLog");
    protected Map<ContextualSegmentIF, NeighborhoodIF<U,I,C>> contextNeighborhoodMap;
    RecommenderIF<U,I,C> engine;
    protected ContextualSlicerIF<U,I,C> slicer;
    protected int neighbors;
    protected double thresholdProbability=0.1;

    public ContextualPostFilteringUserRatingRecommender(ModelIF<U,I,C> model, ContextualSlicerIF<U,I,C> slicer, int neighbors) {
        this.slicer=slicer;
        this.neighbors=neighbors;
        setModel(model);
    }
    
    public Float predict(U user, I item, C context) {
        float prediction = engine.predict(user, item, context);
        ContextualSegmentIF segment=slicer.getSegment(context);
        
        int count=0;
        
        NeighborhoodIF<U,I,C> neighborsComputer=this.contextNeighborhoodMap.get(segment);
        List<SimilarityDatumIF> neighborhood=neighborsComputer.getNeighbors(user);
        for (SimilarityDatumIF neighbor:neighborhood){
            U neighborID=(U)neighbor.getKey();
            if (neighborsComputer.getModel().getPreferences(neighborID, item) != null){
                count++;
            }
        }
        
        double prob=count/(double)neighborhood.size();
        
        return contextualizedPrediction(prediction, prob);
    }    
    
    public void setModel(ModelIF<U,I,C> model) {
        this.model = model;
        this.eModel = new ContextualModelUtils(model);
        SimilarityComputerIF<U,I,C> similarityComputer=new PearsonWeightedSimilarity<U,I,C>();
        NeighborhoodIF<U,I,C> nearestNeighborsComputer=new UserNeighborhoodComputer(model, neighbors, similarityComputer);
        engine= new GenericUserSimilarityBasedRecommender(nearestNeighborsComputer);
//        engine= new GenericUserSimilarityBasedRecommender(nearestNeighborsComputer);
        
        contextNeighborhoodMap=new HashMap();
        
        for (ContextualSegmentIF segment:slicer.getSegments()){
            logger.log(Level.INFO, "Building model for segment {0}", segment);
            
            ModelIF<U,I,C> contextualData=slicer.getSegmentData(model, segment);
            SimilarityComputerIF<U,I,C> contextualSimilarityComputer=new PearsonWeightedSimilarity<U,I,C>();
            NeighborhoodIF<U,I,C> contextualNearestNeighborsComputer=new UserNeighborhoodComputer(contextualData, neighbors, contextualSimilarityComputer);
            contextNeighborhoodMap.put(segment, contextualNearestNeighborsComputer);
        }
    }
    
    public ContextualSlicerIF getSlicer() {
        return this.slicer;
    }

    public ModelIF getContextualModel(ContextualSegmentIF context) {
        return contextNeighborhoodMap.get(context).getModel();
    }
    
    @Override
    public String toString(){
        String s="ContextualPOF_"+thresholdProbability+"_("+slicer+"_"+engine+")";
        return s;
    }

    public I getMostRelevant(U user, I item1, I item2, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<I> recommend(U user, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected float contextualizedPrediction(float prediction, double P){
        if (P < thresholdProbability){
            return (prediction - new Float(0.5));
        }
        return prediction;
    }
    
}
