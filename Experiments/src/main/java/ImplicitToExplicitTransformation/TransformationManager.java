/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImplicitToExplicitTransformation;

import ImplicitToExplicitTransformation.ContextAwereItoE;
import ImplicitToExplicitTransformation.ContextSeparator;
import ImplicitToExplicitTransformation.ContextUnited;
import es.uam.eps.ir.core.context.CategoricalContext;
import es.uam.eps.ir.core.context.ContextContainer;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.impl.GenericExplicitModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gaby
 */
public class TransformationManager<U, I, C extends ContextIF> {
    
    public enum TransformationMethod{
        Basic,
        Adjustment,
        Celma,
        Final;
    }

    public enum TransformationLevel{
        IndividualContext,
        AllContexts;
    }
    
    TransformationMethod method;
    TransformationLevel level;
    
    List<ContextDefinition> listaContextDef;

    public TransformationManager(TransformationMethod method, TransformationLevel level, List<ContextDefinition> contextsToAnalize) {
        this.method = method;
        this.level = level;
        this.listaContextDef = contextsToAnalize;
    }
    
    private ContextAwereItoE getFeedbackAnalyzer(ModelIF<U, I, C> implicitFeedbackModel){
        switch(level){
            case IndividualContext:
                return new ContextUnited(implicitFeedbackModel,listaContextDef,method);
            case AllContexts:
                return new ContextSeparator(implicitFeedbackModel,listaContextDef,method);
        }
        return null;
    } 

    
    public ModelIF<Object, Object, ContextIF>   getImplicitModel(){
    ModelIF<Object, Object, ContextIF> model = new GenericExplicitModel<Object, Object, ContextIF>(); 
    
    
    
    ContextDefinition ctxDef1 = new ContextDefinition("MomentOfDay");
        ctxDef1.addValue("day");
        ctxDef1.addValue("night");
        
        ContextDefinition ctxDef2 = new ContextDefinition("MomentOfWeek");
        ctxDef2.addValue("Week");
        ctxDef2.addValue("Weekend");
        
        ContextDefinition ctxDef3 = new ContextDefinition("Season");
        ctxDef3.addValue("Winter");
        ctxDef3.addValue("Summer");
        
        CategoricalContext _dayCtx = new CategoricalContext(ctxDef1, "day");
        CategoricalContext _nightCtx = new CategoricalContext(ctxDef1, "night");
        
        CategoricalContext _weekCtx = new CategoricalContext(ctxDef2, "Week");
        CategoricalContext _weekendCtx = new CategoricalContext(ctxDef2, "Weekend");
        
        CategoricalContext _winterCtx = new CategoricalContext(ctxDef3, "Winter");
        CategoricalContext _summerCtx = new CategoricalContext(ctxDef3, "Summer");
        
        ContextContainer dayCtx = new ContextContainer();
        dayCtx.add(_dayCtx);
        
         ContextContainer nightCtx = new ContextContainer();
        nightCtx.add(_nightCtx);

        ContextContainer WeekCtx = new ContextContainer();
        WeekCtx.add(_weekCtx);
        
        ContextContainer nightWeekendWinterCtx = new ContextContainer();
        nightWeekendWinterCtx.add(_nightCtx);
        nightWeekendWinterCtx.add(_weekendCtx);
        nightWeekendWinterCtx.add(_winterCtx);
        
        ContextContainer dayWeekendWinterCtx = new ContextContainer();//
        dayWeekendWinterCtx.add(_dayCtx);
        dayWeekendWinterCtx.add(_weekendCtx);
        dayWeekendWinterCtx.add(_winterCtx);
        
        ContextContainer nightWeekendCtx = new ContextContainer();
        nightWeekendCtx.add(_nightCtx);
        nightWeekendCtx.add(_weekendCtx);
        
        ContextContainer nightWeekSummerCtx = new ContextContainer();//
        nightWeekSummerCtx.add(_nightCtx);
        nightWeekSummerCtx.add(_weekCtx);
        nightWeekSummerCtx.add(_summerCtx);
 
        listaContextDef = new ArrayList<ContextDefinition>();
        listaContextDef.add(ctxDef1);
        listaContextDef.add(ctxDef2);
      
    for (int i = 0; i <10; i++) {
            
            model.addPreference(1, 1, (float)1.0, nightWeekSummerCtx);
            
        }
//        for (int i = 0; i <10; i++) {
            
            model.addPreference(1, 1, (float)1.0, nightWeekendCtx);
            
//        }
         for (int i = 0; i <10; i++) {
            
            model.addPreference(1, 1, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <4; i++) {
            
            model.addPreference(1, 2, (float)1.0, nightWeekSummerCtx);
            
        }
         for (int i = 0; i <6; i++) {
            
            model.addPreference(1, 2, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <1; i++) {
            
            model.addPreference(1, 3, (float)1.0, nightWeekSummerCtx);
            
        }
        for (int i = 0; i <1; i++) {
            
            model.addPreference(1, 3, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <3; i++) {
            
            model.addPreference(1, 4, (float)1.0, nightWeekSummerCtx);
            
        }
        for (int i = 0; i <2; i++) {
            
            model.addPreference(1, 4, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <100; i++) {
            
            model.addPreference(2, 1, (float)1.0, nightWeekSummerCtx);
            
        }
        
         for (int i = 0; i <200; i++) {
            
            model.addPreference(2, 1, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <2; i++) {
            
            model.addPreference(2, 2, (float)1.0, nightWeekSummerCtx);
            
        }
         for (int i = 0; i <1; i++) {
            
            model.addPreference(2, 2, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <1; i++) {
            
            model.addPreference(2, 3, (float)1.0, nightWeekSummerCtx);
            
        }
        for (int i = 0; i <1; i++) {
            
            model.addPreference(2, 3, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <2; i++) {
            
            model.addPreference(2, 4, (float)1.0, nightWeekSummerCtx);
            
        }
        for (int i = 0; i <2; i++) {
            
            model.addPreference(2, 4, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <5; i++) {
            
            model.addPreference(3, 1, (float)1.0, nightWeekSummerCtx);
            
        }
        
         for (int i = 0; i <2; i++) {
            
            model.addPreference(3, 1, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <1; i++) {
            
            model.addPreference(3, 2, (float)1.0, nightWeekSummerCtx);
            
        }
         for (int i = 0; i <2; i++) {
            
            model.addPreference(3, 2, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <1; i++) {
            
            model.addPreference(3, 3, (float)1.0, nightWeekSummerCtx);
            
        }
        for (int i = 0; i <2; i++) {
            
            model.addPreference(3, 3, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <3; i++) {
            
            model.addPreference(3, 4, (float)1.0, nightWeekSummerCtx);
            
        }
        for (int i = 0; i <4; i++) {
            
            model.addPreference(3, 4, (float)1.0, dayWeekendWinterCtx);
            
        }
           for (int i = 0; i <1; i++) {
            
            model.addPreference(4, 1, (float)1.0, nightWeekSummerCtx);
            
        }
        
         for (int i = 0; i <1; i++) {
            
            model.addPreference(4, 1, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <1; i++) {
            
            model.addPreference(4, 2, (float)1.0, nightWeekSummerCtx);
            
        }
         for (int i = 0; i <0; i++) {
            
            model.addPreference(4, 2, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <1; i++) {
            
            model.addPreference(4, 3, (float)1.0, nightWeekSummerCtx);
            
        }
        for (int i = 0; i <1; i++) {
            
            model.addPreference(4, 3, (float)1.0, dayWeekendWinterCtx);
            
        }
        for (int i = 0; i <3; i++) {
            
            model.addPreference(4, 4, (float)1.0, nightWeekSummerCtx);
            
        }
        for (int i = 0; i <3; i++) {
            
            model.addPreference(4, 4, (float)1.0, dayWeekendWinterCtx);
            
        }

        return model;
    }
    
    public ModelIF<U, I, C> toExplicit(ModelIF<U, I, C> implicitFeedbackModel){

//         ContextAwereItoE c= new ContextSeparator(implicitFeedbackModel,listaContextDef, ContextSeparator.TransformationMethod.Basic);
//         ContextAwereItoE d =  new ContextUnited(implicitFeedbackModel,listaContextDef,method);
         ContextAwereItoE d =  getFeedbackAnalyzer(implicitFeedbackModel);
//         c.Separation();
         d.Separation();
         
         return d.getmodel();
    }
    
}
