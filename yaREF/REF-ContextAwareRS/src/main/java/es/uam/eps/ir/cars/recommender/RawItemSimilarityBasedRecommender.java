package es.uam.eps.ir.cars.recommender;

import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.cars.neighborhood.SimilarityDatumIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.List;

/**
 *
 * @author pedro
 */
public class RawItemSimilarityBasedRecommender<U,I,C extends ContextIF> extends AbstractRecommender<U,I,C> implements RecommenderIF<U,I,C>{
    protected NeighborhoodIF nearestNeighborsComputer;

    public RawItemSimilarityBasedRecommender(NeighborhoodIF nearestNeighborsComputer) {
        this.nearestNeighborsComputer = nearestNeighborsComputer;
        this.model = nearestNeighborsComputer.getModel();
        this.eModel = new ContextualModelUtils<U,I,C>(model);
    }

    public Float predict(U user, I item, C context) {
        float prediction = 0;
        int count=0;
        
        if (eModel.getUserFeedbackRecordsCount(user) > 0 && eModel.getItemFeedbackRecordsCount(item) > 0){
            List<SimilarityDatumIF> neighborsList=nearestNeighborsComputer.getNeighbors(item);

            for (SimilarityDatumIF data:neighborsList){
//                I neighborKey=(I)data.getKey();
                float similarity=data.getSimilarity();
//                if (model.getPreferences(user, neighborKey) != null) {
                    prediction += similarity;
                    count++;                    
//                }
                    
            }
            prediction = ( prediction / count );
        }
        else{
            prediction = Float.NaN;
        }
        return prediction;
    }

    public List<I> recommend(U u, C c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public I getMostRelevant(U u, I i, I i1, C c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String toString(){
        return "RawItemSim[NeighborsComp:" + nearestNeighborsComputer + "]";
    }
}
