package es.uam.eps.ir.mahout;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 *
 * @author Pedro G. Campos
 */
public class MahoutRecommender<U,I,C extends ContextIF> implements RecommenderIF<U,I,C> {
    
    Recommender mahoutRecommender;
    ContextualModelUtils<U,I,C> utils;

    public MahoutRecommender(Recommender mahoutRecommender, ContextualModelUtils<U,I,C> utils) {
        this.mahoutRecommender = mahoutRecommender;
        this.utils = utils;
    }
    public Float predict(U user, I item, C context) {
        try {
            return mahoutRecommender.estimatePreference(utils.getUserIndex(user), utils.getItemIndex(item));
        } catch (Exception e) {
            return Float.NaN;
        }
    }

    public List<I> recommend(U user, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public I getMostRelevant(U user, I item1, I item2, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ModelIF<U, I, C> getModel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String toString(){
        return mahoutRecommender.toString();
    }
}
