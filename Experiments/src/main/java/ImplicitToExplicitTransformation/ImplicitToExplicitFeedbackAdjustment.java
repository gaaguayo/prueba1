/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImplicitToExplicitTransformation;

import es.uam.eps.ir.core.context.ContextIF;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gaby
 */
public class ImplicitToExplicitFeedbackAdjustment<U, I, C  extends ContextIF> extends ImplicitToExplicitFeedback<U, I, C> {
    double[] fr=frecuenciasRelativas();
    int[] fa=frecuenciasAbsoluta();
    double[] FC =frecuenciasAcumuladasComplementarias();
   public ImplicitToExplicitFeedbackAdjustment(List<AccumulatedFrequency> _lista) {
        super(_lista);
    }

   
   
   public Map<C,Map<I, Integer>> freqs_to_ratings() {

       int []rating=new int[lista.size()];
       int []ratingajustado=new int[lista.size()];

       
       for (int i = 0; i < lista.size(); i++) 
       {     
           rating[i]=inverseFreq_to_rating(FC[i]);

       }      
        for (int i = rating.length-1; i >=0; i--) {
            ratingajustado[rating.length-1]=rating[rating.length-1];
            
            if ((i-1)>=0) {
                 if(rating[i]<rating[i-1]&&fa[i]<fa[i-1])
                 {
                    
                     ratingajustado[i-1]=rating[i-1];
                 }
                 else
                 {ratingajustado[i-1]=ratingajustado[i];}
            }
//            System.out.println("RAjusta"+ratingajustado[i]);
        }

        
             


        return Mapacontexto(ratingajustado);
   } 
}

