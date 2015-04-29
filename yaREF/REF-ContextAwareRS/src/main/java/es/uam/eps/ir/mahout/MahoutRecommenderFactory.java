package es.uam.eps.ir.mahout;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.knn.ConjugateGradientOptimizer;
import org.apache.mahout.cf.taste.impl.recommender.knn.KnnItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ExpectationMaximizationSVDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 *
 * @author Pedro G. Campos
 */
public class MahoutRecommenderFactory<U,I,C extends ContextIF> {
    private int numFeatures = 60;
    private int numIterations = 30;
    private int neighbors = 200;
    
    public enum Type{
        SVDRecommender,
        kNN_UB,
        kNN_IB
    }

    public MahoutRecommenderFactory() {
    }
    
    public MahoutRecommenderFactory<U,I,C> neighbors(int neighbors){
        this.neighbors = neighbors;
        return this;
    }
    
    public MahoutRecommenderFactory<U,I,C> factors(int factors){
        this.numFeatures = factors;
        return this;
    }
    
    public MahoutRecommenderFactory<U,I,C> iterations(int iterations){
        this.numIterations = iterations;
        return this;
    }

    public RecommenderIF<U,I,C> getRecommender(Type type, ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> utils){

        Recommender mahoutRecommender = null;
        DataModelFactory dmf = new DataModelFactory();
        DataModel dataModel = dmf.getDataModel(model, utils);
        
        switch(type){
            case SVDRecommender:
                mahoutRecommender = getSVDRecommender(dataModel);
                break;
            case kNN_UB:
                mahoutRecommender = getKNN_UBRecommender(dataModel);
                break;
            case kNN_IB:
                mahoutRecommender = getKNN_IBRecommender(dataModel);
                break;
        }
        
        MahoutRecommender<U,I,C> recommender = new MahoutRecommender<U,I,C>(mahoutRecommender, utils);
        return recommender;
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    private Recommender getSVDRecommender(DataModel model){
        try {
            Factorizer factorizer = new ExpectationMaximizationSVDFactorizer(model, numFeatures, numIterations);
            Recommender recommender = new SVDRecommender(model, factorizer);
            return recommender;
        } catch (TasteException ex) {
            Logger.getLogger(MahoutRecommenderFactory.class.getName()).log(Level.SEVERE, null, ex);
            Thread.dumpStack();
            System.exit(1);
        }
        return null;
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    private Recommender getKNN_UBRecommender(DataModel model){
        try {
            UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(neighbors, userSimilarity, model);
            Recommender recommender =
                      new GenericUserBasedRecommender(model, neighborhood, userSimilarity);
            Recommender cachingRecommender = new CachingRecommender(recommender);            
            return cachingRecommender;
        } catch (TasteException ex) {
            Logger.getLogger(MahoutRecommenderFactory.class.getName()).log(Level.SEVERE, null, ex);
            Thread.dumpStack();
            System.exit(1);
        }
        return null;
    }    

    @SuppressWarnings("CallToPrintStackTrace")
    private Recommender getKNN_IBRecommender(DataModel model){
        try {
            ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(model);
//            GenericItemSimilarity genericItemSimilarity = new GenericItemSimilarity(itemSimilarity, model);
            Recommender recommender =
                      new KnnItemBasedRecommender(model, itemSimilarity, new ConjugateGradientOptimizer(), neighbors);
            Recommender cachingRecommender = new CachingRecommender(recommender);            
            return cachingRecommender;
        } catch (TasteException ex) {
            Logger.getLogger(MahoutRecommenderFactory.class.getName()).log(Level.SEVERE, null, ex);
            Thread.dumpStack();
            System.exit(1);
        }
        return null;
    }    
}
