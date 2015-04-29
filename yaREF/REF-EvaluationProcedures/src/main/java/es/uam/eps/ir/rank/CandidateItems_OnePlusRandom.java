package es.uam.eps.ir.rank;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author pedro
 */
public class CandidateItems_OnePlusRandom<U,I,C extends ContextIF> extends AbstractCandidateItems<U,I,C> implements CandidateItemsIF<U,I,C>{
    protected int N;
    protected float thresholdForHighRating;

    public CandidateItems_OnePlusRandom(SplitIF<U, I, C> split, int N, float thresholdForHighRating) {
        this.split = split;
        this.N = N;
        this.thresholdForHighRating = thresholdForHighRating;
    }
    
    public Set<I> getRelevantSet(U user, C context) {
        Set<I> set = new TreeSet<I>();
        
        ModelIF<U,I,C> testingSet = split.getTestingSet();
        Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testingSet.getPreferencesFromUser(user);
        
        for (PreferenceIF<U,I,C> pref:prefs){
            Float prefRating = testingSet.getPreferenceValue(user, pref.getItem(), null);
            if (isValidItemForRelevantSet(pref.getItem(), prefRating)) {
                set.add(pref.getItem());
            }            
        }
        
        return set;
    }

    public Set<I> getNonRelevantSet(U user, C context) {
        SortedSet<I> _nonRelevantSet = new TreeSet<I>();
        ModelIF<U,I,C> testingSet = split.getTestingSet();

        _nonRelevantSet.addAll(testingSet.getItems());

        SortedSet<I> ratedItems = new TreeSet<I>();
        ModelIF<U,I,C> dataset = split.getTestingSet();
        Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)dataset.getPreferencesFromUser(user);
        for (PreferenceIF<U,I,C> pref:prefs){
            ratedItems.add(pref.getItem());
        }
        _nonRelevantSet.removeAll(ratedItems);
        
        List<I> nonRelevantItems = new ArrayList(removeTrainingRatedItemsBy(user, _nonRelevantSet));
        Collections.shuffle(nonRelevantItems, new Random(0));
        
        Set<I> nonRelevantSet = new TreeSet<I>();
        for (int i = 0; i < N; i++){
            nonRelevantSet.add(nonRelevantItems.get(i));
        }
        
        return nonRelevantSet;
    }
    
    
    
    protected boolean isValidItemForRelevantSet(I item, float value){
        return value >= thresholdForHighRating;
    }
    
    public Set<I> getNonRelevantSet(U user, I item, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString(){
        return "OnePlusRandom_N=" + N +"_threshold="+thresholdForHighRating;
    }
}
