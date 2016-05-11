
package ImplicitToExplicitTransformation;

import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.Collections;
import java.util.List;


public class ContextUnited<U, I, C extends ContextIF> extends ContextAwereItoE<U, I, C> {
    
   
    ImplicitToExplicitFeedback<Object,Object, ContextIF> envio_lista;

    public ContextUnited(ModelIF<U, I, C> _model, List<ContextDefinition> _cdef, TransformationManager.TransformationMethod method) {
        super(_model, _cdef, method);
    }
    

    
//    private ImplicitToExplicitFeedback<Object,Object, ContextIF> getTransformationMethod(List<AccumulatedFrequency> lista){
//        
//        switch (method){
//            case Basic:
//                return new ImplicitToExplicitFeedbackBasic(lista);
//            case Adjustment:
//                return new ImplicitToExplicitFeedbackAdjustment(lista);
//            case Celma:
//                return new ImplicitToExplicitFeedbackCelma(lista);
//            case Final:
//                return new ImplicitToExplicitFeedbackFinal(lista);
//        }
//        return new ImplicitToExplicitFeedbackFinal(lista);
//    }
    @Override
    public void Send_to_ImplicitToExplicitFeedback() {
        
         int indice=0;
            
           for(U u:mapausuario.keySet())
            {       
            for (C l:mapausuario.get(u).keySet()) 
                {   
                    
                for (I y:mapausuario.get(u).get(l).keySet()) 
                    {
                     lista.add(new AccumulatedFrequency(u,l,y,mapausuario.get(u).get(l).get(y)));
//                      System.out.println("Lista__:  "+"U: "+lista.get(indice).getUser()+"Contexto: "+lista.get(indice).getContext()+"I: "+ lista.get(indice).getItem()+"Fr: "+lista.get(indice).getFrec());
//                      indice++;  
                    }
                }            
                Collections.sort(lista);
//                for (int i = 0; i < lista.size(); i++) {
////                System.out.println("Lista__:  "+"U: "+lista.get(indice).getUser()+"Contexto: "+lista.get(indice).getContext()   +"I: "+ lista.get(indice).getItem()+"Fr: "+lista.get(indice).getFrec());
//                indice++;
//                }
               envioycargadoModel(lista); 
//                envio_lista= getTransformationMethod(lista);
//                envio_lista.doTransformation();
//                
//            for (C l:mapausuario.get(u).keySet()) 
//                {    
//                 for (I y:mapausuario.get(u).get(l).keySet()) 
//                    {
//                     model2.addPreference(u, y, (float)envio_lista.getRating(u, l, y), l);
////                        System.out.println("R"+(float)envio_lista.getRating(u, l, y));                    
//                    }          
//                }
      lista.clear();
               indice=0;      
            }       
//      Print();
                }

}