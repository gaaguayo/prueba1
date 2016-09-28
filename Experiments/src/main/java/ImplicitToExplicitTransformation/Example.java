
package ImplicitToExplicitTransformation;

import es.uam.eps.ir.core.context.CategoricalContext;
import es.uam.eps.ir.core.context.ContextContainer;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.impl.GenericExplicitModel;
import java.util.ArrayList;
import java.util.List;

public class Example {
    static TransformationManager.TransformationMethod transformationMethod = null;
    static TransformationManager.TransformationLevel tranformationLevel = null;
    static List<ContextDefinition> listaContextDef;
    
    public static void main( String[] args )
    {

      // Se crea modelo de datos
      ModelIF model = getImplicitModel();
      
      List<ContextIF> listaCtx = new ArrayList();
            
      // Se realizará transformación
      transformationMethod=   TransformationManager.TransformationMethod.Adjustment;
      tranformationLevel= TransformationManager.TransformationLevel.AllContexts;
      TransformationManager example = new TransformationManager(transformationMethod,tranformationLevel,listaContextDef);
      ModelIF explicitModel = example.toExplicit(model);
      Print(example.getlistcontext(),explicitModel);
    }
   
    
   public static ModelIF<Object, Object, ContextIF>   getImplicitModel(){
    ModelIF<Object, Object, ContextIF> model = new GenericExplicitModel<Object, Object, ContextIF>(); 
    
    
    
        ContextDefinition ctxDef1 = new ContextDefinition("MomentOfDay");
        ctxDef1.addValue("day");
        ctxDef1.addValue("night");
        
        ContextDefinition ctxDef2 = new ContextDefinition("City");
        ctxDef2.addValue("Concepción");
        ctxDef2.addValue("Santiago");
             
        CategoricalContext _dayCtx = new CategoricalContext(ctxDef1, "day");
        CategoricalContext _nightCtx = new CategoricalContext(ctxDef1, "night");
        
        CategoricalContext _conceCtx = new CategoricalContext(ctxDef2, "Concepción");
        CategoricalContext _santCtx = new CategoricalContext(ctxDef2, "Santiago");
        
        ContextContainer dayCtx = new ContextContainer();
        dayCtx.add(_dayCtx);
        
         ContextContainer nightCtx = new ContextContainer();
        nightCtx.add(_nightCtx);

        ContextContainer conceCtx = new ContextContainer();
        conceCtx.add(_conceCtx);
        
        ContextContainer santCtx = new ContextContainer();
        santCtx.add(_santCtx);
        
        ContextContainer daySantCtx = new ContextContainer();
        daySantCtx.add(_dayCtx);
        daySantCtx.add(_santCtx);
        
        ContextContainer nightSantCtx = new ContextContainer();
        nightSantCtx.add(_nightCtx);
        nightSantCtx.add(_santCtx);
        
        ContextContainer dayConceCtx = new ContextContainer();
        dayConceCtx.add(_dayCtx);
        dayConceCtx.add(_conceCtx);
        
        ContextContainer nightConceCtx = new ContextContainer();
        nightConceCtx.add(_nightCtx);
        nightConceCtx.add(_conceCtx);
        
        listaContextDef = new ArrayList<ContextDefinition>();
        listaContextDef.add(ctxDef1);
//        listaContextDef.add(ctxDef2);
        
        

    for (int i = 0; i <10; i++) {
            
            model.addPreference(1, 1, (float)1.0, dayConceCtx);
            
        }  
    for (int i = 0; i <20; i++) {
            
            model.addPreference(1, 1, (float)1.0, daySantCtx);
            
        }
    for (int i = 0; i <30; i++) {
            
            model.addPreference(1, 1, (float)1.0, nightConceCtx);
            
        }
    for (int i = 0; i <25; i++) {
            
            model.addPreference(1, 1, (float)1.0, nightSantCtx);
            
        }
        for (int i = 0; i <40; i++) {
            
            model.addPreference(1, 2, (float)1.0, dayConceCtx);
            
        }  
    for (int i = 0; i <30; i++) {
            
            model.addPreference(1, 2, (float)1.0, daySantCtx);
            
        }
    for (int i = 0; i <23; i++) {
            
            model.addPreference(1, 2, (float)1.0, nightConceCtx);
            
        }
    for (int i = 0; i <24; i++) {
            
            model.addPreference(1, 2, (float)1.0, nightSantCtx);
            
        }
    for (int i = 0; i <22; i++) {
            
            model.addPreference(1, 3, (float)1.0, dayConceCtx);
            
        }  
    for (int i = 0; i <40; i++) {
            
            model.addPreference(1, 3, (float)1.0, daySantCtx);
            
        }
    for (int i = 0; i <22; i++) {
            
            model.addPreference(1, 3, (float)1.0, nightConceCtx);
            
        }
    for (int i = 0; i <18; i++) {
            
            model.addPreference(1, 3, (float)1.0, nightSantCtx);
            
        }
        for (int i = 0; i <23; i++) {
            
            model.addPreference(1, 4, (float)1.0, dayConceCtx);
            
        }  
    for (int i = 0; i <50; i++) {
            
            model.addPreference(1, 4, (float)1.0, daySantCtx);
            
        }
    for (int i = 0; i <23; i++) {
            
            model.addPreference(1, 4, (float)1.0, nightConceCtx);
            
        }
    for (int i = 0; i <24; i++) {
            
            model.addPreference(1, 4, (float)1.0, nightSantCtx);
            
        }
    
    ///
        for (int i = 0; i <10; i++) {
            
            model.addPreference(2, 1, (float)1.0, dayConceCtx);
            
        }  
    for (int i = 0; i <25; i++) {
            
            model.addPreference(2, 1, (float)1.0, daySantCtx);
            
        }
    for (int i = 0; i <30; i++) {
            
            model.addPreference(2, 1, (float)1.0, nightConceCtx);
            
        }
    for (int i = 0; i <50; i++) {
            
            model.addPreference(2, 1, (float)1.0, nightSantCtx);
            
        }
        for (int i = 0; i <20; i++) {
            
            model.addPreference(2, 2, (float)1.0, dayConceCtx);
            
        }  
    for (int i = 0; i <40; i++) {
            
            model.addPreference(2, 2, (float)1.0, daySantCtx);
            
        }
    for (int i = 0; i <25; i++) {
            
            model.addPreference(2, 2, (float)1.0, nightConceCtx);
            
        }
    for (int i = 0; i <40; i++) {
            
            model.addPreference(2, 2, (float)1.0, nightSantCtx);
            
        }
    for (int i = 0; i <30; i++) {
            
            model.addPreference(2, 3, (float)1.0, dayConceCtx);
            
        }  
    for (int i = 0; i <120; i++) {
            
            model.addPreference(2, 3, (float)1.0, daySantCtx);
            
        }
    for (int i = 0; i <50; i++) {
            
            model.addPreference(2, 3, (float)1.0, nightConceCtx);
            
        }
    for (int i = 0; i <5; i++) {
            
            model.addPreference(2, 3, (float)1.0, nightSantCtx);
            
        }
        for (int i = 0; i <40; i++) {
            
            model.addPreference(2, 4, (float)1.0, dayConceCtx);
            
        }  
    for (int i = 0; i <15; i++) {
            
            model.addPreference(2, 4, (float)1.0, daySantCtx);
            
        }
    for (int i = 0; i <8; i++) {
            
            model.addPreference(2, 4, (float)1.0, nightConceCtx);
            
        }
    for (int i = 0; i <900; i++) {
            
            model.addPreference(2, 4, (float)1.0, nightSantCtx);
            
        }
    

        return model;
    }    

      
    public static void Print(List<ContextIF> listacntxcont, ModelIF<Object, Object, ContextIF> model){
   
        for(ContextIF c:listacntxcont)
     
        { 
            System.out.println("");
            System.out.println("CONTEXTO: "+c);

            System.out.println("");
            for(Object u : model.getUsers())
            {
                System.out.print("U:"+u);
                System.out.print("\t");
                for(Object i: model.getItems())
                {
                    System.out.print(model.getPreferenceValue(u, i, (ContextIF)c)+"\t");        
                }
                System.out.println("");
            }    
    
        } 
         System.out.println("");
   }     
    
    
}
