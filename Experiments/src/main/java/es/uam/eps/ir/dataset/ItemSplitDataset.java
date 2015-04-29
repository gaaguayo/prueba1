package es.uam.eps.ir.dataset;

import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder.TimeContext;
import es.uam.eps.ir.cars.itemsplitting.CategoricalContextItemSplitter;
import es.uam.eps.ir.cars.itemsplitting.ContextBasedItemSplitterIF;
import es.uam.eps.ir.cars.itemsplitting.ImpurityComputerIF;
import es.uam.eps.ir.cars.itemsplitting.TimeContextItemSplitter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pedro G. Campos
 */
public class ItemSplitDataset<U,I,C extends ContextIF> implements DatasetIF<U,I,C> {
    DatasetIF originalDataset;
    ModelIF<U,I,C> splitModel;
    ContextBasedItemSplitterIF itemSplitter;
    
    public ItemSplitDataset(DatasetIF dataset, ImpurityComputerIF impurityComputer, List<String> contexts) {
        this.originalDataset = dataset;
        if (dataset instanceof ContextualDatasetIF){
            List<ContextDefinition> allContextDefinitions = ((ContextualDatasetIF)originalDataset).getContextDefinitions();
            List<ContextDefinition> selectedContextDefinitions = new ArrayList<ContextDefinition>();
            for (String context : contexts){
                for (ContextDefinition ctxDef : allContextDefinitions){
                    if (ctxDef.getName().equalsIgnoreCase(context)){
                        selectedContextDefinitions.add(ctxDef);
                    }
                }
                
            }
            itemSplitter = new CategoricalContextItemSplitter(impurityComputer, selectedContextDefinitions);            
        }
        else{
            List<TimeContext> is_contexts = new ArrayList<TimeContext>();
                for (String context : contexts){
                    for (TimeContext tc : TimeContext.values()){
                        if (tc.name().equalsIgnoreCase(context)){
                            is_contexts.add(tc);
                        }
                    }
                }
            
            itemSplitter = new TimeContextItemSplitter(impurityComputer, is_contexts);            
        }
    }
    
    public ItemSplitDataset(DatasetIF dataset, ImpurityComputerIF impurityComputer) {
        this.originalDataset = dataset;
        itemSplitter = new CategoricalContextItemSplitter(impurityComputer, ((ContextualDatasetIF)dataset).getContextDefinitions());
    }
    
    public ModelIF<U, I, C> getModel() {
        if (splitModel == null){
            splitModel = itemSplitter.splitModel(originalDataset.getModel());
        }
        return splitModel;
    }

    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ModelIF<U, I, C> getPredefinedTestModel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPath() {
        return originalDataset.getPath();
    }

    public String getResultsPath() {
        return originalDataset.getResultsPath();
    }
    
    @Override
    public String toString(){
        return originalDataset.toString();
    }
    
    public String getDetails(){
        return "ItemSplit["  + itemSplitter + "]";
    }
}
