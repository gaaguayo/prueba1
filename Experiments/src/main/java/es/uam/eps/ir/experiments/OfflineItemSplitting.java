package es.uam.eps.ir.experiments;

import es.uam.eps.ir.cars.model.ModelReaderIF;
import es.uam.eps.ir.cars.model.TrecFormatImplicitModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.dataset.ItemSplitDataset;
import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder.TimeContext;
import es.uam.eps.ir.cars.itemsplitting.ImpurityComputerIF;
import java.util.List;
import modelUtils.ModelPrintUtils;
import utils.PrintUtils;

/**
 *
 * @author Pedro G. Campos
 */
public class OfflineItemSplitting {
    
    public static void doItemSplitting(String[] args, CommonDatasets.DATASET dataset_name, ImpurityComputerIF is_impurity, List<TimeContext> is_timeContexts){
//        DatasetIF<Object, Object, ContinuousTimeContextIF> dataset = new CommonDatasets(args).item_split(true).is_impurity(is_impurity).is_timeContexts(is_timeContexts).getDataset(dataset_name);
        DatasetIF<Object, Object, ContinuousTimeContextIF> originalDataset = new CommonDatasets(args).getDataset(dataset_name);
        
        DatasetIF<Object, Object, ContinuousTimeContextIF> dataset;
        dataset = new ItemSplitDataset(originalDataset, is_impurity, is_timeContexts);

        ModelIF<Object, Object, ContinuousTimeContextIF> model = dataset.getModel();
        String filePrefix = dataset.getPath() + "itemsplitting"  + "/" + is_impurity + is_timeContexts + "__";
        PrintUtils.toGz(filePrefix + "model.txt.gz", ModelPrintUtils.printModel2(model));
    }
    
    public static ModelIF readItemSplitting(String[] args, CommonDatasets.DATASET dataset_name, ImpurityComputerIF is_impurity, List<TimeContext> is_timeContexts){
        DatasetIF<Object, Object, ContinuousTimeContextIF> dataset = new CommonDatasets(args).getDataset(dataset_name);
        String filePrefix = dataset.getPath() + "itemsplitting"  + "/" + is_impurity + is_timeContexts + "__";
        ModelReaderIF reader = new TrecFormatImplicitModelReader(true);
        return reader.readModel(filePrefix + "model.txt"); // gz extension not required!
    }
}
