/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImplicitToExplicitTransformation;

import es.uam.eps.ir.core.context.ContextIF;
import java.util.List;
import java.util.Map;


public class ImplicitToExplicitFeedbackCelma<U, I, C extends ContextIF> extends ImplicitToExplicitFeedback<U, I, C> {
    double []fr= frecuenciasRelativas();
      int []fa= frecuenciasAbsoluta();       
    double[] FC =frecuenciasAcumuladasComplementarias();
    double cv=CV();
    
    public ImplicitToExplicitFeedbackCelma(List<AccumulatedFrequency> _lista) {
        super(_lista);
    }

    @Override
   public Map<C,Map<I, Integer>> freqs_to_ratings() {

       int []rating=new int[lista.size()];

//        System.out.println("tamaño rating"+rating.length);
       for (int i = 0; i < rating.length; i++) 
       {      
          
           if (cv<=0.5) {
               
               rating[i]=3;
           }
           else
           {rating[i]=inverseFreq_to_rating(FC[i]);
           }          
       } 
//       System.out.println("cv:  "+cv);

       return Mapacontexto(rating);
    }
     
 
   
    public double desviacionestandar(){     
      double media;
      double varianza = 0.0;
      double desviacion; 
   
      media = Sum() / lista.size(); //media aritmetica 
//        System.out.println("U: " +Integer.parseInt(u.toString())+"Sum"+Sum+"divisor"+mapaitems.values().size());
  //se hace la Sum de las diferencias respecto a a lamedia
//        System.out.println("media: "+media);
    for(int i = 0; i < lista.size(); i++)
    {
    double rango;
    rango = Math.pow((fa[i]-media),2);
//        System.out.println("rango"+rango);
    varianza = varianza + rango;
    }
//        System.out.println("sum rango"+varianza);
    varianza = varianza/(float)lista.size();//suma de diferencias sobre "n"
//        System.out.println("divisor"+(float)mapaitems.values().size());
  //teniendo ya la varinza solo debemos sacarle raiz cuadrada 
  //tendremos la desviación estandar
    desviacion = Math.sqrt(varianza);
  
  //impresion de resultados
//    System.out.println("Media: "+media);
//    System.out.println("Varianza: "+varianza);
//    System.out.println("Desvianción Estándar: "+desviacion);
//        System.out.println("desviacion estandar"+desviacion);
      return desviacion;
     }
     public double CV(){
     
      double CoeficienteVar=desviacionestandar()/(Sum()/lista.size());
//         System.out.println("cv:  "+cv);
        return CoeficienteVar;
     }
     
     
    
}
