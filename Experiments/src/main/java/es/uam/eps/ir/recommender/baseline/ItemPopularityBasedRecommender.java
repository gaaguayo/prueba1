package es.uam.eps.ir.recommender.baseline;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.cars.recommender.AbstractRecommender;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import java.util.List;

/**
 *
 * @author pedro
 */
public class ItemPopularityBasedRecommender<U,I,C extends ContextIF> extends AbstractRecommender<U,I,C> implements RecommenderIF<U,I,C>{
    private int maxItemRatingCount = 0;

    public ItemPopularityBasedRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel) {
        this.model = model;
        this.eModel = eModel;
        
        // Computes max item rating count
        for (I item: model.getItems()){
            int ratingCount = eModel.getItemFeedbackRecordsCount(item);
            if (ratingCount > maxItemRatingCount){
                maxItemRatingCount = ratingCount;
            }
        }
        
    }

    public ItemPopularityBasedRecommender(ModelIF<U,I,C> model) {
        this(model, new ContextualModelUtils(model));
    }
    
    public Float predict(U user, I item, C context) {
        // Transforms frequency into rating
        double derivedRating= (double)eModel.getItemFeedbackRecordsCount(item) /(double) maxItemRatingCount;
        derivedRating*=eModel.getMaxRating()-eModel.getMinRating();
        derivedRating+=eModel.getMinRating();
        return (float)derivedRating;
    }

    
    public I getMostRelevant(U user, I item1, I item2, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<I> recommend(U user, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String toString(){
        return "ItemPopularity";
    }
}
