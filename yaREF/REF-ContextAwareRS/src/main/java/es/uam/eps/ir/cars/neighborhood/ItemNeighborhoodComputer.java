package es.uam.eps.ir.cars.neighborhood;

import es.uam.eps.ir.core.similarity.SimilarityComputerIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author pedro
 */
public class ItemNeighborhoodComputer<U,I,C extends ContextIF> extends AbstractNeighborhoodComputer<U,I,C> implements NeighborhoodIF<U,I,C> {
    public ItemNeighborhoodComputer(ModelIF<U,I,C> model, SimilarityComputerIF<U,I,C> computer) {
        super(model,-1, Double.NEGATIVE_INFINITY, computer);
        if (model!=null) {
            otherElements=new ArrayList(model.getItems());
            Collections.sort((List)otherElements);
        }
    }
    
    public ItemNeighborhoodComputer(ModelIF<U,I,C> model, int neighbors, SimilarityComputerIF<U,I,C> computer) {
        super(model, neighbors, Double.NEGATIVE_INFINITY, computer);
        if (model!=null) {
            otherElements=new ArrayList(model.getItems());
            Collections.sort((List)otherElements);
        }
    }

    public ItemNeighborhoodComputer(int neighbors, SimilarityComputerIF<U,I,C> computer) {
        super(null, neighbors, Double.NEGATIVE_INFINITY, computer);
    }

    public double getSimilarity(Object item1ID, Object item2ID) {
        return computer.getItemsSimilarity(model, (I)item1ID, (I)item2ID);
    }
        
    @Override
    public String toString(){
        return "ItemRatingSim("+this.computer+")_neighbors="+this.neighbors;
    }
}
