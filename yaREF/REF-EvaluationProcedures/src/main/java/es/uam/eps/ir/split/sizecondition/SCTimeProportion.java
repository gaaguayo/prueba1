package es.uam.eps.ir.split.sizecondition;

import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.util.Pair;
import es.uam.eps.ir.split.ratingorder.ROTime;
import es.uam.eps.ir.split.ratingorder.RatingOrderIF;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author pedro
 */
public class SCTimeProportion<U,I,C extends ContinuousTimeContextIF> implements SizeConditionIF<U,I,C>{
    float testProportion;
    
    public SCTimeProportion(float testProportion){
        if (testProportion < 0){ //problem 
            throw new IllegalArgumentException("Test proportion must be larger than 0%");
        }
        else if (testProportion < 1.0){ // Value given in the range ]0, 1[
            this.testProportion=testProportion;
        }
        else{ // Value grater than 100, senseless
            throw new IllegalArgumentException("Test proportion must be smaller than 100%");            
        }
    }
    

    public int getNumberOfRatingsForTraining(ModelIF<U, I, C > model) {
        RatingOrderIF ro = new ROTime();
        List<PreferenceIF<U,I,C>> prefs = ro.getOrderedRatings(model);

        long minTime = prefs.get(0).getContext().getTimestamp();
        long maxTime = prefs.get(prefs.size()-1).getContext().getTimestamp();
        
//        for (U user: model.getUsers()){
//            Collection<? extends PreferenceIF<U,I,C>> prefs = model.getPreferencesFromUser(user);
//            for (PreferenceIF<U,I,C> pref: prefs){
//                long time = pref.getContext().getTimestamp();
//                if (time < minTime){
//                    minTime = time;
//                }
//                if (time > maxTime){
//                    maxTime = time;
//                }
//            }
//        }
        
        long totalTime = maxTime - minTime;
        long testTime = maxTime - (long) (totalTime * testProportion);
        
        Set<Pair<U,I>> pairs = new HashSet<Pair<U,I>>();
        for (PreferenceIF<U,I,C> pref: prefs){
            long time = pref.getContext().getTimestamp();
            if (time > testTime){
                break;
            }
            pairs.add(new Pair<U,I>(pref.getUser(), pref.getItem()));
        }
//        for (U user: model.getUsers()){
//            Collection<? extends PreferenceIF<U,I,C>> prefs = model.getPreferencesFromUser(user);
//            for (PreferenceIF<U,I,C> pref: prefs){
//                long time = pref.getContext().getTimestamp();
//                if (time > testTime){
//                    testSize++;
//                }
//            }
//        }        
        int trainSize = pairs.size();
        return trainSize;
    }
    
    @Override
    public String toString(){
        return "TimeProportionSize("+testProportion+")";
    }

    public String shortName(){
        return "TP("+testProportion+")";
    }
}
