package es.uam.eps.ir.rank;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Pedro G. Campos
 */
public class CandidateItems_UserTest<U,I,C extends ContextIF> extends AbstractCandidateItems<U,I,C> implements CandidateItemsIF<U,I,C>{
    private float threshold;

    public CandidateItems_UserTest(SplitIF<U, I, C> split, float threshold) {
        this.split = split;
        this.threshold = threshold;
    }

    public Set<I> getNonRelevantSet(U user, C context) {
        Set<I> _notRelevantSet = new HashSet<I>();
        
        ModelIF<U,I,C> testingSet = split.getTestingSet();
        Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testingSet.getPreferencesFromUser(user);
        
        for (PreferenceIF<U,I,C> pref:prefs){
            Float prefRating = testingSet.getPreferenceValue(user, pref.getItem(), null);
            if (prefRating < threshold){
                _notRelevantSet.add(pref.getItem());
            }
        }
        return _notRelevantSet;
    }

    public Set<I> getRelevantSet(U user, C context) {
        Set<I> _relevantSet = new HashSet<I>();
        
        ModelIF<U,I,C> testingSet = split.getTestingSet();
        Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testingSet.getPreferencesFromUser(user);
        
        for (PreferenceIF<U,I,C> pref:prefs){
            Float prefRating = testingSet.getPreferenceValue(user, pref.getItem(), null);
            if (prefRating >= threshold){
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
        return "UserTest_threshold="+threshold;
    }
}
