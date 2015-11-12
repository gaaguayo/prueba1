package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.cars.inferred.CategoricalContextComputerIF;
import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder;
import es.uam.eps.ir.cars.recommender.AbstractRecommender;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.mahout.DataModelFactory;
import es.uam.eps.ir.mahout.MahoutRecommender;
import es.uam.eps.ir.mahout.MahoutRecommenderFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 *
 * @author pedro
 */
public class ContextualModeling2MahoutUserRatingRecommender <U,I,C extends ContextIF> extends AbstractRecommender<U,I,C> implements RecommenderIF<U,I,C>,ContextDependentIF{
    private static final Logger logger = Logger.getLogger("ExperimentLog");
    private RecommenderIF<U,I,C> engine;
    private ModelIF<U,I,C> contextualModel;
    private Map<U,Map<ContextualSegmentIF,String>> usercontextUseridMap;
    protected ContextualSlicerIF<U,I,C> slicer;
    private int neighbors;
    
        public ContextualModeling2MahoutUserRatingRecommender(ModelIF<U, I, C> originalModel, ContextualSlicerIF<U,I,C> slicer, int neighbors) {
        this.model = originalModel;
        this.slicer = slicer;
        this.usercontextUseridMap = new HashMap<U,Map<ContextualSegmentIF,String>>();
        this.neighbors = neighbors;
        this.contextualModel = this.getContextualModel();
        initRecommender(contextualModel);
    }
    
    

    public Float predict(U user, I item, C context) {
        final ContextualSegmentIF segment=slicer.getSegment(context);
        Map<ContextualSegmentIF,String> contextContextualuserMap = usercontextUseridMap.get(user);
        String contextualuser = null;
        if (contextContextualuserMap==null) { 
            contextualuser = (String)user;
        }
        else{
            contextualuser = contextContextualuserMap.get(segment);            
        }
    
//        U contextualuser = user;
        float prediction = Float.NaN;
        if (engine != null){
            prediction = engine.predict((U)contextualuser, item, context);
        }
        return prediction;
    }

    public List<I> recommend(U user, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public I getMostRelevant(U user, I item1, I item2, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ContextualSlicerIF getSlicer() {
        return this.slicer;
    }

    public ModelIF getContextualModel(ContextualSegmentIF context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private ModelIF getContextualModel(){
        logger.log(Level.INFO,"Contextual model profiling started");
        ModelIF<Object,Object,C> cModel = null;
        try{
            cModel = model.getClass().newInstance();
        } catch (Exception e){
        System.err.println("Problem instatiating copy of model " + model.toString() + ": " + e);
            e.printStackTrace();
            System.exit(1);
        }
        
        for (ContextualSegmentIF segment:slicer.getSegments()){
            logger.log(Level.INFO, "Building contextual model for segment {0}", segment);
                        
            final ModelIF<U,I,C> contextualData=slicer.getSegmentData(model, segment);
            if (contextualData.getUsers().isEmpty()){
                continue;
            }
            
            Collection<U> users = contextualData.getUsers();
            for (U user : users){
                Collection<? extends PreferenceIF<U,I,C>> userPreferences = contextualData.getPreferencesFromUser(user);
                for (PreferenceIF<U,I,C> preference : userPreferences){
                    String newUser = "" + preference.getUser() + segment;
                    cModel.addPreference(newUser, preference.getItem(), preference.getValue(), preference.getContext());
                    Map<ContextualSegmentIF,String> contextUseridMap = usercontextUseridMap.get(user);
                    if (contextUseridMap == null){
                        contextUseridMap = new HashMap<ContextualSegmentIF,String>();
                    }
                    contextUseridMap.put(segment, newUser);
                    usercontextUseridMap.put(user, contextUseridMap);
                }
            }
        }
        
        // adding full user profiles
        Collection<U>users = model.getUsers();
        for (U user : users){
            Collection<? extends PreferenceIF<U,I,C>> userPreferences = model.getPreferencesFromUser(user);
            for (PreferenceIF<U,I,C> preference : userPreferences){
                cModel.addPreference(preference.getUser(), preference.getItem(), preference.getValue(), preference.getContext());                
            }
        }
        
        return cModel;
    }
    
    private static CategoricalContextComputerIF getContextComputer(ContinuousTimeContextComputerBuilder.TimeContext timeContext) {
        return ContinuousTimeContextComputerBuilder.getContextComputer(timeContext);
    }

    private Map<String, Collection<PreferenceIF<U, I, C>>> getContextSplits(Collection<? extends PreferenceIF<U, I, C>> preferences, CategoricalContextComputerIF contextComputer) {
        Map<String, Collection<PreferenceIF<U,I,C>>> contextSplits = new HashMap<String, Collection<PreferenceIF<U,I,C>>>();
        
        for (PreferenceIF<U,I,C> pref: preferences){
            String contextNominalValue = contextComputer.getAttributeNominalValue(pref.getContext());
            
            Collection<PreferenceIF<U,I,C>> splitPreferences = contextSplits.get(contextNominalValue);
            if (splitPreferences == null){
                splitPreferences = new ArrayList<PreferenceIF<U,I,C>>();
            }
            splitPreferences.add(pref);
            contextSplits.put(contextNominalValue, splitPreferences);
        }
        
        return contextSplits;
    }
    
    private void initRecommender(ModelIF<U,I,C> cModel) {
        this.eModel = new ContextualModelUtils(cModel);
        
        DataModelFactory dmf = new DataModelFactory();
        DataModel dataModel = dmf.getDataModel(cModel, eModel);
        
        try {
            UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(neighbors, userSimilarity, dataModel);
            Recommender recommender =
                      new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
            Recommender cachingRecommender = new CachingRecommender(recommender);            
            MahoutRecommender<U,I,C> mRecommender = new MahoutRecommender<U,I,C>(cachingRecommender, eModel);
            engine = mRecommender;
        } catch (TasteException ex) {
            Logger.getLogger(MahoutRecommenderFactory.class.getName()).log(Level.SEVERE, null, ex);
            Thread.dumpStack();
            System.exit(1);
        }
    }    

    @Override
    public String toString(){
        String s="CM_("+slicer+"_"+engine+")";
        return s;
    }
}
