package es.uam.eps.ir.experiments;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.split.impl.DatasetSplitterBuilder;
import es.uam.eps.ir.split.DatasetSplitterIF;
import es.uam.eps.ir.split.impl.Split;

/**
 *
 * @author pedro
 */
public class CommonDatasetSplitters<U,I,C extends ContextIF> {
    private double proportion = 0.085;
    private int size = 9;
    private DatasetIF<U,I,C> dataset;
    
    public enum METHOD {
        UserRandomOrderProportionHoldout,
        UserRandomOrderFixedHoldout,
        UserTimeOrderProportionHoldout,
        UserTimeOrderFixedHoldout,
        CommunityRandomOrderProportionHoldout,
        CommunityTimeOrderProportionHoldout,
        CommunityTimeOrderTimeProportionHoldout,
        CommunityTimeOrderFixedHoldout,
        PredefinedTest,
        CommunityXFold_CV,
        UserXFold_CV,
    }
    
    public CommonDatasetSplitters(){
    }
    
    public CommonDatasetSplitters proportion(double proportion){
        this.proportion = proportion;
        return this;
    }

    public CommonDatasetSplitters size(int size){
        this.size = size;
        return this;
    }

    public CommonDatasetSplitters dataset(DatasetIF<U,I,C> dataset){
        this.dataset = dataset;
        return this;
    }
    
    public DatasetSplitterIF<U,I,C> getDatasetSplitter(METHOD method){
        DatasetSplitterIF<U,I,C> splitter = null;
        switch (method){
            case UserRandomOrderProportionHoldout:
                return this.getUserRandomOrderProportionHoldout();
            case UserRandomOrderFixedHoldout:
                return this.getUserRandomOrderFixedHoldout();
            case UserTimeOrderProportionHoldout:
                return this.getUserTimeOrderProportionHoldout();
            case UserTimeOrderFixedHoldout:
                return this.getUserTimeOrderFixedHoldout();
            case CommunityRandomOrderProportionHoldout:
                return this.getCommunityRandomOrderProportionHoldout();
            case CommunityTimeOrderProportionHoldout:
                return this.getCommunityTimeOrderProportionHoldout();
            case CommunityTimeOrderTimeProportionHoldout:
                return this.getCommunityTimeOrderTimeProportionHoldout();
            case CommunityTimeOrderFixedHoldout:
                return this.getCommunityTimeOrderFixedHoldout();
            case CommunityXFold_CV:
                return this.getCommunityXfold_CV();
            case UserXFold_CV:
                return this.getUserXfold_CV();
                
            case PredefinedTest:
                return new PredefinedTestSplitter(dataset);
        }
        return splitter;
    }
    
    public DatasetSplitterIF<U,I,C> getUserRandomOrderProportionHoldout(){
        DatasetSplitterIF<U,I,C> splitter = new DatasetSplitterBuilder<U,I,C>()
                .userBase()
                .RandomOrder()
                .ProportionSize(proportion)
                .buildSplitter();
        return splitter;
    }

    public DatasetSplitterIF<U,I,C> getUserRandomOrderFixedHoldout(){
        DatasetSplitterIF<U,I,C> splitter = new DatasetSplitterBuilder<U,I,C>()
                .userBase()
                .RandomOrder()
                .FixedSize(size)
                .buildSplitter();
        return splitter;
    }

    public DatasetSplitterIF<U,I,C> getUserTimeOrderProportionHoldout(){
        DatasetSplitterIF<U,I,C> splitter = new DatasetSplitterBuilder<U,I,C>()
                .userBase()
                .TimeOrder()
                .ProportionSize(proportion)
                .buildSplitter();
        return splitter;
    }

    public DatasetSplitterIF<U,I,C> getUserTimeOrderFixedHoldout(){
        DatasetSplitterIF<U,I,C> splitter = new DatasetSplitterBuilder<U,I,C>()
                .userBase()
                .TimeOrder()
                .FixedSize(size)
                .buildSplitter();
        return splitter;
    }

    public DatasetSplitterIF<U,I,C> getCommunityRandomOrderProportionHoldout(){
        DatasetSplitterIF<U,I,C> splitter = new DatasetSplitterBuilder<U,I,C>()
                .communityBase()
                .RandomOrder()
                .ProportionSize(proportion)
                .buildSplitter();
        return splitter;
    }
    
    public DatasetSplitterIF<U,I,C> getCommunityTimeOrderProportionHoldout(){
        DatasetSplitterIF<U,I,C> splitter = new DatasetSplitterBuilder<U,I,C>()
                .communityBase()
                .TimeOrder()
                .ProportionSize(proportion)
                .buildSplitter();
        return splitter;
    }
    
    public DatasetSplitterIF<U,I,C> getCommunityTimeOrderTimeProportionHoldout(){
        DatasetSplitterIF<U,I,C> splitter = new DatasetSplitterBuilder<U,I,C>()
                .communityBase()
                .TimeOrder()
                .TimeProportionSize(proportion)
                .buildSplitter();
        return splitter;
    }
    
    public DatasetSplitterIF<U,I,C> getCommunityTimeOrderFixedHoldout(){
        DatasetSplitterIF<U,I,C> splitter = new DatasetSplitterBuilder<U,I,C>()
                .communityBase()
                .TimeOrder()
                .FixedSize(size)
                .buildSplitter();
        return splitter;
    }
    
    public DatasetSplitterIF<U,I,C> getCommunityXfold_CV(){
        DatasetSplitterIF<U,I,C> splitter = new DatasetSplitterBuilder<U,I,C>()
                .communityBase()
                .xFold_CV(size)
                .buildSplitter();
        return splitter;
    }
    
    public DatasetSplitterIF<U,I,C> getUserXfold_CV(){
        DatasetSplitterIF<U,I,C> splitter = new DatasetSplitterBuilder<U,I,C>()
                .userBase()
                .xFold_CV(size)
                .buildSplitter();
        return splitter;
    }
    
    
    public class PredefinedTestSplitter<U,I,C extends ContextIF> implements DatasetSplitterIF<U,I,C>{
        private ModelIF<U,I,C> testModel;

        public PredefinedTestSplitter(DatasetIF<U, I, C> dataset) {
            this.testModel = dataset.getPredefinedTestModel();
        }
        
        public SplitIF<U,I,C>[] split(ModelIF<U, I, C> model) {
            SplitIF<U,I,C> splits[] = new SplitIF[1];
            splits[0] = new Split(model, testModel);
            return splits;
        }
        
        @Override
        public String toString(){
            return "PredefinedTest";
        }
        
    }
}
