package es.uam.eps.ir.metrics.util;

import es.uam.eps.ir.core.similarity.SimilarityComputerIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author pedro
 */
public class RatingItemSimilarityMatrix<U,I,C extends ContextIF> {
    Map<Object, Map<Object, Double>> simMatrix;
    ModelIF<U,I,C> model;
    SimilarityComputerIF<U,I,C> computer;

    public RatingItemSimilarityMatrix(ModelIF<U,I,C> model, SimilarityComputerIF<U,I,C> computer) {
        this.model = model;
        this.computer = computer;
        simMatrix=new HashMap();
    }
    
    public double getSimilarity(I item1, I item2){
        double similarity=Double.NaN;
        I key1,key2;
        
        if (item1 instanceof Integer){
            if ((Integer)item1 < (Integer)item2){
                key1=item1;
                key2=item2;
            }
            else{
                key1=item2;
                key2=item1;                
            }
        }
        else{
            if ( ((String)item1).compareTo((String)item2) < 0){
                key1=item1;
                key2=item2;
            }
            else{
                key1=item2;
                key2=item1;                
            }
        }
        
        if (simMatrix.containsKey(key1)){
            if (simMatrix.get(key1).containsKey(key2)){
                similarity=simMatrix.get(key1).get(key2);
            }
            else{
                similarity=computer.getItemsSimilarity(model, key1, key2);
                simMatrix.get(key1).put(key2, similarity);
            }
        }
        else{
            similarity=computer.getItemsSimilarity(model, key1, key2);
            Map itemSimMap=new HashMap();
            itemSimMap.put(key2, similarity);
            simMatrix.put(key1, itemSimMap);
        }
                
        if (similarity> 1.0){System.out.println("Sim:"+similarity);}
        return similarity;
    }
    
}
