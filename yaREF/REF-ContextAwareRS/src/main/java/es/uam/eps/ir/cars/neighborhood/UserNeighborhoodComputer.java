package es.uam.eps.ir.cars.neighborhood;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author pedro
 */
public class UserNeighborhoodComputer<U,I,C extends ContextIF> extends AbstractNeighborhoodComputer<U,I,C> implements NeighborhoodIF<U,I,C> {
    public UserNeighborhoodComputer(ModelIF<U,I,C> model, SimilarityComputerIF<U,I,C> computer) {
        super(model,-1, Double.NEGATIVE_INFINITY, computer);
        if (model!=null) {
            otherElements=new ArrayList(model.getUsers());
            Collections.sort((List)otherElements);
        }
    }
    
    public UserNeighborhoodComputer(ModelIF<U,I,C> model, int neighbors, SimilarityComputerIF<U,I,C> computer) {
        super(model, neighbors, Double.NEGATIVE_INFINITY, computer);
        if (model!=null) {
            otherElements=new ArrayList(model.getUsers());
            Collections.sort((List)otherElements);
        }
    }

    public UserNeighborhoodComputer(int neighbors, SimilarityComputerIF<U,I,C> computer) {
        super(null, neighbors, Double.NEGATIVE_INFINITY, computer);
    }

    public double getSimilarity(Object user1ID, Object user2ID) {
        return computer.getUsersSimilarity(model, (U)user1ID, (U)user2ID);
    }
        
    @Override
    public String toString(){
        return "UserRatingSim("+this.computer+")_neighbors="+this.neighbors;
    }
}
