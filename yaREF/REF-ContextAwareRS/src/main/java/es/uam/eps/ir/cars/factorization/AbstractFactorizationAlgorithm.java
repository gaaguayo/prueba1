package es.uam.eps.ir.cars.factorization;

import es.uam.eps.ir.cars.bias.AbstractTrainingAlgorithm;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;

/**
 *
 * @author Pedro G. Campos
 */
public abstract class AbstractFactorizationAlgorithm<U,I,C extends ContextIF> extends AbstractTrainingAlgorithm<U,I,C>{
    private static boolean RMSEData=true;
    
    protected ModelIF<U,I,C> dataModel;
    private ModelIF<U,I,C> testModel;

    public abstract void trainFactor(int factor, int dim1Index, int dim2Index, PreferenceIF<U, I, C> pref, double error);
    public abstract void trainBias(int dim1Index, int dim2Index, PreferenceIF<U, I, C> pref, double error);
    
    @Override
    public void trainStep(int dim1Index, int dim2Index, PreferenceIF<U, I, C> pref, double error) {
        FactorModel theModel = (FactorModel)this.recommenderModel;
        
        /* All factors are updated on each iteration, in order to obtain faster convergence.
         * See Takács et al.,
         *      Scalable Collaborative Filtering Approaches for Large Recommender Systems, 
         *      Journal of Machine Learning Research 10, pp. 623-656 (2009).
         * 
         */
        for (int factor=0;factor<theModel.getnFeatures();factor++){
            trainFactor(factor, dim1Index, dim2Index, pref, error);
        }
        trainBias(dim1Index, dim2Index, pref, error);
    }
    
    public double getRmse(ModelIF<U,I,C> model){
        double rmse=0;
        int ratingCount=0;
        double err=0;
        double sqerr=0;
        
        for (U user: model.getUsers()){
            for (PreferenceIF<U,I,C> pref: model.getPreferencesFromUser(user)){
                I item = pref.getItem();
                if (recommenderModel.existKeys(user, item)){
                    double rating = pref.getValue();
                    double prediction = this.getEstimation(user, item, pref.getContext());
                    err=rating-prediction;
                    sqerr+=(err*err);
                    ratingCount++;                    
                }
            }
        }        
        if (ratingCount!=0){
          rmse=Math.sqrt(sqerr/(double)ratingCount);
        }
        return rmse;
    }

}
