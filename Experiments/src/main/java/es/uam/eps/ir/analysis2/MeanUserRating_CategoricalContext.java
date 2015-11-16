package es.uam.eps.ir.analysis2;

import es.uam.eps.ir.cars.contextualfiltering.ContextualSegmentIF;
import es.uam.eps.ir.cars.contextualfiltering.ContextualSlicerIF;
import es.uam.eps.ir.cars.contextualfiltering.ContextualSlicer_CategoricalContext;
import es.uam.eps.ir.cars.contextualfiltering.ContextualSlicer_ContinuousTimeContext;
import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.ContextualDatasetIF;
import es.uam.eps.ir.dataset.DatasetIF;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.LogFormatter;
import utils.PrintUtils;
import utils.SummaryPrinter;

/**
 * This class creates a tabulated file
 * of mean users ratings among
 * different values of a categorical context variable.
 * One user's data per row.
 * One categorical context per column
 * 
 * 
 * @author pedro
 */
public class MeanUserRating_CategoricalContext {
    // Computations
    static int minRatingsInContext = 3;
        
    // Dataset
    static CommonDatasets.DATASET dataset_name = CommonDatasets.DATASET.Netflix;
    static List<String> contextVariable = Arrays.asList("PeriodOfWeek"); // context variables used in pre- and post-filtering (PRF and POF)
    
    // Output
    static String RESULTS_PATH = "/datos/experiments/context/";
    
    final static Level level = Level.INFO;
    final static Logger logger = Logger.getLogger("AnalysisLog");

    public static void main( String[] args )
    {
        processArgs(args);
        exec_experiment(args);
    }
    
    public static void exec_experiment( String[] args ){
        // Logger init
        //////////////
        logger.setUseParentHandlers(false);
        logger.setLevel(level);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        handler.setFormatter(formatter);
        logger.addHandler(handler);

        logger.info("Starting analysis");
        
        final StringBuilder sb = new StringBuilder();
        final String newline = System.getProperty("line.separator");
        
        // Data load
        logger.log(Level.INFO, "Dataset: {0}", dataset_name);        
        logger.info("Loading data");
        DatasetIF<Object, Object, ContextIF> dataset = new CommonDatasets(args).getDataset(dataset_name);
        if (RESULTS_PATH == null){
            RESULTS_PATH = dataset.getResultsPath();
        }
        StringBuilder contextVars = new StringBuilder();
        contextVars.append(contextVariable.get(0));
        for (int i = 1; i < contextVariable.size(); i++){
            contextVars.append("_").append(contextVariable.get(i));
        }
        
        String USER_RESULTS_FILE = dataset_name + "_UserAvg_" + contextVars + ".tsv";
        String AVG_RESULTS_FILE = dataset_name + "_Avg.tsv";
        
        ModelIF<Object, Object, ContextIF> model = dataset.getModel();
        logger.info("Data loaded");
        final StringBuilder datasetInfo = new StringBuilder();
        datasetInfo.append(newline).append("DatasetSummary").append(newline);
        datasetInfo.append("Name\t").append(dataset).append(newline);
        datasetInfo.append(new SummaryPrinter().summary(model, null));
        System.out.print(datasetInfo);
        
        logger.log(Level.INFO, "Results path: {0}", RESULTS_PATH);        

        // Context
        List<ContextDefinition> selectedCategoricalContextDefinitions;
        List<ContinuousTimeContextComputerBuilder.TimeContext> selectedContinuousTimeContextDefinitions;
        // Slicer (for pre/post-filtering)
        ContextualSlicerIF<Object,Object, ContextIF> slicer = null;
        if (contextVariable != null){
            if (dataset instanceof ContextualDatasetIF){
                List<ContextDefinition> allCategoricalContextDefinitions = ((ContextualDatasetIF)dataset).getContextDefinitions();
                selectedCategoricalContextDefinitions = new ArrayList<ContextDefinition>();
                for (String context : contextVariable){
                    for (ContextDefinition ctxDef : allCategoricalContextDefinitions){
                        if (ctxDef.getName().equalsIgnoreCase(context)){
                            selectedCategoricalContextDefinitions.add(ctxDef);
                        }
                    }
                }
                slicer = new ContextualSlicer_CategoricalContext<Object,Object, ContextIF>(selectedCategoricalContextDefinitions);                
            }
            else{
                selectedContinuousTimeContextDefinitions = new ArrayList<ContinuousTimeContextComputerBuilder.TimeContext>();
                for (String context : contextVariable){
                    for (ContinuousTimeContextComputerBuilder.TimeContext tc : ContinuousTimeContextComputerBuilder.TimeContext.values()){
                        if (tc.name().equalsIgnoreCase(context)){
                            selectedContinuousTimeContextDefinitions.add(tc);
                        }
                    }
                }
                slicer = new ContextualSlicer_ContinuousTimeContext<Object,Object, ContextIF>(selectedContinuousTimeContextDefinitions);                
            }
        }                        
        
        
        Map<ContextualSegmentIF, ModelIF<Object,Object, ContextIF>> contextModelMap = new HashMap<ContextualSegmentIF, ModelIF<Object,Object, ContextIF>>();
        Map<ContextualSegmentIF, ContextualModelUtils<Object,Object, ContextIF>> contextModelUtilsMap = new HashMap<ContextualSegmentIF, ContextualModelUtils<Object,Object, ContextIF>>();
        
        for (ContextualSegmentIF segment:slicer.getSegments()){
            logger.log(Level.INFO, "Building model for segment {0}", segment);

            final ModelIF<Object,Object, ContextIF> contextSegmentModel=slicer.getSegmentData(model, segment);
            final ContextualModelUtils<Object,Object, ContextIF> cmu = new ContextualModelUtils<Object,Object, ContextIF>(contextSegmentModel);
            contextModelMap.put(segment, contextSegmentModel);
            contextModelUtilsMap.put(segment, cmu);
        }
        
        
        Collection<Object> users = model.getUsers();
        sb.append("User");
        for (ContextualSegmentIF segment:slicer.getSegments()){
            sb.append("\t").append(segment);
        }
        sb.append(newline);
        for (Object user : users){
            sb.append(user);
            for (ContextualSegmentIF segment:slicer.getSegments()){
                ModelIF<Object,Object, ContextIF> contextSegmentModel = contextModelMap.get(segment);
                ContextualModelUtils<Object,Object, ContextIF> cmu = contextModelUtilsMap.get(segment);
                if (cmu.getUserFeedbackRecordsCount(user) >= minRatingsInContext){
                    sb.append("\t").append(cmu.getUserMeanRating(user));
                }
                else{
                    sb.append("\tNaN");
                }
            }
            sb.append(newline);
        }

        String userOutput = RESULTS_PATH + USER_RESULTS_FILE;
        PrintUtils.cleanFile(userOutput);
        PrintUtils.toFile(userOutput, sb.toString());
//        System.out.print(sb);
        
        // Context averages
        StringBuilder sbAll = new StringBuilder();
        StringBuilder sbAllTitleLine = new StringBuilder();
        sbAllTitleLine.append("Dataset\tContextVars\tContextSegment\tvalue").append(newline);
        String avgOutput = RESULTS_PATH + AVG_RESULTS_FILE;
        

        File f = new File(avgOutput);
        if (!f.exists()){
            PrintUtils.toFile(avgOutput, sbAllTitleLine.toString());
        }
        
        for (ContextualSegmentIF segment:slicer.getSegments()){
            sbAll.append(dataset_name).append("\t").append(contextVars).append("\t").append(segment).append("\t");
            ContextualModelUtils<Object,Object, ContextIF> cmu = contextModelUtilsMap.get(segment);
            if (cmu.getFeedbackRecordsCount() >= minRatingsInContext){
                sbAll.append(cmu.getMeanRating());
            }
            else{
                sbAll.append("NaN");
            }
            sbAll.append(newline);
        }
        
        
                
        PrintUtils.toFile(avgOutput, sbAll.toString(),true);
        System.out.print(sbAllTitleLine);
        System.out.print(sbAll);
        
        System.exit(0);
    }
    
  protected static void processArgs(String[] args){
        // process parameters
        for (String arg:args){
            if (arg.startsWith("dataset")){
                String[] sm = arg.split("=");
                dataset_name = null;
                for (CommonDatasets.DATASET _dataset : CommonDatasets.DATASET.values()){
                    if (_dataset.name().equalsIgnoreCase(sm[1])){
                        dataset_name = _dataset;
                    }
                }                   
                if (dataset_name==null){
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }
            }
            else if (arg.startsWith("context")){
                contextVariable = new ArrayList<String>();
                String[] sm = arg.split("=");
                contextVariable.addAll(Arrays.asList(sm[1].split(",")));
            }
            else if (arg.startsWith("results_path")){
                String[] sm = arg.split("=");
                RESULTS_PATH = sm[1];
            }
            else if (arg.startsWith("min_ratings")){
                String[] sm = arg.split("=");
                minRatingsInContext = Integer.parseInt(sm[1]);
            }
            else{
                System.err.println("unrecognized option: " + arg);
                System.exit(1);
            }            
        }
    }
}
