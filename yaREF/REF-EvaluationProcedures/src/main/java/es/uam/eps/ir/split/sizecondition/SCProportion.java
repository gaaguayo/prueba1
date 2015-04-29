package es.uam.eps.ir.split.sizecondition;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Pedro G. Campos
 */
public class SCProportion<U,I,C extends ContextIF> implements SizeConditionIF<U,I,C>{
    float testProportion;
    
    public SCProportion(float testProportion){
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
    

    public int getNumberOfRatingsForTraining(ModelIF<U, I, C> model) {
        int totalSize=0;
        // (user,item) pair based count
        Set<I> userItems;
        for (U user: model.getUsers()){
            userItems = new TreeSet<I>();            
            for (PreferenceIF<U,I,C> pref: model.getPreferencesFromUser(user)){
                userItems.add(pref.getItem());
            }
//            totalSize += model.getPreferencesFromUser(user).size();
            totalSize += userItems.size();
        }
        
        int testSize = (int)(Math.round(totalSize * testProportion));
        
//        return testSize;
        return totalSize - testSize;
    }
    
    @Override
    public String toString(){
        return "ProportionSize("+testProportion+")";
    }

    public String shortName(){
        return "Pr("+testProportion+")";
    }
}
