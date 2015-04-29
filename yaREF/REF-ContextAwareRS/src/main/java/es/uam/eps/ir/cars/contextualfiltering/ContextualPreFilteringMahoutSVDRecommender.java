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
 * This class creates Contextual segments (slices) from a {@link ModelIF}
 * based on the Weekday or Weekend time property.
 *
 * @author Pedro G. Campos <pcampossoto@gmail.com>
 * Creation date: 19-feb-2012
 */
public class ContextualPreFilteringMahoutSVDRecommender<U,I,C extends ContextIF> extends AbstractRecommender<U,I,C> implements RecommenderIF<U,I,C>,ContextDependentIF{
    private static final Logger logger = Logger.getLogger("ExperimentLog");
    protected Map<ContextualSegmentIF, RecommenderIF<U,I,C>> contextEngineMap;
    protected ContextualSlicerIF<U,I,C> slicer;
    private int numFeatures = 60;
    private int numIterations = 30;
    protected String engineDescriptor="";

    public ContextualPreFilteringMahoutSVDRecommender(ModelIF<U,I,C> model, ContextualSlicerIF<U,I,C> slicer, int numFeatures, int numIterations) {
        this.slicer = slicer;
        this.numFeatures = numFeatures;
        this.numIterations = numIterations;
        setModel(model);
    }
    
    public Float predict(U user, I item, C context) {
        final ContextualSegmentIF segment=slicer.getSegment(context);
        final RecommenderIF<U,I,C> engine=contextEngineMap.get(segment);
        float prediction = Float.NaN;
        if (engine != null){
            prediction = engine.predict(user, item, context);
        }
        return prediction;
    }    
    

    public final void setModel(ModelIF<U,I,C> model) {
        this.model=model;
        contextEngineMap=new HashMap<ContextualSegmentIF, RecommenderIF<U,I,C>>();
        
        for (ContextualSegmentIF segment:slicer.getSegments()){
            logger.log(Level.INFO, "Building model for segment {0}", segment);
                        
            final ModelIF<U,I,C> contextualData=slicer.getSegmentData(model, segment);
            
            if (contextualData.getUsers().isEmpty()){
                continue;
            }
            
            ContextualModelUtils<U,I,C> utils = new ContextualModelUtils<U,I,C>(contextualData);
            DataModelFactory dmf = new DataModelFactory();
            DataModel dataModel = dmf.getDataModel(contextualData, utils);
            
            try {
                Factorizer factorizer = new ExpectationMaximizationSVDFactorizer(dataModel, numFeatures, numIterations);
                Recommender recommender = new SVDRecommender(dataModel, factorizer);
                
                MahoutRecommender<U,I,C> mRecommender = new MahoutRecommender<U,I,C>(recommender, utils);
                contextEngineMap.put(segment, mRecommender);
                engineDescriptor=mRecommender.toString();
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
