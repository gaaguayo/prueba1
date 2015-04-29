package es.uam.eps.ir.split.impl;

import es.uam.eps.ir.split.sizecondition.SizeConditionIF;
import es.uam.eps.ir.split.ratingorder.RatingOrderIF;
import es.uam.eps.ir.split.baseset.BaseSetGeneratorIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.split.DatasetSplitterIF;

/**
 *
 * @author pedro
 */
public class DatasetSplitter_RepeatedSampling<U,I,C extends ContextIF> implements DatasetSplitterIF<U,I,C> {
    private DatasetSplitter_Holdout<U,I,C> ho;
    private int nSplits;

    public DatasetSplitter_RepeatedSampling(
            int nSplits,
            BaseSetGeneratorIF<U, I, C> baseSet, 
            RatingOrderIF<U, I, C> ratingOrder, 
            SizeConditionIF<U, I, C> sizeCondition
            ) {
        this.nSplits = nSplits;
        this.ho = new DatasetSplitter_Holdout<U,I,C>(baseSet, ratingOrder, sizeCondition);
    }
    
    public SplitIF<U, I, C>[] split(ModelIF<U, I, C> model) {
        SplitIF<U, I, C> splitArray[] = new SplitIF[nSplits];
        for (int i = 0; i < nSplits; i++){
            splitArray[i] = ho.getHoldoutSplit(model);
        }
        return splitArray;
    }
    
    @Override
    public String toString(){
        return "RepeatedSampling[" + ho + "]";
    }
}
