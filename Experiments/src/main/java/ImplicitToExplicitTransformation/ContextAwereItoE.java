/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImplicitToExplicitTransformation;


import es.uam.eps.ir.core.context.CategoricalContext;
import es.uam.eps.ir.core.context.ContextContainer;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.model.impl.GenericExplicitModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class ContextAwereItoE<U, I, C extends ContextIF> {
    final ModelIF<U, I, C> model;
    ModelIF<U, I, C> model2 = new GenericExplicitModel<U, I, C>();
    List<ContextIF> listacntxcont;
    Map<C,Map<I,Integer>> mapacontexto;
    Map<I,Integer> mapaitems;
    List<ContextDefinition> cdef;
    List<AccumulatedFrequency> lista= new ArrayList<AccumulatedFrequency>();
    Map<U,Map<C,Map<I,Integer>>> mapausuario=new HashMap<U,Map<C,Map<I,Integer>>>();
    
    ImplicitToExplicitFeedback<Object,Object, ContextIF> z;
    
    private TransformationManager.TransformationMethod method;
    
    public ContextAwereItoE(ModelIF<U, I, C> _model,List<ContextDefinition> _cdef, TransformationManager.TransformationMethod method) {
        model=_model;
        cdef= _cdef;
        this.method=method;
    }
    
    private ImplicitToExplicitFeedback<Object,Object, ContextIF> getTransformationMethod(List<AccumulatedFrequency> lista){
        
        switch (method){
            case Basic:
                return new ImplicitToExplicitFeedbackBasic(lista);
            case Adjustment:
                return new ImplicitToExplicitFeedbackAdjustment(lista);
            case Celma:
                return new ImplicitToExplicitFeedbackCelma(lista);
            case Final:
                return new ImplicitToExplicitFeedbackFinal(lista);
        }
        return new ImplicitToExplicitFeedbackFinal(lista);
    }
    
    public void envioycargadoModel(List<AccumulatedFrequency> lista){
        
        ImplicitToExplicitFeedback<Object,Object, ContextIF> z;
        z=getTransformationMethod(lista);
        z.doTransformation();
        
        for (AccumulatedFrequency h: lista) {   
            model2.addPreference((U)h.getUser(), (I)h.getItem(), (float)z.getRating((U)h.getUser(), h.getContext(), (I)h.getItem()), (C)h.getContext());
//            System.out.println("U: "+(U)h.getUser()+" I: "+(I)h.getItem()+" C: "+h.getContext()+" R: "+(float)z.getRating(h.getUser(), h.getContext(), (I)h.getItem()));
        }
    }
    
    public  void Separation (){
        listacntxcont= new ArrayList<ContextIF>();
//             new HashMap<I,Integer>();
        for(U u: model.getUsers())
            { 
        
        mapacontexto= new HashMap<C,Map<I,Integer>>();
        Collection<? extends PreferenceIF<U,I,C>> userprefs= model.getPreferencesFromUser(u);
        for(PreferenceIF p:userprefs)
            {
        ContextContainer cntx= new ContextContainer();    
            I i= (I) p.getItem();
            
            ContextIF c= p.getContext();
            if (c instanceof ContextContainer)
            {
            ContextContainer cc = (ContextContainer)c;
            
//            List<ContextIF> ctxs = cc.getContexts();
            
//                System.out.println(""+cntx.getContexts().size());
//            String clave="";

                            try{
                            
                              for (ContextDefinition j:cdef) 
                {           
//                            clave=clave+cc.getCategoricalContext(j).getValue();
                            cntx.add(new CategoricalContext(j,j.getNominalValue(cc.getCategoricalContext(j).getValue())));
             
                } 
                                     
                            }catch(IndexOutOfBoundsException ex){
                                
                                continue;
                            }
                            
                if(!listacntxcont.contains(cntx))  
                {listacntxcont.add(cntx);}
                    
                         if (mapacontexto.containsKey((C)cntx)) 
                                {
                                mapaitems=mapacontexto.get((C)cntx);
                                if (mapaitems.containsKey(i)) 
                                    {
                                    mapaitems.put(i, mapaitems.get(i)+1);
                                      
                                    }
                                 else
                                    {
                                    mapaitems.put(i,1);
                                    
                                    }
                                }
                            else
                                {
                                mapaitems=new HashMap<I,Integer>() ;
                                mapaitems.put(i,1);
                                mapacontexto.put((C)cntx, mapaitems);
                               
                                }                           
                }  
            mapausuario.put(u, mapacontexto);
            } 
         
        
        
        }
       
      Send_to_ImplicitToExplicitFeedback();
      
    } 
    public ModelIF<U, I, C> getmodel(){
    return model2;
    }
    
    public List<ContextIF> getlistcontext(){
    return  listacntxcont;
    }
    
    public abstract void Send_to_ImplicitToExplicitFeedback();
     
    public void Print(){
        Print(listacntxcont, model2);
    }
      
    public void Print(List<ContextIF> listacntxcont, ModelIF<U, I, C> model){
   
        for(ContextIF c:listacntxcont)
     
        { 
            System.out.println("");
            System.out.println("CONTEXTO: "+c);

            System.out.println("");
            for(U u : model.getUsers())
            {
                System.out.print("U:"+u);
                System.out.print("\t");
                for(I i: model.getItems())
                {
                    System.out.print(model.getPreferenceValue(u, i, (C)c)+"\t");        
                }
                System.out.println("");
            }    
    
        } 
         System.out.println("");
   } 
}