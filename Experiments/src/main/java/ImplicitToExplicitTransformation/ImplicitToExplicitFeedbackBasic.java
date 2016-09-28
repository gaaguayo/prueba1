 
package ImplicitToExplicitTransformation;

import es.uam.eps.ir.core.context.ContextIF;
import java.util.List;
import java.util.Map;

public class ImplicitToExplicitFeedbackBasic<U, I, C extends ContextIF> extends ImplicitToExplicitFeedback<U, I, C> {
    double[] fr=frecuenciasRelativas();
    double[] FC=frecuenciasAcumuladasComplementarias();  
    
    public ImplicitToExplicitFeedbackBasic(List<AccumulatedFrequency> _lista) {
        super(_lista);
    }

    
    public Map<C,Map<I, Integer>> freqs_to_ratings() {
      
     
       int []rating=new int[lista.size()];
          
       for (int i = 0; i < lista.size(); i++) 
       {     
           rating[i]=inverseFreq_to_rating(FC[i]);
//           System.out.println("rating"+rating[i]);
       }      
     
       return Mapacontexto(rating);
    }

   
    
}
