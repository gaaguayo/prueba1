package es.uam.eps.ir.core.context;

import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.Comparator;

/**
 *
 * @author Pedro G. Campos
 * @author Alejandro Bellogin
 * 
 */
public class RatingPreferenceComparator<U, I, C extends ContinuousTimeContextIF> implements Comparator<PreferenceIF<U, I, C>> {

    public int compare(PreferenceIF<U, I, C> t, PreferenceIF<U, I, C> t1) {
        int result = Long.signum(t.getContext().getTimestamp() - t1.getContext().getTimestamp());

        // Comparing item IDs
        if (result == 0) {
            String otherItemKey = t1.getItem().toString();
            String thisItemKey  =  t.getItem().toString();
            result = otherItemKey.compareTo(thisItemKey);
        }

        // Comparing user IDs
        if (result == 0) {
            String otherUserKey = t1.getUser().toString();
            String thisUserKey  =  t.getUser().toString();
            result = otherUserKey.compareTo(thisUserKey);
        }

        return result;
    }
}
