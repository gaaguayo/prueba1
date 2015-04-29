package es.uam.eps.ir.split.impl;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.util.Pair;
import es.uam.eps.ir.split.DatasetSplitterIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.split.baseset.BaseSetGeneratorIF;
import es.uam.eps.ir.split.ratingorder.RORandom;
import es.uam.eps.ir.split.ratingorder.RatingOrderIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author pedro
 */
public class DatasetSplitter_Xfold_CV<U,I,C extends ContextIF> implements DatasetSplitterIF<U,I,C> {
    private DatasetSplitter_Holdout<U,I,C> ho;
    
    BaseSetGeneratorIF<U,I,C> baseSet;
    RatingOrderIF<U,I,C> ratingOrder;
    
    private int nSplits;

    public DatasetSplitter_Xfold_CV(
            int nSplits,
            BaseSetGeneratorIF<U, I, C> baseSet
            ) {
        this.nSplits = nSplits;
        this.baseSet = baseSet;
        this.ratingOrder = new RORandom(0);
    }
    
    @SuppressWarnings("CallToThreadDumpStack")
    public SplitIF<U, I, C>[] getHoldoutSplit(ModelIF<U, I, C> model){
        SplitIF<U, I, C> _split[] = new Split[nSplits];
        
        // Obtains base sets
        Collection<ModelIF<U,I,C>> baseSets = baseSet.getBaseSets(model);
        
        List<PreferenceIF<U,I,C>>[] foldData = new List[nSplits];
        for (int fold = 0; fold < nSplits; fold++){
            foldData[fold] = new ArrayList<PreferenceIF<U,I,C>>();
        }
        
        for (ModelIF<U,I,C> _baseSet:baseSets){
            // Obstains ordered ratings from each base set
            List<PreferenceIF<U,I,C>> prefs = ratingOrder.getOrderedRatings(_baseSet);
            // (user,item) pair based count
            Set<Pair<U,I>> pairs = new HashSet<Pair<U,I>>();
            for (PreferenceIF<U,I,C> pref : prefs){
                Pair<U,I> pair = new Pair<U,I>(pref.getUser(), pref.getItem());
                pairs.add(pair);
            }            
            int foldSize = pairs.size() / nSplits;
            

            // Generation of fold's data
            int fold = 0;
            Set<Pair<U,I>> foldPairs = new HashSet<Pair<U,I>>();
            for (PreferenceIF<U,I,C> pref: prefs){
                if (foldPairs.size() < foldSize){
                    Pair<U,I> pair = new Pair<U,I>(pref.getUser(), pref.getItem());
                    foldPairs.add(pair);
                }
                else{
                    fold++;
                    foldPairs = new HashSet<Pair<U,I>>();
                }
                if (fold >= nSplits){
                    fold = nSplits-1;
                }
                
                foldData[fold].add(pref);                
            }
        }
        // Generation of training and test sets (splits)

        for (int fold = 0; fold < nSplits; fold++){
            SplitIF<U, I, C> theSplit = null;
            ModelIF<U,I,C> trainData;
            ModelIF<U,I,C> testData;

            try {
                trainData = model.getClass().newInstance();
                testData = model.getClass().newInstance();
            }
            catch(Exception e){
                System.err.println("Problem instantiating model");
                e.printStackTrace();
                return _split;
            }

            for (int currentFold = 0; currentFold < nSplits; currentFold++){
                if (currentFold == fold) {
                    for (PreferenceIF<U,I,C> pref : foldData[currentFold]){
                        testData.addPreference(pref.getUser(), pref.getItem(), pref.getValue(), pref.getContext());
                    }                        
                }
                else{
                    for (PreferenceIF<U,I,C> pref : foldData[currentFold]){
                        trainData.addPreference(pref.getUser(), pref.getItem(), pref.getValue(), pref.getContext());
                    }
                }
            }
            _split[fold] = new Split(trainData, testData);
        }
        return _split;        
    }
    
    public SplitIF<U, I, C>[] split(ModelIF<U, I, C> model) {
        return getHoldoutSplit(model);
    }
    
    @Override
    public String toString(){
        return "Xfold_CV[" + baseSet + "_folds=" + nSplits + "]";
    }
}
