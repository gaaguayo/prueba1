package es.uam.eps.ir.dataset;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.dataset.Context_IRG.Context_Movies_IRG_Dataset;
import es.uam.eps.ir.dataset.Context_IRG.Context_Musicians_IRG_Dataset;

/**
 *
 * @author pedro
 */
public class ContextualDatasets<U,I,C extends ContextIF> {
    public enum DATASET{
        Context_Movies_IRG,
        Context_Musicians_IRG,
    }
    
    private String[] args;
//    private boolean item_splitting = false;
//    ImpurityComputerIF impurity;
//    private List<ContextDefinition> ContextDefinitions;
//    private int sample;

    public ContextualDatasets(String[] args) {
        this.args = args;
    }
    
//    public ContextualDatasets item_split(boolean item_split){
//        this.item_splitting = item_split;
//        return this;
//    }
//    public ContextualDatasets is_impurity(ImpurityComputerIF impurity){
//        this.impurity = impurity;
//        return this;
//    }
//
    public DatasetIF<U,I,C> getDataset(DATASET dataset_name){
        DatasetIF<U,I,C> dataset = null;
        switch (dataset_name){
            case Context_Movies_IRG:
                dataset = new Context_Movies_IRG_Dataset(args);
                break;
            case Context_Musicians_IRG:
                dataset = new Context_Musicians_IRG_Dataset(args);
                break;
        }
//        if (item_splitting){
//            dataset = new ItemSplitDataset(dataset, impurity);
//        }
        
        return dataset;
    }
    
    
}
