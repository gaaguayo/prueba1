package cars.context;

import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder;
import es.uam.eps.ir.cars.inferred.TimeContextDefinition;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.dataset.ContextualDatasetIF;
import es.uam.eps.ir.dataset.DatasetIF;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pedro
 */
public class ContextDefinitionManager {
    public static List<ContextDefinition> getContextDefinitions(DatasetIF<Object, Object, ContextIF> dataset, List<String> contextsToAnalyze){
        List<ContextDefinition> selectedCategoricalContextDefinitions = null;
//        List<ContinuousTimeContextComputerBuilder.TimeContext> selectedContinuousTimeContextDefinitions = null;
        // Slicer (for pre/post-filtering)
        if (contextsToAnalyze != null){
            if (dataset instanceof ContextualDatasetIF){
                List<ContextDefinition> allCategoricalContextDefinitions = ((ContextualDatasetIF)dataset).getContextDefinitions();
                selectedCategoricalContextDefinitions = new ArrayList<ContextDefinition>();
                for (String context : contextsToAnalyze){
                    for (ContextDefinition ctxDef : allCategoricalContextDefinitions){
                        if (ctxDef.getName().equalsIgnoreCase(context)){
                            selectedCategoricalContextDefinitions.add(ctxDef);
                        }
                    }
                    for (ContinuousTimeContextComputerBuilder.TimeContext tc : ContinuousTimeContextComputerBuilder.TimeContext.values()){
                        if (tc.name().equalsIgnoreCase(context)){
                            selectedCategoricalContextDefinitions.add(new TimeContextDefinition(context));                                
                        }
                    }
                }
            }
//                else{
//                    selectedContinuousTimeContextDefinitions = new ArrayList<TimeContext>();
//                    for (String context : filtering_contexts){
//                        for (TimeContext tc : TimeContext.values()){
//                            if (tc.name().equalsIgnoreCase(context)){
//                                selectedContinuousTimeContextDefinitions.add(tc);
//                            }
//                        }
//                    }
//                }
        }                        
        return selectedCategoricalContextDefinitions;
    }
}
