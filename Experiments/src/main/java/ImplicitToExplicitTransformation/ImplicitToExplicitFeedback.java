/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImplicitToExplicitTransformation;


import es.uam.eps.ir.core.context.ContextIF;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class ImplicitToExplicitFeedback<U,I,C extends ContextIF> {
    Map<U,Map<C,Map<I,Integer>>>  user_items_ratingsMap; 
    Map<C,Map<I,Integer>> mapacontexto= new HashMap<C,Map<I,Integer>>();
    Map<I,Integer> mapaitems;    
    final List<AccumulatedFrequency> lista;
    Map<Integer,Map<Integer,Integer>> mapausuarios;
    boolean ratingsComputed;
     int b= 20;
     int c= 40;
     int d= 60;
     int e= 80;
     int f= 100;
    
    
    public ImplicitToExplicitFeedback( List<AccumulatedFrequency> _lista){
        user_items_ratingsMap= new HashMap<U,Map<C,Map<I,Integer>>>();
//        model = _model;
        lista=_lista;
        ratingsComputed = false;
    }
    
    public Integer getRating(U user,C vc,I item){
        if (!ratingsComputed)
            this.doTransformation();
        
        return user_items_ratingsMap.get(user).get(vc).get(item);
    }

    public Map<C,Map<I,Integer>> getRatings(U user){
        if (!ratingsComputed)
            this.doTransformation();
        
        return user_items_ratingsMap.get(user);
    }
    

    public Map<C,Map<I,Integer>> Mapacontexto(int[] rating){
    int indic=0;
    for(AccumulatedFrequency j: lista)
        {
       
        I i=(I)j.getItem();
        C vc= (C)j.getContext();
//        Integer f=j.getFrec();
        
         if (mapacontexto.containsKey(vc)) 
            {
                mapaitems=mapacontexto.get(vc);
                mapaitems.put(i, rating[indic]);
            }
            else
            {
            
            mapaitems= new HashMap<I,Integer>();
            mapaitems.put(i, rating[indic]);
            mapacontexto.put(vc, mapaitems);
//            listadecontextos.add(vc);
            }
         indic++;
         
        }
     
    
    return mapacontexto;
   }

    
     public float Sum(){
        float suma=0;
         for(AccumulatedFrequency j: lista)
        {
        Integer a=j.getFrec();
        suma=suma+a; 
        }
//         System.out.println("Sum: "+Sum);
         return suma;
    }
    public int[] frecuenciasAbsoluta(){
        int []frecuenciasabsolutas=new int[lista.size()];
        int indice=0;
      
         for(AccumulatedFrequency j: lista)
        {
            frecuenciasabsolutas[indice]=j.getFrec(); 
//            System.out.println("frecuenciasAbsolutas"+frecuenciasabsolutas[indice]);
            indice++;
        }
         return frecuenciasabsolutas;
    }
        
    public double[] frecuenciasRelativas(){
        double s=Sum();
        double []frecuenciasrelativas=new double[lista.size()];
        int indice=0;
         for(AccumulatedFrequency j: lista)
        {
        frecuenciasrelativas[indice]=Redondear(j.getFrec()/s,2);
//        frecuenciasrelativas[indice]=j.getFrec()/s;
//        System.out.println("indice: "+indice+"frecuenciasRelativas"+frecuenciasrelativas[indice]);
        indice++;
        }
         return frecuenciasrelativas;
    }
    
    public double[] frecuenciasRelativasAcumuladas(){
            double[]  fr=frecuenciasRelativas();
            double []frecuenciasRelativasAcumuladas=new double[lista.size()];
            
            for (int a=0;a<lista.size();a++) 
       {
           
           if(a!=0){      
            frecuenciasRelativasAcumuladas[a]=Redondear(frecuenciasRelativasAcumuladas[a-1],2)+Redondear(fr[a],2); 
//            frecuenciasRelativasAcumuladas[a]=frecuenciasRelativasAcumuladas[a-1]+fr[a];   
           }
           else
           {
           frecuenciasRelativasAcumuladas[a]=Redondear(frecuenciasRelativas()[a],2);
//           frecuenciasRelativasAcumuladas[a]=frecuenciasRelativas()[a];
           }
//           System.out.println("fRElAcumulada:"+frecuenciasRelativasAcumuladas[a]);
       }
            return frecuenciasRelativasAcumuladas;
    }
    
    public double[] frecuenciasAcumuladasComplementarias(){
              double [] frRA=frecuenciasRelativasAcumuladas();
              double []frecuenciasAcumuladasComplementarias=new double[lista.size()];
           
               frecuenciasAcumuladasComplementarias[0]=1*100;
//               System.out.println("frecuenciasComp"+frecuenciasAcumuladasComplementarias[0]);
               for (int i = 1; i < lista.size(); i++) {
               frecuenciasAcumuladasComplementarias[i]=Redondear((1-frRA[i-1])*100,0);
               
//               frecuenciasAcumuladasComplementarias[i]=(1-frRA[i])*100;
//               System.out.println("frecuenciasComp"+frecuenciasAcumuladasComplementarias[i]);
       }
          return frecuenciasAcumuladasComplementarias;
    }
    

      
    public void doTransformation(){
        
        user_items_ratingsMap.put((U)lista.get(0).getUser(),freqs_to_ratings());

         ratingsComputed = true;   
        }
         
               
    public abstract Map<C,Map<I,Integer>> freqs_to_ratings();
    

    public static double Redondear(double numero,int digitos){
      int cifras=(int) Math.pow(10,digitos);
      
      return Math.rint(numero*cifras)/cifras;
}
    
    public int inverseFreq_to_rating(double a){      
       if (a>=b) {
           if (a<c) {
               return 2;
           }
       }
        if (a>=c) {
           if (a<d) {
               return 3;
           }
       }
        if (a>=d) {
           if (a<e) {
               return 4;
           }
       }   
       if (a>=e) {
           if (a<=f) {
               return 5;
           }
       }      
       if (a>= f){
           System.out.println("Error: frecuencia a=" + a + " en calculo de rating");
           System.exit(0);
       }
       return 1;
   }
}
