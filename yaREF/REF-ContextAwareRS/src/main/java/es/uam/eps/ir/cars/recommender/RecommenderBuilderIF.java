package es.uam.eps.ir.cars.recommender;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author pedro
 */
public interface RecommenderBuilderIF<U,I,C extends ContextIF> {
    public static enum RECOMMENDER_METHOD{
        kNN_PearsonWeightSimilarity_UserBased,
        kNN_PearsonWeightSimilarity_ItemBased,
        kNN_CosineSimilarity_UserBased,
        kNN_CosineSimilarity_ItemBased,
        Raw_CosineSimilarity_UserBased,
        Raw_CosineSimilarity_ItemBased,
        ContextualPreFiltering_UserBased,
        ContextualPostFiltering_UserBased,
        ContextualFilterPostFiltering_UserBased,
        ContextualWeightPostFiltering_UserBased,
        ContextualPreFiltering_MahoutUserBased,
        ContextualPreFiltering_MahoutSVD,
        ContextualPostFiltering_MahoutUserBased,
        ContextualFilterPostFiltering_MahoutUserBased,
        ContextualWeightPostFiltering_MahoutUserBased,
        ContextualPostFiltering_MahoutSVD,
        ContextualFilterPostFiltering_MahoutSVD,
        ContextualWeightPostFiltering_MahoutSVD,
        ContextualModeling_MahoutUserBased,
        ContextualModeling2_MahoutUserBased,
        ContextualModeling_MahoutSVD,
        TimeDecay_UserBased,
        TimeDecay_UserBased_TrProp,
        TimeDecay_ItemBased
    }
    
    public RecommenderIF<U,I,C> buildRecommender(RECOMMENDER_METHOD recommenderMethod);
    
}
