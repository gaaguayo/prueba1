package es.uam.eps.ir.split.impl;

import es.uam.eps.ir.split.sizecondition.SizeConditionIF;
import es.uam.eps.ir.split.ratingorder.RatingOrderIF;
import es.uam.eps.ir.split.baseset.BaseSetGeneratorIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.util.Pair;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.split.DatasetSplitterIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Pedro G. Campos
 */
public class DatasetSplitter_Holdout<U,I,C extends ContextIF> implements DatasetSplitterIF<U,I,C>{
    BaseSetGeneratorIF<U,I,C> baseSet;
    RatingOrderIF<U,I,C> ratingOrder;
    SizeConditionIF<U,I,C> sizeCondition;

    public DatasetSplitter_Holdout(
            BaseSetGeneratorIF<U, I, C> baseSet, 
            RatingOrderIF<U, I, C> ratingOrder, 
            SizeConditionIF<U, I, C> sizeCondition) {
        this.baseSet = baseSet;
        this.ratingOrder = ratingOrder;
        this.sizeCondition = sizeCondition;
    }
    
    @SuppressWarnings("CallToThreadDumpStack")
    public SplitIF<U, I, C> getHoldoutSplit(ModelIF<U, I, C> model){
        SplitIF<U, I, C> _split = null;
        ModelIF<U,I,C> trainData;
        ModelIF<U,I,C> testData;
        
        try {
            trainData = model.getClass().newInstance();
            testData = model.getClass().newInstance();
        }
        catch(Exception e){
            System.err.println("Problem instantiating model");
            e.printStackTrace();
            return _split;
        }
        
        // Obtains base sets
        List<ModelIF<U,I,C>> baseSets = baseSet.getBaseSets(model);
        // baseSets are sorted to ensure replicability
        Collections.sort(baseSets, new Comparator<ModelIF<U,I,C>>(){
            public int compare(ModelIF<U,I,C> m1, ModelIF<U,I,C> m2){
                int result = m1.getUsers().size() - m2.getUsers().size();
                
                if (result == 0)
                    result = m1.getItems().size() - m2.getItems().size();

                if (result == 0){
                    List<U> l1 = new ArrayList(m1.getUsers());
                    List<U> l2 = new ArrayList(m2.getUsers());
                    U u1 = l1.get(0);
                    U u2 = l2.get(0);
                    result = u1.hashCode() - u2.hashCode();
                }

                if (result == 0){
//                    result = m1.hashCode() - m2.hashCode();
                    ContextualModelUtils<U,I,C> utils1 = new ContextualModelUtils(m1);
                    ContextualModelUtils<U,I,C> utils2 = new ContextualModelUtils(m2);
                    
                    result = utils1.getFeedbackRecordsCount() - utils2.getFeedbackRecordsCount();
                    
                    if (result == 0)
                        result = (int)(utils1.getMeanRating()*1000.0) - (int)(utils2.getMeanRating()*1000.0);
                    
                }
                    
                return result;
            }
        });
        
        
        for (ModelIF<U,I,C> _baseSet:baseSets){
            // Obstains ordered ratings from each base set
            List<PreferenceIF<U,I,C>> prefs = ratingOrder.getOrderedRatings(_baseSet);
            // (user,item) pair based count
            Set<Pair<U,I>> pairs = new HashSet<Pair<U,I>>();
            Set<Pair<U,I>> trainingPairs = new HashSet<Pair<U,I>>();
            int size = sizeCondition.getNumberOfRatingsForTraining(_baseSet);
//            int pos=0;
            
            for (PreferenceIF<U,I,C> pref : prefs){
                Pair<U,I> pair = new Pair<U,I>(pref.getUser(), pref.getItem());
                pairs.add(pair);
                if (pairs.size() <= size){
                    trainingPairs.add(pair);
                    trainData.addPreference(pref.getUser(), pref.getItem(), pref.getValue(), pref.getContext());
                }
                else{
                    if (!trainingPairs.contains(pair)){
                        testData.addPreference(pref.getUser(), pref.getItem(), pref.getValue(), pref.getContext());
                    }
                }
            }
            
//            for (pos = 0; pos < size; pos++){
//                PreferenceIF<U,I,C> pref = prefs.get(pos);
//                testData.addPreference(pref.getUser(), pref.getItem(), pref.getValue(), pref.getContext());
//            }
//            for (; pos < prefs.size(); pos++){
//                PreferenceIF<U,I,C> pref = prefs.get(pos);
//                trainData.addPreference(pref.getUser(), pref.getItem(), pref.getValue(), pref.getContext());
//            }
        }
        
        _split = new Split(trainData, testData);
        return _split;
    }

    public SplitIF<U, I, C>[] split(ModelIF<U, I, C> model) {
        SplitIF<U, I, C> splitArray[] = new SplitIF[1];
        splitArray[0] = getHoldoutSplit(model);
        return splitArray;
    }
    
    @Override
    public String toString(){
        return "Holdout[" + baseSet + "_" + ratingOrder + "_" + sizeCondition + "]";
    }
}
