package es.uam.eps.ir.core.similarity;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class PearsonWeightedSimilarity<U,I,C extends ContextIF> implements SimilarityComputerIF<U,I,C>{
    protected final int minimumCommonRatings=1;
    protected final double defaultValue=0.0; //According Lathia, 2009 (PhD Thesis)
    protected final int PearsonWeight=50; // According to Herlocker et al., 1999. An algorithmic framework for collaborative filtering
    protected ContextualModelUtils<U,I,C> eModel=null;
    final static Logger logger = Logger.getLogger("ExperimentLog");
    
    private synchronized void check_eModel(ModelIF<U,I,C> model){
        if (eModel == null){
            eModel=new ContextualModelUtils<U,I,C>(model);
        }
    }

    public double getItemsSimilarity(ModelIF<U,I,C> model, I item1ID, I item2ID) {
        double element1Diff;
        double element2Diff;
        int diffCount=0;
        double element1SD=0.0;
        double element2SD=0.0;
        double numerator=0;
        double denominator;
        
        double correlation=defaultValue;
        
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
       
        double element1Mean=eModel.getItemMeanRating(item1ID);
        double element2Mean=eModel.getItemMeanRating(item2ID);
        logger.log(Level.FINEST, "PearsonW_Item_Sim({0},{1})", new Object[]{item1ID, item2ID});
        logger.log(Level.FINEST, "Item {0} mean: {1}", new Object[]{item1ID, element1Mean});
        logger.log(Level.FINEST, "Item {0} mean: {1}", new Object[]{item2ID, element2Mean});
        
        
        
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
                    element1Diff=model.getPreferenceValue( userID, item1ID, null) - element1Mean;
                    element2Diff=model.getPreferenceValue( userID, item2ID, null) - element2Mean;
                    diffCount++;
                    numerator+=  element1Diff * element2Diff;
                    element1SD+= element1Diff * element1Diff;
                    element2SD+= element2Diff * element2Diff;
            }
        }
        
        if (diffCount >= minimumCommonRatings){

            denominator=Math.sqrt(element1SD*element2SD);
            if (denominator!=0.0){
                correlation=numerator/denominator;
                
                if (diffCount<this.PearsonWeight){
                    correlation*=(double)(diffCount/(double)this.PearsonWeight);
                }
            }
            
        }
        
        if (correlation > 1.0) {
            correlation=1.0;
        }
        if (correlation < -1.0) {
            correlation=-1.0;
        }
        return correlation;
        
    }
    public double getUsersSimilarity(ModelIF<U,I,C> model, U user1ID, U user2ID) {
        double element1Diff;
        double element2Diff;
        int diffCount=0;
        double element1SD=0.0;
        double element2SD=0.0;
        double numerator=0;
        double denominator;

        double correlation=defaultValue;
        
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
       
        double element1Mean=eModel.getUserMeanRating(user1ID);
        double element2Mean=eModel.getUserMeanRating(user2ID);
        logger.log(Level.FINER, "User_Sim({0},{1})", new Object[]{user1ID, user2ID});
        logger.log(Level.FINER, "User1: {0} mean: {1}", new Object[]{user1ID, element1Mean});
        logger.log(Level.FINER, "User2: {0} mean: {1}", new Object[]{user2ID, element2Mean});
        
        for (I itemID:smallestList){
            boolean validItem=false;
            if (selectedList==1){
                if (model.getPreferences(user2ID, itemID) != null){
                    validItem=true;
                }
            }
            else if (model.getPreferences(user1ID, itemID) != null){
                validItem=true;
            }
            if (validItem){
                    element1Diff=model.getPreferenceValue(user1ID,itemID, null) - element1Mean;
                    element2Diff=model.getPreferenceValue(user2ID,itemID, null) - element2Mean;                    
                    diffCount++;
                    numerator+=  element1Diff * element2Diff;                   
                    element1SD+= element1Diff * element1Diff;
                    element2SD+= element2Diff * element2Diff;
                    logger.log(Level.FINEST, "Item {0} user1: {1}, user2: {2}", new Object[]{itemID, model.getPreferenceValue(user1ID,itemID, null), model.getPreferenceValue(user2ID,itemID, null)});
                    logger.log(Level.FINEST, "Item {0} diff: {1}, user1sd: {2}, user2sd: {3}", new Object[]{itemID, element1Diff * element2Diff, element1Diff * element1Diff, element2Diff * element2Diff});
            }
        }

        if (diffCount >= minimumCommonRatings){

            denominator=Math.sqrt(element1SD*element2SD);
            if (denominator!=0.0){
                correlation=numerator/denominator;

                if (diffCount<this.PearsonWeight){
                    correlation*=(double)(diffCount/(double)this.PearsonWeight);
                }

            }
            
        }
        
        if (correlation > 1.0) {
            correlation=1.0;
        }
        if (correlation < -1.0) {
            correlation=-1.0;
        }
        return correlation;

    }
    
    @Override
    public String toString(){
        return "PearsonWeightedSim_minCommonRatings="+this.minimumCommonRatings+"_weight="+PearsonWeight;
    }
}
