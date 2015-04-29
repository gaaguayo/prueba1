package es.uam.eps.ir.experiments;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.split.impl.Split;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class SplitFilterer<U,I,C extends ContextIF> {
    private static final Logger logger = Logger.getLogger("ExperimentLog");
    private String filtersInfoString;  
    private String filtersString;
    
    public SplitIF<U,I,C> processSplit(SplitIF<U,I,C> split, FilterUtil.FILTER[] trainFilters, FilterUtil.FILTER[] testFilters){
        final StringBuilder filtersInfo = new StringBuilder();
        final StringBuilder filters = new StringBuilder();
        final String newline = System.getProperty("line.separator");
        logger.info("Applying filters");
        
        ModelIF<U,I,C> trainSet = split.getTrainingSet();
        ModelIF<U,I,C> testSet  = split.getTestingSet();
        
        filtersInfo.append("trainingFilters\t");
        filters.append("TrF_");
        // Training data filter
        FilterUtil<U,I,C> trainFilter = new FilterUtil().knownData(trainSet);
        for (FilterUtil.FILTER filter:trainFilters){
            trainFilter.addFilter(filter);
            filtersInfo.append(filter).append(",");
            filters.append(filter);
        }
        trainSet = trainFilter.filter(trainSet);
        filtersInfo.append(newline);
        filters.append("_");

        filtersInfo.append("testFilters\t");
        filters.append("TeF_");
        // Test data filter
        FilterUtil<U,I,C> testFilter = new FilterUtil().knownData(trainSet);
        for (FilterUtil.FILTER filter:testFilters){
            testFilter.addFilter(filter);
            filtersInfo.append(filter).append(",");
            filters.append(filter);
        }
        testSet = testFilter.filter(testSet);
        filtersInfo.append(newline);
        filtersInfoString = filtersInfo.toString();
        filtersString = filters.toString();
        System.out.print(filtersInfo);
        
        SplitIF<U,I,C> filteredSplit = new Split(trainSet, testSet);
        return filteredSplit;
    }
    
    public String getFiltersDetails(){
        return filtersInfoString;
    }    

    public String getFilters(){
        return filtersString;
    }    
}
