package es.uam.eps.ir.nonpersonalized;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author Pedro G. Campos
 */
public class NonPersonalizedPrediction<U,I,C extends ContextIF> {
    
    public enum Type{
        ItemMean,
        ItemUserMean,
        ItemUserOverallMean,
        OverallMean,
        UserMean,
        None
    }
    
    private RecommenderIF<U,I,C> predictor;

    public NonPersonalizedPrediction(Type type, ModelIF<U,I,C> model) {
        switch (type){
            case ItemMean:
                predictor = new ItemMean(model);
                break;
            case ItemUserMean:
                predictor = new ItemUserMean(model);
                break;
            case ItemUserOverallMean:
                predictor = new ItemUserOverallMean(model);
                break;
            case UserMean:
                predictor = new UserMean(model);
                break;
            case OverallMean:
                predictor = new OverallMean(model);
                break;
            case None:
            default:
                predictor = null;
        }
        
    }
    
    public Float getPrediction(U user, I item, C context){
        if (predictor != null){
            return this.predictor.predict(user, item, context);
        }
        else{
            return Float.NaN;
        }
    }
    
    @Override
    public String toString(){
        if (predictor != null){
            return this.predictor.toString();
        }
        else {
            return "None";
        }
    }
}
