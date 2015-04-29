package es.uam.eps.ir.cars.neighborhood;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public class CosineSimilarity<U,I,C extends ContextIF> implements SimilarityComputerIF<U,I,C> {
    protected final int minimumCommonRatings=1;
    protected final double defaultValue=0.0; //TODO: Check!!!!
    protected ContextualModelUtils<U,I,C> eModel=null;

    private synchronized void check_eModel(ModelIF<U,I,C> model){
        if (eModel == null){
            eModel=new ContextualModelUtils<U,I,C>(model);
        }
    }

    public double getUsersSimilarity(ModelIF<U, I, C> model, U user1ID, U user2ID) {
        int commonItems=0;
        double element1Value;
        double element2Value;
        double element1SD=0.0;
        double element2SD=0.0;
        double numerator=0;
        double denominator;

        double similarity=defaultValue;
        check_eModel(model);
        
        Collection<I> user1Items=eModel.getItemsRatedBy(user1ID);
        Collection<I> user2Items=eModel.getItemsRatedBy(user2ID);
        
        Collection<I> smallestList;
        int selectedList;
        if (user1Items.size() < user2Items.size()){
            smallestList=new ArrayList<I>(user1Items);
            selectedList=1;
        }
        else{
            smallestList=new ArrayList<I>(user2Items);
            selectedList=2;
        }
        
        for (I itemID:smallestList){
            boolean commonItem=false;
            if (selectedList==1){
                if (model.getPreferences(user2ID, itemID) != null){
                    commonItem=true;
                }
            }
            else if (model.getPreferences(user1ID, itemID) != null){
                commonItem=true;
            }
            if (commonItem){
                commonItems++;
                element1Value=model.getPreferenceValue(user1ID, itemID, null);
                element2Value=model.getPreferenceValue(user2ID, itemID, null);
                numerator+=  element1Value * element2Value;
                element1SD+= element1Value * element1Value;
                element2SD+= element2Value * element2Value;
            }
        }
        
        
        if (commonItems >= minimumCommonRatings){
            denominator=Math.sqrt(element1SD)*Math.sqrt(element2SD);
            if (denominator!=0.0){
                similarity=numerator/denominator;

            }
        }
        
        return similarity;
    }

    public double getItemsSimilarity(ModelIF<U, I, C> model, I item1ID, I item2ID) {
        int commonUsers=0;
        double element1Value;
        double element2Value;
        double element1SD=0.0;
        double element2SD=0.0;
        double numerator=0;
        double denominator;

        double similarity=defaultValue;
        check_eModel(model);

        Collection<U> item1Users=eModel.getUsersWhomRate(item1ID);
        Collection<U> item2Users=eModel.getUsersWhomRate(item2ID);

        // Check for extremely cold start users
        if (item1Users == null || item2Users==null){
            return defaultValue;
        }

        Collection<U> smallestList;
        int selectedList;
        if (item1Users.size() < item2Users.size()){
            smallestList=new ArrayList<U>(item1Users);
            selectedList=1;
        }
        else{
            smallestList=new ArrayList<U>(item2Users);
            selectedList=2;
        }

        // Iteration over common users
        for (U userID:smallestList){
            boolean commonUser=false;
            if (selectedList==1){
                if (model.getPreferences(userID, item2ID) != null){
                    commonUser=true;
                }
            }
            else if (model.getPreferences(userID, item1ID) != null){
                commonUser=true;
            }
            if (commonUser){
                commonUsers++;
                element1Value=model.getPreferenceValue( userID, item1ID, null);
                element2Value=model.getPreferenceValue( userID, item2ID, null);
                numerator+=  element1Value * element2Value;
                element1SD+= element1Value * element1Value;
                element2SD+= element2Value * element2Value;
            }
        }

        if (commonUsers >= minimumCommonRatings){
            denominator=Math.sqrt(element1SD)*Math.sqrt(element2SD);
            if (denominator!=0.0){
                similarity=numerator/denominator;
            }
        }
        
        return similarity;
    }
    
    @Override
    public String toString(){
        return "CosineSim_minCommonRatings="+this.minimumCommonRatings;
    }
}
