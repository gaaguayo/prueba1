package es.uam.eps.ir.rank;

import es.uam.eps.ir.core.model.impl.DefaultAggregationFunction;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceAggregationFunction;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author pedro
 */
public abstract class AbstractCandidateItems<U,I,C extends ContextIF> implements CandidateItemsIF<U,I,C>{
    protected SplitIF<U,I,C> split;
    protected final PreferenceAggregationFunction f = new DefaultAggregationFunction();
    
    protected Set<I> removeTrainingRatedItemsBy(U user, SortedSet<I> items){
        SortedSet<I> ratedItems = new TreeSet<I>();
        Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)split.getTrainingSet().getPreferencesFromUser(user);
        if (prefs == null){
            return items;
        }
        
        for (PreferenceIF<U,I,C> pref:prefs){
            ratedItems.add(pref.getItem());
        }
        
        items.removeAll(ratedItems);
        return items;
    }
}
