package es.uam.eps.ir.metrics;

/**
 *
 * @author pedro
 */
public interface RecommendationIF<I>  extends Comparable<RecommendationIF<I>>{
   
    /*
     * @return the ID of the recommended item
     */
    public I getItemID();

    /*
     * @return the predicted preference for the item
     */
    public Float getValue();
    
    /*
     * @return whether the preference prediction computation was personalized
     *         (false if the engine was unable to compute it via its standard method)
     */
    public boolean isPersonalized();
}
