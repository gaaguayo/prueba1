package es.uam.eps.ir.rank;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author pedro
 */
public class CandidateItems_OnePlusRandom_Context<U,I,C extends ContextIF> extends CandidateItems_OnePlusRandom<U,I,C> implements CandidateItemsIF<U,I,C>{
    private Map<I,C> itemContextMap;

    public CandidateItems_OnePlusRandom_Context(SplitIF<U, I, C> split, int N, float thresholdForHighRating) {
        super(split, N, thresholdForHighRating);
        itemContextMap = new HashMap<I,C>();
    }
    
    @Override
    public Set<I> getRelevantSet(U user, C context) {
        Set<I> set = new TreeSet<I>();
        
        ModelIF<U,I,C> testingSet = split.getTestingSet();
        Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testingSet.getPreferencesFromUser(user);
        
        for (PreferenceIF<U,I,C> pref:prefs){
            Float prefRating = testingSet.getPreferenceValue(user, pref.getItem(), null);
            if (isValidItemForRelevantSet(pref.getItem(), prefRating)) {
                set.add(pref.getItem());
                itemContextMap.put(pref.getItem(), pref.getContext());
            }            
        }
        
        return set;
    }
    
        
    public C getContext(I item){
        return itemContextMap.get(item);
    }

    @Override
    public String toString(){
        return "OnePlusRandom_Context_N=" + N +"_threshold="+thresholdForHighRating;
    }
}
