package es.uam.eps.ir.cars.bias;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.List;

/**
 *
 * @author Pedro G. Campos, pcampossoto@gmail.com
 */
public class ModelBasedRecommender<U,I,C extends ContextIF> implements RecommenderIF<U,I,C> {
    protected ModelIF<U,I,C> dataModel;
    protected ContextualModelUtils<U,I,C> eModel;
    protected AbstractTrainingAlgorithm algorithm;
    protected boolean trained = false;


    protected ModelBasedRecommender(ModelIF<U,I,C> model){
        this.dataModel=model;
    }
    
    public ModelBasedRecommender(ModelIF<U,I,C> dataModel, ContextualModelUtils<U,I,C> eModel, double defaultValue, int iterationSteps, double lrate, double lambda){
        this.dataModel=dataModel;
        this.eModel=eModel;
        this.algorithm=new BiasAlgorithm(dataModel, eModel, defaultValue, iterationSteps, lrate, lambda);
    }

    public void trainModel(){
        algorithm.train();
        trained = true;
    }

    public Float predict(U user, I item, C context){
        if (!trained){
            trainModel();
        }
        return algorithm.getEstimation(user, item, context);
    }

    public ModelIF<U, I, C> getModel() {
        return this.dataModel;
    }

    public I getMostRelevant(U user, I item1, I item2, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<I> recommend(U user, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    

//    public boolean saveModel(String file){
//        return algorithm.saveModel(file);
//    }
//
//    public boolean loadModel(String file){
//        return algorithm.loadModel(file);
//    }

    public boolean validKeys(U user, I item){
        return algorithm.recommenderModel.existKeys(user, item);
    }

    public boolean validUser(U user){
        return algorithm.recommenderModel.existsUser(user);
    }

    public boolean validItem(I item){
        return algorithm.recommenderModel.existsItem(item);
    }

    @Override
    public String toString(){
        return this.algorithm.toString();
    }
}
