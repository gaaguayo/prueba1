package es.uam.eps.ir.rec.impl;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Pedro G. Campos
 */
public class RandomRecommender<U,I,C extends ContextIF> implements RecommenderIF<U,I,C>{
    protected final float minRating;
    protected final float maxRating;
    protected final Random random;

    public RandomRecommender(ModelIF<U,I,C> model) {
        ContextualModelUtils cmu = new ContextualModelUtils(model);
        this.minRating = cmu.getMinRating();
        this.maxRating = cmu.getMaxRating();
        this.random = new Random(0);
    }

    public Float predict(U user, I item, C context) {
        return minRating + (float)(random.nextFloat() * ((maxRating - minRating) + 1));
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
        return this.getClass().getSimpleName();
    }
    
}
