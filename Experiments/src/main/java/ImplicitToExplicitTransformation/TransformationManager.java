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
    List<ContextIF> listaContext;

    public TransformationManager(TransformationMethod method, TransformationLevel level, List<ContextDefinition> contextsToAnalize) {
        this.method = method;
        this.level = level;
        this.listaContextDef = contextsToAnalize;
    }
    
    private ContextAwereItoE getFeedbackAnalyzer(ModelIF<U, I, C> implicitFeedbackModel){
        switch(level){
            case  AllContexts:
                return new ContextUnited(implicitFeedbackModel,listaContextDef,method);
            case IndividualContext :
                return new ContextSeparator(implicitFeedbackModel,listaContextDef,method);
        }
        return null;
    } 

    
    
    public List<ContextIF> getlistcontext(){
        return listaContext;
    }
    
    public ModelIF<U, I, C> toExplicit(ModelIF<U, I, C> implicitFeedbackModel){

//         ContextAwereItoE c= new ContextSeparator(implicitFeedbackModel,listaContextDef, ContextSeparator.TransformationMethod.Basic);
//         ContextAwereItoE d =  new ContextUnited(implicitFeedbackModel,listaContextDef,method);
         ContextAwereItoE d =  getFeedbackAnalyzer(implicitFeedbackModel);
//         c.Separation();
         d.Separation();
         listaContext = d.getlistcontext();
         
         return d.getmodel();
    }
    
}
