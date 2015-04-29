package es.uam.eps.ir.recommender.offline;

import es.uam.eps.ir.cars.model.TrecFormatRecommenderReader;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.cars.inferred.ContextualAttributeComputerIF;
import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder.TimeContext;
import es.uam.eps.ir.cars.itemsplitting.TimeContextItemSplitter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Pedro G. Campos
 */
public class HybridOfflineRecommender<U,I,C extends ContextIF> implements RecommenderIF<U,I,C>{
    private Map<String,RecommenderIF<U,I,C>> basicRecommenders;
    private ModelIF<U,I,C> testData;
    private String name = "";
    
    public HybridOfflineRecommender(ModelIF<U,I,C> testData, List<String> recommenders, String name){
        this(testData, recommenders);
        this.name = name;
    }
    
    public HybridOfflineRecommender(ModelIF<U,I,C> testData, List<String> recommenders){
        this.testData  = testData;
        basicRecommenders = new HashMap<String, RecommenderIF<U,I,C>>();
        TrecFormatRecommenderReader<U,I,C> reader = new TrecFormatRecommenderReader<U,I,C>(true);
        for (String recommender: recommenders){
            basicRecommenders.put(recommender,reader.readRecommendations(recommender));
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public Float predict(U user, I item, C context) {
        if (testData != null && testData.getPreferences(user, item) == null){
            System.err.println("Tryin to predict for a (user,item) pair without context! Pair (" + user + "," + item + ")");
            Thread.dumpStack();
            System.exit(1);
        }
        int validPredictions = 0;
        Float sum = new Float(0);
        for (String recommender:basicRecommenders.keySet()){
            RecommenderIF<U,I,C> basicRecommender = basicRecommenders.get(recommender);
            Float prediction = basicRecommender.predict(user, item, context);
            if (Float.isNaN(prediction)){
                for (TimeContext timeContext: TimeContext.values()){
                    ContextualAttributeComputerIF attComp = TimeContextItemSplitter.getContextComputer(timeContext);
                    PreferenceIF<U,I,C> pref = (PreferenceIF<U,I,C>)testData.getPreferences(user, item).toArray()[0];
                    String ctx =  "_" + attComp.getAttributeNominalValue(pref.getContext());
                    prediction = basicRecommender.predict(user, (I)(item + "_" + timeContext.toString() + ctx), context);
                    if (Float.isNaN(prediction)){
                        prediction = basicRecommender.predict(user, (I)(item + "_" + timeContext.toString() + "_Other"), context);                        
                    }
                    if (!Float.isNaN(prediction)){
                        break;
                    }
                }
            }
            if (!Float.isNaN(prediction)){
                validPredictions++;
                sum += prediction;
            }
        }
        if (validPredictions > 0){
            return sum / validPredictions;
        }
        return Float.NaN;
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
        return "Hybrid_" + name + "(" + basicRecommenders.size() + ")";
    }
}
