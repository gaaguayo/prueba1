/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.cars.neighborhood;

import es.uam.eps.ir.core.similarity.SimilarityComputerIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public abstract class AbstractNeighborhoodComputer<U, I, C extends ContextIF> implements NeighborhoodIF<U, I, C>{
    protected SimilarityComputerIF<U,I,C> computer;
    public static final int MAX_NEIGHBORS=1000;
    public static final long serialVersionUID = 1001L;
    protected Collection<Object> elements;
    protected Collection<Object> otherElements;
    protected ModelIF<U,I,C> model;
    protected Map<Object, List<SimilarityDatumIF>> neighborhood;
    protected final int neighbors;
    protected final double threshold;
    final static Logger logger = Logger.getLogger("ExperimentLog");

    public AbstractNeighborhoodComputer(ModelIF<U,I,C> model, int neighbors, double threshold, SimilarityComputerIF<U,I,C> computer){
        this.model = model;
        this.computer=computer;
        this.neighborhood= new HashMap();
        this.threshold=threshold;
        this.neighbors = neighbors==-1 ? MAX_NEIGHBORS : neighbors;
    }
    
    public int getNeighborhoodSize() {
        return this.neighbors;
    }

    public List<SimilarityDatumIF> getNeighbors(Object elementID) {
        if (!this.neighborhood.containsKey(elementID)) {
            this.computeNeighbors(elementID);
        }
        return this.neighborhood.get(elementID);
    }

    public void computeNeighbors(Object key){
        PriorityQueue<SimilarityDatumIF> queue=new PriorityQueue();
        ArrayList<SimilarityDatumIF> similars=new ArrayList();

        // Computes similarity with all otherElements
        for (Object element2Key:otherElements){
            SimilarityDatumIF data;
            if (element2Key!=key){
                double similarity=getSimilarity(key, element2Key);
                if ( !Double.isInfinite(similarity) ){
                    data=new FloatSimilarityDatum((float)similarity, element2Key);
                    queue.add(data);
                }
            }
        }
        
        
        //kNN
        if (neighbors>0){
            while ( !queue.isEmpty() ){
                logger.log(Level.FINE, "Target: {0} neighbor {1}", new Object[]{key, queue.peek().toString()});
                similars.add(queue.poll());
                if (neighbors==similars.size()) break;
            }        
        }
        
        // Threshold-limited neighbors
        else{
            while ( !queue.isEmpty() ){
                SimilarityDatumIF data=queue.poll();
                if (data.getSimilarity()<this.threshold) break;
                logger.log(Level.FINE, "Target: {0} neighbor {1}", new Object[]{key, data.toString()});
                similars.add(data);
            }            
        }
                        
        this.neighborhood.put(key, similars);        
    }
    
    public abstract double getSimilarity(Object element1ID, Object element2ID);    
    
    public ModelIF<U,I,C> getModel(){
        return model;
    }
}
