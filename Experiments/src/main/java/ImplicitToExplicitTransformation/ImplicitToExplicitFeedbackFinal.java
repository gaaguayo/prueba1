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
public class ImplicitToExplicitFeedbackFinal<U, I, C  extends ContextIF> extends ImplicitToExplicitFeedback<U, I, C> {
    double[] fr=frecuenciasRelativas(); 
       int[] fa=frecuenciasAbsoluta();
       float sum=Sum();
       double cv=CV();
    
//       double[] FC =frecuenciasAcumuladasComplementarias();
       
    public ImplicitToExplicitFeedbackFinal(List<AccumulatedFrequency> _lista) {
        super(_lista);
    }


    public Map<C,Map<I, Integer>> freqs_to_ratings() {
       int []ratingajustado=new int[lista.size()];
      double []frecuenciascom=new double[lista.size()];
       double []frecuenciascomrelativa=new double[lista.size()];
       int []rating=new int[lista.size()];
      

       int indice=0;
       frecuenciascom[0]=sum;
       for (Integer s: fa) 
       {
//           fa[indice]=s;
           if (indice!=0) {
                
                frecuenciascom[indice]=Redondear(frecuenciascom[indice-1]-fa[indice-1],2);
                
           }
               frecuenciascomrelativa[indice]=Redondear((frecuenciascom[indice]/sum)*100,0); 

            indice++;
            
       }
       for (int i = 0; i < frecuenciascomrelativa.length; i++) {
//            rating[i]=inverseFreq_to_rating(frecuenciascomrelativa[i]);
              if (cv<=0.5) {
               
               rating[i]=3;
           }
           else
           {rating[i]=inverseFreq_to_rating(frecuenciascomrelativa[i]);
           }
//            System.out.println("rating"+rating[i]+i);
        }
       
         for (int i = rating.length-1; i >=0; i--) {
            ratingajustado[rating.length-1]=rating[rating.length-1];
            if ((i-1)>=0) {
                 if(rating[i]<rating[i-1]&&fa[i]<fa[i-1])
                 {
                     ratingajustado[i-1]=rating[i-1];
                 }
                 else
                     ratingajustado[i-1]=ratingajustado[i];
            }
//             System.out.println("rating"+rating[i]);
//            System.out.println("RAjustado"+ratingajustado[i]);
        }

      
       return Mapacontexto(ratingajustado);
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
