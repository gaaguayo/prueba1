package es.uam.eps.ir.rank;

import es.uam.eps.ir.core.model.impl.DefaultAggregationFunction;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Pedro G. Campos
 */
public class CandidateItems_UserTest_UserMean<U,I,C extends ContextIF> extends AbstractCandidateItems<U,I,C> implements CandidateItemsIF<U,I,C>{
    ContextualModelUtils<U,I,C> utils;

    public CandidateItems_UserTest_UserMean(SplitIF<U, I, C> split) {
        this.split = split;
        utils = new ContextualModelUtils<U,I,C>(split.getTrainingSet());
        utils.getMeanRating();
    }

    public Set<I> getNonRelevantSet(U user, C context) {
        Set<I> _notRelevantSet = new HashSet<I>();
        
        ModelIF<U,I,C> testingSet = split.getTestingSet();
        Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testingSet.getPreferencesFromUser(user);
        
        Float threshold = utils.getUserMeanRating(user);
        if (threshold == null || Float.isNaN(threshold)){
            threshold = utils.getMeanRating();
        }
        for (PreferenceIF<U,I,C> pref:prefs){
            Float prefRating = testingSet.getPreferenceValue(user, pref.getItem(), null);
            if (prefRating <= threshold){ // rating should be over the mean to consider the item relevant
                _notRelevantSet.add(pref.getItem());
            }
        }
        return _notRelevantSet;
    }

    public Set<I> getRelevantSet(U user, C context) {
        Set<I> _relevantSet = new HashSet<I>();
        
        ModelIF<U,I,C> testingSet = split.getTestingSet();
        Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testingSet.getPreferencesFromUser(user);
        
        Float threshold = utils.getUserMeanRating(user);
        if (threshold == null || Float.isNaN(threshold)){
            threshold = utils.getMeanRating();
        }
        for (PreferenceIF<U,I,C> pref:prefs){
            Float prefRating = testingSet.getPreferenceValue(user, pref.getItem(), null);
            if (prefRating > threshold){ // rating should be over the mean to consider the item relevant
                _relevantSet.add(pref.getItem());
            }
        }
        return _relevantSet;
    }
    
    public Set<I> getNonRelevantSet(U user, I item, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String toString(){
        return "UserTest_UserMean";
    }
}
