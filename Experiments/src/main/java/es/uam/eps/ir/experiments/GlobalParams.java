package es.uam.eps.ir.experiments;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * This class encapsulates global params required by the application
 * It implementes the Singleton pattern to maintain a unique instance of the class
 * @author pedro
 */
public class GlobalParams {
    private static GlobalParams theInstance;  // singleton
    
    private String dataset_configuration_file_path;
    private static final Logger logger = Logger.getLogger("ExperimentLog");
    
    private GlobalParams(){
    }
    
    public static GlobalParams getGlobalParams(){
        if (theInstance == null){
            theInstance = new GlobalParams();
        }
        return theInstance;
    }

    public static GlobalParams getGlobalParams(String config_file){
        if (theInstance == null){
            theInstance = new GlobalParams();
        }
        
        // check params file
        try{
            //Params reading
            String line;
            java.io.FileReader file=new java.io.FileReader(config_file);
            java.io.BufferedReader buffer=new java.io.BufferedReader(file);
            while ( (line=buffer.readLine())!=null){
                String[] values=line.split("=");
                if (values[0].equals("BASE_DATASET_CONFIG_FILE_PATH"))
                    theInstance.setDataset_configuration_file_path(values[1]);
                else if (values[0].equals("trainFile")){
                }
            }
            
        }catch(FileNotFoundException e){
            logger.log(Level.SEVERE, "{0} not found!", config_file);
        }catch(IOException e){
        }
        
        
        return theInstance;
    }
    
    // Getters and Setters of params
    public String getDataset_configuration_file_path() {
        return dataset_configuration_file_path;
    }

    public void setDataset_configuration_file_path(String dataset_configuration_file_path) {
        this.dataset_configuration_file_path = dataset_configuration_file_path;
    }
    
}
