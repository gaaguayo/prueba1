package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.cars.recommender.RecommenderBuilderIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author pedro
 */
public class ContextFilteringModelBasedRecommenderBuilder<U,I,C extends ContextIF> extends ContextFilteringBasedRecommenderBuilder<U,I,C> implements RecommenderBuilderIF<U,I,C>{
    protected int _numFeatures = 60; // default value
    protected int _numIterations = 30; // default value

    public ContextFilteringModelBasedRecommenderBuilder(ModelIF<U,I,C> model){
        super(model);
    }
    
    public ContextFilteringModelBasedRecommenderBuilder<U,I,C> numFeatures(int _numFeatures){
        this._numFeatures = _numFeatures;
        return this;
    }
    
    public ContextFilteringModelBasedRecommenderBuilder<U,I,C> numIterations(int _numIterations){
        this._numIterations = _numIterations;
        return this;
    }
    
    @Override
    protected RecommenderIF<U,I,C> instantiateRecommender(RECOMMENDER_METHOD recommenderMethod){
        RecommenderIF<U, I, C> recomm=null;
        switch(recommenderMethod){
            case ContextualPreFiltering_MahoutSVD:
                recomm = new ContextualPreFilteringMahoutSVDRecommender<U,I,C>(model, _slicer, _numFeatures, _numIterations);
                break;
            case ContextualPostFiltering_MahoutSVD:
                recomm = new ContextualPostFilteringMahoutSVDRecommender_Frequency<U,I,C>(model, _slicer, _neighbors, _numFeatures, _numIterations);
                break;
            case ContextualFilterPostFiltering_MahoutSVD:
                recomm = new ContextualFilterPostFilteringMahoutSVDRecommender_Neighbors<U,I,C>(model, _slicer, _neighbors, _numFeatures, _numIterations);
                break;
            case ContextualWeightPostFiltering_MahoutSVD:
                recomm = new ContextualWeightPostFilteringMahoutSVDRecommender_Neighbors<U,I,C>(model, _slicer, _neighbors, _numFeatures, _numIterations);
                break;
            case ContextualModeling_MahoutSVD:
                recomm = new ContextualModelingMahoutSVDRecommender<U,I,C>(model, _slicer, _numFeatures, _numIterations);
                break;
        }
        return recomm;
    }
    
}
