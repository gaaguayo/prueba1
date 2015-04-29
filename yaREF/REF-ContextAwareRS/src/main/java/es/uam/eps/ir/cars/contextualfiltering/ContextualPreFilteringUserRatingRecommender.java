package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.cars.neighborhood.PearsonWeightedSimilarity;
import es.uam.eps.ir.cars.neighborhood.SimilarityComputerIF;
import es.uam.eps.ir.cars.neighborhood.UserNeighborhoodComputer;
import es.uam.eps.ir.cars.recommender.AbstractRecommender;
import es.uam.eps.ir.cars.recommender.GenericUserSimilarityBasedRecommender;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class creates Contextual segments (slices) from a {@link ModelIF}
 * based on the Weekday or Weekend time property.
 *
 * @author Pedro G. Campos <pcampossoto@gmail.com>
 * Creation date: 19-feb-2012
 */
public class ContextualPreFilteringUserRatingRecommender<U,I,C extends ContextIF> extends AbstractRecommender<U,I,C> implements RecommenderIF<U,I,C>,ContextDependentIF{
    private static final Logger logger = Logger.getLogger("ExperimentLog");
    protected Map<ContextualSegmentIF, RecommenderIF<U,I,C>> contextEngineMap;
    protected ContextualSlicerIF<U,I,C> slicer;
    protected int neighbors;
    protected String engineDescriptor="";

    public ContextualPreFilteringUserRatingRecommender(ModelIF<U,I,C> model, ContextualSlicerIF<U,I,C> slicer, int neighbors) {
        this.slicer=slicer;
        this.neighbors=neighbors;
        setModel(model);
    }
    
    public Float predict(U user, I item, C context) {
        final ContextualSegmentIF segment=slicer.getSegment(context);
        final RecommenderIF<U,I,C> engine=contextEngineMap.get(segment);
        float prediction = engine.predict(user, item, context);
        return prediction;
    }    
    

    public final void setModel(ModelIF<U,I,C> model) {
        this.model=model;
        contextEngineMap=new HashMap<ContextualSegmentIF, RecommenderIF<U,I,C>>();
        
        for (ContextualSegmentIF segment:slicer.getSegments()){
            logger.log(Level.INFO, "Building model for segment {0}", segment);
                        
            final ModelIF<U,I,C> contextualData=slicer.getSegmentData(model, segment);
            final SimilarityComputerIF<U,I,C> similarityComputer=new PearsonWeightedSimilarity<U,I,C>();
            final NeighborhoodIF<U,I,C> nearestNeighborsComputer=new UserNeighborhoodComputer(contextualData, neighbors, similarityComputer);
            final RecommenderIF<U,I,C> pEngine= new GenericUserSimilarityBasedRecommender(nearestNeighborsComputer);
//            final RecommenderIF<U,I,C> pEngine= new GenericUserSimilarityBasedRecommender(nearestNeighborsComputer);
            contextEngineMap.put(segment, pEngine);
            engineDescriptor=pEngine.toString();
        }
    }
    
    public ContextualSlicerIF getSlicer() {
        return this.slicer;
    }

    public ModelIF getContextualModel(ContextualSegmentIF context) {
        return contextEngineMap.get(context).getModel();
    }
    
    

    @Override
    public String toString(){
        String s="ContextualPRF("+slicer+"_"+engineDescriptor+")";
        return s;
    }

    public I getMostRelevant(U user, I item1, I item2, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<I> recommend(U user, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
