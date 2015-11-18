package es.uam.eps.ir.split.ratingorder;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Pedro G. Campos
 */
public class RORandom<U,I,C extends ContextIF> implements RatingOrderIF<U,I,C>{
    Random random;

    public RORandom() {
        // Fixed seed value to ensure replicability
        random = new Random(0L);
    }

    public RORandom(int seed) {
        random = new Random(seed);
    }
    
    public List<PreferenceIF<U, I, C>> getOrderedRatings(ModelIF<U, I, C> model) {
        final List<PreferenceIF<U, I, C>> prefsList = new ArrayList<PreferenceIF<U, I, C>>();
        final List<Pair<U,I>> pairsList = new ArrayList<Pair<U,I>>();
        
        Set<I> userItems;
        for (U user: model.getUsers()){
            userItems = new TreeSet<I>();
            for (PreferenceIF<U,I,C> pref: model.getPreferencesFromUser(user)){
                userItems.add(pref.getItem());
            }
            for (I item : userItems){
                pairsList.add(new Pair<U,I>(user, item));
            }
        }
        
        // List is sorted first to ensure replicability
        Collections.sort(pairsList);
        Collections.shuffle(pairsList, random);

        for (Pair<U,I> p : pairsList){
            prefsList.addAll(model.getPreferences(p.getValue1(), p.getValue2()));
        }
        
        return prefsList;
    }
    
    @Override
    public String toString(){
        return "randomOrder";
    }
    
//    private class Pair<U,I>{
//        U user;
//        I item;
//
//        public Pair(U user, I item) {
//            this.user = user;
//            this.item = item;
//        }
//
//        public U getUser() {
//            return user;
//        }
//
//        public I getItem() {
//            return item;
//        }
//        
//    }
}
