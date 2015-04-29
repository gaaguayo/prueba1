package es.uam.eps.ir.rank;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author pedro
 */
public class CandidateItems_CommunityTest<U,I,C extends ContextIF> extends AbstractCandidateItems<U,I,C> implements CandidateItemsIF<U,I,C> {
    private float threshold;
    private SortedSet<I> communityTestItems;

    public CandidateItems_CommunityTest(SplitIF<U, I, C> split, float threshold) {
        this.split = split;
        this.threshold = threshold;
        
        communityTestItems = getCommunityTestItems(split);        
    }
    
    private SortedSet<I> getCommunityTestItems(SplitIF<U,I,C> _split){
        SortedSet _communityTestItems = new TreeSet<I>();
        ModelIF<U,I,C> testingSet = _split.getTestingSet();
        
        _communityTestItems.addAll(testingSet.getItems());
        return _communityTestItems;
    }
    
    public Set<I> getNonRelevantSet(U user, C context) {
        SortedSet<I> _notRelevantSet = new TreeSet<I>();
        _notRelevantSet.addAll(communityTestItems);
        
        
        SortedSet<I> _relevantSet = new TreeSet<I>();
        ModelIF<U,I,C> testingSet = split.getTestingSet();
        Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testingSet.getPreferencesFromUser(user);
        
        for (PreferenceIF<U,I,C> pref:prefs){
            Float prefRating = testingSet.getPreferenceValue(user, pref.getItem(), null);
            if (prefRating >= threshold){
                _relevantSet.add(pref.getItem());
            }
        }
        
        _notRelevantSet.removeAll(_relevantSet);
        return removeTrainingRatedItemsBy(user, _notRelevantSet);
    }
    
    public Set<I> getRelevantSet(U user, C context) {
        Set<I> _relevantSet = new TreeSet<I>();
        
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
        return "CommunityTest_threshold="+threshold;
    }    
}
