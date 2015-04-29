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
public class SCFixed<U,I,C extends ContextIF> implements SizeConditionIF<U,I,C>{
    int size;
    
    public SCFixed(int size){
        this.size = size;
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
        if (totalSize > size){
//            return size;
            return totalSize - size;
        }
        else{
            return totalSize/2;
        }
    }
    
    @Override
    public String toString(){
        return "FixedSize("+size+")";
    }

    public String shortName(){
        return "FS("+size+")";
    }
}
