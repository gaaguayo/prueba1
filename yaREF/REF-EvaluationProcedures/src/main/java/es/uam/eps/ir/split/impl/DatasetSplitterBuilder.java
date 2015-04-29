package es.uam.eps.ir.split.impl;

import es.uam.eps.ir.split.sizecondition.SizeConditionIF;
import es.uam.eps.ir.split.sizecondition.SCProportion;
import es.uam.eps.ir.split.sizecondition.SCTimeProportion;
import es.uam.eps.ir.split.sizecondition.SCFixed;
import es.uam.eps.ir.split.ratingorder.RatingOrderIF;
import es.uam.eps.ir.split.ratingorder.RORandom;
import es.uam.eps.ir.split.ratingorder.ROTime;
import es.uam.eps.ir.split.baseset.BSCommunity;
import es.uam.eps.ir.split.baseset.BaseSetGeneratorIF;
import es.uam.eps.ir.split.baseset.BSUser;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.split.DatasetSplitterIF;

/**
 *
 * @author pedro
 */
public class DatasetSplitterBuilder<U,I,C extends ContextIF> {
    public enum BASE_SET{
        COMMUNITY,
        USER
    }
    
    public enum RATING_ORDER{
        RANDOM,
        TIME
    }
    
    public enum SIZE_CONDITION{
        FIXED,
        PROPORTION,
        TIME_PROPORTION,
    }
    
    public enum SPLITTING_METHOD{
        HOLDOUT,
        REPEATED_SAMPLING,
        XFOLD_CV
    }

    // DEFAULT PARAMETERS' VALUES
    private BASE_SET         base_set        = BASE_SET.USER;
    private RATING_ORDER     rating_order    = RATING_ORDER.RANDOM;
    private SIZE_CONDITION   size_condition  = SIZE_CONDITION.PROPORTION;
    private SPLITTING_METHOD method          = SPLITTING_METHOD.HOLDOUT;
    
    
    private double proportion_value = 0.2;
    private int fixed_size_value = 9;
    private int nSplits = 5;
    
    public DatasetSplitterBuilder(){
    }
    
    public DatasetSplitterBuilder<U,I,C> communityBase(){
        base_set = BASE_SET.COMMUNITY;
        return this;
    }
    
    public DatasetSplitterBuilder<U,I,C> userBase(){
        base_set = BASE_SET.USER;
        return this;
    }
    
    public DatasetSplitterBuilder<U,I,C> RandomOrder(){
        rating_order = RATING_ORDER.RANDOM;
        return this;
    }
    
    public DatasetSplitterBuilder<U,I,C> TimeOrder(){
        rating_order = RATING_ORDER.TIME;
        return this;
    }
    
    public DatasetSplitterBuilder<U,I,C> FixedSize(){
        size_condition = SIZE_CONDITION.FIXED;
        return this;
    }
    
    public DatasetSplitterBuilder<U,I,C> FixedSize(int size){
        size_condition = SIZE_CONDITION.FIXED;
        fixed_size_value = size;
        return this;
    }
    
    
    public DatasetSplitterBuilder<U,I,C> ProportionSize(){
        size_condition = SIZE_CONDITION.PROPORTION;
        return this;
    }
    
    public DatasetSplitterBuilder<U,I,C> ProportionSize(double proportion){
        size_condition = SIZE_CONDITION.PROPORTION;
        proportion_value = proportion;
        return this;
    }
    
    public DatasetSplitterBuilder<U,I,C> TimeProportionSize(){
        size_condition = SIZE_CONDITION.TIME_PROPORTION;
        return this;
    }
    
    public DatasetSplitterBuilder<U,I,C> TimeProportionSize(double proportion){
        size_condition = SIZE_CONDITION.TIME_PROPORTION;
        proportion_value = proportion;
        return this;
    }
    
    public DatasetSplitterBuilder<U,I,C> holdout(){
        method = SPLITTING_METHOD.HOLDOUT;
        return this;
    }
    
    public DatasetSplitterBuilder<U,I,C> repeatedSampling(){
        method = SPLITTING_METHOD.REPEATED_SAMPLING;
        return this;
    }

    public DatasetSplitterBuilder<U,I,C> repeatedSampling(int nSplits){
        method = SPLITTING_METHOD.REPEATED_SAMPLING;
        this.nSplits = nSplits;
        return this;
    }
    
    public DatasetSplitterBuilder<U,I,C> xFold_CV(){
        method = SPLITTING_METHOD.XFOLD_CV;
        return this;
    }

    public DatasetSplitterBuilder<U,I,C> xFold_CV(int nSplits){
        method = SPLITTING_METHOD.XFOLD_CV;
        this.nSplits = nSplits;
        return this;
    }
    
    public DatasetSplitterIF<U,I,C> buildSplitter(){
        BaseSetGeneratorIF<U,I,C> baseSet = buildBaseSet();
        RatingOrderIF<U,I,C> ratingOrder = buildRatingOrder();
        SizeConditionIF<U,I,C> sizeCondition = buildSizeCondition();
        
        DatasetSplitterIF<U,I,C> splitter = buildDatasetSplitter(baseSet, ratingOrder, sizeCondition);
        return splitter;
    }
    
    private BaseSetGeneratorIF<U,I,C> buildBaseSet(){
        BaseSetGeneratorIF<U,I,C> _baseSet = null;
        switch (base_set){
            case COMMUNITY:
                _baseSet = new BSCommunity<U,I,C>();
                break;
            case USER:
                _baseSet = new BSUser<U,I,C>();
                break;
        }
        return _baseSet;
    }
    
    private RatingOrderIF<U,I,C> buildRatingOrder(){
        RatingOrderIF<U,I,C> _ratingOrder = null;
        switch (rating_order){
            case RANDOM:
                _ratingOrder = new RORandom<U,I,C>();
                break;
            case TIME:
                _ratingOrder = new ROTime();
                break;
        }
        return _ratingOrder;
    }
    
    private SizeConditionIF<U,I,C> buildSizeCondition(){
        SizeConditionIF<U,I,C> _sizeCondition = null;
        switch(size_condition){
            case FIXED:
                _sizeCondition = new SCFixed<U,I,C>(fixed_size_value);
                break;
            case PROPORTION:
                _sizeCondition = new SCProportion<U,I,C>((float)proportion_value);
                break;
            case TIME_PROPORTION:
                _sizeCondition = (SizeConditionIF<U, I, C>) new SCTimeProportion<U,I,ContinuousTimeContextIF>((float)proportion_value);
                break;
        }
        return _sizeCondition;
    }
    
    private DatasetSplitterIF<U,I,C> buildDatasetSplitter(
            BaseSetGeneratorIF<U, I, C> baseSet, 
            RatingOrderIF<U, I, C> ratingOrder, 
            SizeConditionIF<U, I, C> sizeCondition) {
        DatasetSplitterIF<U,I,C> _datasetSplitter = null;
        switch(method){
            case HOLDOUT:
                _datasetSplitter = new DatasetSplitter_Holdout<U,I,C>(baseSet, ratingOrder, sizeCondition);
                break;
            case REPEATED_SAMPLING:
                _datasetSplitter = new DatasetSplitter_RepeatedSampling<U,I,C>(nSplits, baseSet, ratingOrder, sizeCondition);
                break;
            case XFOLD_CV:
                _datasetSplitter = new DatasetSplitter_Xfold_CV<U,I,C>(nSplits, baseSet);
                break;
        }
        return _datasetSplitter;
    }
}
