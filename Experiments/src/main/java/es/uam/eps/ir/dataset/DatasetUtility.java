package es.uam.eps.ir.dataset;

import es.uam.eps.ir.experiments.GlobalParams;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class DatasetUtility {
    // Set data
    String set_name;
    String test_name;
    String base_path;
    int split;

    // Access to data files
    String paramsFile;
    String sets_path;
    String results_path;
    String trainFile;
    String testFile;
    String friendsFile;
    
    Map<String,String> paramValueMap;
    java.io.FileWriter rankingFile;
    Logger logger;
    
    public DatasetUtility(String[] args){
        logger = Logger.getLogger("ExperimentLog");
        for (String arg:args){
            String[] param=arg.split("=");
            if (param[0].compareTo("set")==0)
                set_name=param[1];
            else if (param[0].compareTo("test")==0)
                test_name=param[1];
            else if (param[0].compareTo("base_path")==0)
                base_path=param[1];
            else if (param[0].compareTo("split")==0)
                split=new Integer(param[1]);
        }
        this.loadParams(set_name);
    }
    
    public DatasetUtility(String datasetName){
        logger = Logger.getLogger("ExperimentLog");
        set_name = datasetName;
        this.loadParams(set_name);        
    }    
    
    private void loadParams(String set_name){
        GlobalParams params = GlobalParams.getGlobalParams();
        this.base_path = params.getDataset_configuration_file_path();
        paramValueMap=new HashMap();
        String paramsFileName="params";
        String paramsFileExt=".txt";
        paramsFileName=paramsFileName.concat(set_name);
        
        // check params file
        try{
            paramsFile=System.getProperty("user.home")+"/recommender/"+paramsFileName+paramsFileExt;
            java.io.FileReader file=new java.io.FileReader(paramsFile);            
        }
        catch(FileNotFoundException e){
            logger.log(Level.WARNING, "{0} not found!", paramsFile);
            paramsFile=base_path+paramsFileName+paramsFileExt;
        }
        
        if (logger.isLoggable(Level.INFO)){
            String message="paramsFile:"+paramsFile;
            logger.info(message);
        }
        
        try{
            //Params reading
            String line;
            java.io.FileReader file=new java.io.FileReader(paramsFile);
            java.io.BufferedReader buffer=new java.io.BufferedReader(file);
            while ( (line=buffer.readLine())!=null){
                String[] values=line.split("=");
                paramValueMap.put(values[0], values[1]);
                if (values[0].equals("sets_path"))
                    sets_path=values[1];
                else if (values[0].equals("trainFile")){
                    trainFile=values[1];
                }
                else if (values[0].equals("testFile")){
                    testFile=values[1];
                }
                else if (values[0].equals("friendsFile")){
                    friendsFile=values[1];
                }
                else if (values[0].equals("results_path")){
                    results_path=values[1];
                }
            }
            
        }catch(FileNotFoundException e){
            logger.log(Level.SEVERE, "{0} not found!", paramsFile);
        }catch(IOException e){
        }
        if (this.getTestName() != null){
           this.setPath(this.getPath()+this.getTestName());
        }
        if (this.getResultsPath() == null){
           this.results_path=this.sets_path+"/results/" ;
        }
        else {
            this.results_path=this.results_path+"/";
        }
        this.setPath(this.getPath()+"/");
        if (logger.isLoggable(Level.INFO)){
            String message="datos:"+sets_path;
            logger.info(message);
            message="results:"+results_path;
            logger.info(message);
        }
    }
    
    String getParamValue(String param){
        return paramValueMap.get(param);
    }
    
    public String getSetName() {return set_name;}
    public String getTestName() {return test_name;}
    public String getBasePath() {return base_path;}
    public String getPath() {return sets_path;}
    public FileWriter getRankingFile() {return rankingFile;}
    public String getResultsPath() {return results_path;}
    public String getTestDataFile() {return testFile;}
    public String getTrainingDataFile() {return trainFile;}
    public void setPath(String path){ this.sets_path=path;}
    public void setResultsPath(String path) {this.results_path=path;}
    
}
