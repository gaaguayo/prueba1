package es.uam.eps.ir.cars.neighborhood;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.List;

/**
 *
 * @author pedro
 */
public interface NeighborhoodIF<U,I,C extends ContextIF> {
    
    /*
     * @param elementID Key of the element
     * @return List of neighborhoods of (elementID) element
     */
    public List<SimilarityDatumIF> getNeighbors(Object elementID); 
    
    /*
     * @return General size of neighborhood used
     */
    public int getNeighborhoodSize();
    
    public ModelIF<U,I,C> getModel();
}
