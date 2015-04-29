package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.cars.recommender.NeighborBasedRecommenderBuilder;
import es.uam.eps.ir.cars.recommender.RecommenderBuilderIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author pedro
 */
public class ContextFilteringBasedRecommenderBuilder<U,I,C extends ContextIF> extends NeighborBasedRecommenderBuilder<U,I,C> implements RecommenderBuilderIF<U,I,C>{
//    private ContextualSlicerIF<U,I,C> _slicer = new ContextualSlicer_WeekPeriod();
    protected ContextualSlicerIF<U,I,C> _slicer;
    
    public ContextFilteringBasedRecommenderBuilder(ModelIF<U,I,C> model){
        super(model);
    }
    
    public ContextFilteringBasedRecommenderBuilder<U,I,C> slicer(ContextualSlicerIF<U,I,C> _slicer){
        this._slicer = _slicer;
        return this;
    }
    
    public ContextFilteringBasedRecommenderBuilder<U,I,C> WeekPeriod(){
        this._slicer = new ContextualSlicer_WeekPeriod();
        return this;
    }
    
    @Override
    protected RecommenderIF<U,I,C> instantiateRecommender(RECOMMENDER_METHOD recommenderMethod){
        RecommenderIF<U, I, C> recomm=null;
        switch(recommenderMethod){
            case ContextualPreFiltering_UserBased:
                recomm = new ContextualPreFilteringUserRatingRecommender<U,I,C>(model, _slicer, _neighbors);
                break;
            case ContextualPreFiltering_MahoutUserBased:
                recomm = new ContextualPreFilteringMahoutUserRatingRecommender<U,I,C>(model, _slicer, _neighbors);
                break;
            case ContextualPostFiltering_UserBased:
                recomm = new ContextualPostFilteringUserRatingRecommender<U,I,C>(model, _slicer, _neighbors);
                break;
            case ContextualFilterPostFiltering_UserBased:
                recomm = new ContextualFilterPostFilteringUserRatingRecommender<U,I,C>(model, _slicer, _neighbors);
                break;
            case ContextualWeightPostFiltering_UserBased:
                recomm = new ContextualWeightPostFilteringUserRatingRecommender<U,I,C>(model, _slicer, _neighbors);
                break;
            case ContextualPostFiltering_MahoutUserBased:
                recomm = new ContextualPostFilteringMahoutUserRatingRecommender<U,I,C>(model, _slicer, _neighbors);
                break;
            case ContextualFilterPostFiltering_MahoutUserBased:
                recomm = new ContextualFilterPostFilteringMahoutUserRatingRecommender<U,I,C>(model, _slicer, _neighbors);
                break;
            case ContextualWeightPostFiltering_MahoutUserBased:
                recomm = new ContextualWeightPostFilteringMahoutUserRatingRecommender<U,I,C>(model, _slicer, _neighbors);
                break;
            case ContextualModeling_MahoutUserBased:
                recomm = new ContextualModelingMahoutUserRatingRecommender<U,I,C>(model, _slicer, _neighbors);
                break;
            case ContextualModeling2_MahoutUserBased:
                recomm = new ContextualModeling2MahoutUserRatingRecommender<U,I,C>(model, _slicer, _neighbors);
                break;
        }
        return recomm;
    }
}
