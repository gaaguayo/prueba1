package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.cars.recommender.AbstractRecommender;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.mahout.DataModelFactory;
import es.uam.eps.ir.mahout.MahoutRecommender;
import es.uam.eps.ir.mahout.MahoutRecommenderFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ExpectationMaximizationSVDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

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
public class ContextualPostFilteringMahoutSVDRecommender_Neighbors<U,I,C extends ContextIF> extends AbstractRecommender<U,I,C> implements RecommenderIF<U,I,C>,ContextDependentIF{
    private static final Logger logger = Logger.getLogger("ExperimentLog");
    protected Map<ContextualSegmentIF, UserNeighborhood> contextNeighborhoodMap;
    protected Map<ContextualSegmentIF, DataModel> contextDataModelMap;
    RecommenderIF<U,I,C> engine;
    protected ContextualSlicerIF<U,I,C> slicer;
    protected int neighbors;
    protected double thresholdProbability=0.1;
    private int numFeatures = 60;
    private int numIterations = 30;

    public ContextualPostFilteringMahoutSVDRecommender_Neighbors(ModelIF<U,I,C> model, ContextualSlicerIF<U,I,C> slicer, int neighbors, int numFeatures, int numIterations) {
        this.slicer=slicer;
        this.neighbors=neighbors;
        this.numFeatures = numFeatures;
        this.numIterations = numIterations;
        setModel(model);
    }
    
    public Float predict(U user, I item, C context) {
        float prediction = engine.predict(user, item, context);
        ContextualSegmentIF segment=slicer.getSegment(context);
        
        int count=0;
        
        UserNeighborhood neighborsComputer = this.contextNeighborhoodMap.get(segment);
        DataModel contextDataModel = this.contextDataModelMap.get(segment);
        long neighborhood[] = null;
        long userIndex = ((Integer)eModel.getUserIndex(user)).longValue();
        long itemIndex = ((Integer)eModel.getItemIndex(item)).longValue();
        if (userIndex == -1 || itemIndex == -1 || neighborsComputer == null){
//            return Float.NaN;
            return prediction;
        }
//        List<SimilarityDatumIF> neighborhood=neighborsComputer.getUserNeighborhood(count);
        try{
            neighborhood = neighborsComputer.getUserNeighborhood(userIndex);
            for (long neighbor:neighborhood){
                if (contextDataModel.getPreferenceValue(neighbor, itemIndex) != null){
                    count++;
                }
            }
        } catch (TasteException ex) {
            Logger.getLogger(MahoutRecommenderFactory.class.getName()).log(Level.SEVERE, null, ex);
            Thread.dumpStack();
            System.exit(1);
        }
        
        double prob=count/(double)neighborhood.length;
        
        return contextualizedPrediction(prediction, prob);
    }    
    
    public void setModel(ModelIF<U,I,C> model) {
        this.model = model;
        this.eModel = new ContextualModelUtils(model);
        
        DataModelFactory dmf = new DataModelFactory();
        DataModel dataModel = dmf.getDataModel(model, eModel);
        
        try {
            Factorizer factorizer = new ExpectationMaximizationSVDFactorizer(dataModel, numFeatures, numIterations);
            Recommender recommender = new SVDRecommender(dataModel, factorizer);
                
            MahoutRecommender<U,I,C> mRecommender = new MahoutRecommender<U,I,C>(recommender, eModel);
            engine = mRecommender;
        } catch (TasteException ex) {
            Logger.getLogger(MahoutRecommenderFactory.class.getName()).log(Level.SEVERE, null, ex);
            Thread.dumpStack();
            System.exit(1);
        }
                
        contextNeighborhoodMap=new HashMap();
        contextDataModelMap=new HashMap();
        
        for (ContextualSegmentIF segment:slicer.getSegments()){
            logger.log(Level.INFO, "Building model for segment {0}", segment);
            
            ModelIF<U,I,C> contextualData=slicer.getSegmentData(model, segment);
            DataModel contextDataModel = dmf.getDataModel(contextualData, eModel);
            try {
                UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(contextDataModel);
                UserNeighborhood contextNeighborhood = new NearestNUserNeighborhood(neighbors, userSimilarity, contextDataModel);
                contextNeighborhoodMap.put(segment, contextNeighborhood);
                contextDataModelMap.put(segment, contextDataModel);
            } catch (TasteException ex) {
                Logger.getLogger(MahoutRecommenderFactory.class.getName()).log(Level.SEVERE, null, ex);
                Thread.dumpStack();
                System.exit(1);
            }
            
 
        }
    }
    
    public ContextualSlicerIF getSlicer() {
        return this.slicer;
    }

    public ModelIF getContextualModel(ContextualSegmentIF context) {
        throw new UnsupportedOperationException("Not supported yet.");
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
