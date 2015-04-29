package es.uam.eps.ir.dataset;

import es.uam.eps.ir.cars.model.ModelReaderIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author pedro
 */
public abstract class AbstractDataset<U,I,C extends ContextIF> implements DatasetIF<U,I,C>{
    protected ModelIF<U,I,C> model;
    protected ModelReaderIF<U,I,C> reader;
    protected ModelIF<U,I,C> testModel;
    protected String path;
    protected String file;
    protected String testFile;
    protected String resultsPath;
    
    public AbstractDataset(String path, String file){
        this.path = path;
        this.file = file;
        this.setReader();
    }
    
    public AbstractDataset(String[] args){
        DatasetUtility utility = null;
        boolean set_defined=false;
        for (String arg:args){
            if (arg.startsWith("set=")){
                set_defined = true;
            }
        }
        if (set_defined){
            utility = new DatasetUtility(args);
        }
        else {
            utility = new DatasetUtility(name());
        }
        this.path = utility.getPath();
        this.file = utility.getTrainingDataFile();
        this.testFile = utility.getTestDataFile();
        this.resultsPath = utility.getResultsPath();
        this.setReader();        
    }
    
    
    public AbstractDataset(){
        DatasetUtility utility = new DatasetUtility(name());
        this.path = utility.getPath();
        this.file = utility.getTrainingDataFile();
        this.testFile = utility.getTestDataFile();
        this.resultsPath = utility.getResultsPath();
        this.setReader();        
    }
    
    
    protected abstract void setReader();

    public ModelIF<U, I, C> getModel() {
        if (model == null){
            model = reader.readModel(path + file);
        }
        return model;
    }
    
    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        if (testModel == null){
            testModel = reader.readModel(path + testName);
        }
        return testModel;
    }
    
    public ModelIF<U, I, C> getPredefinedTestModel() {
        if (testModel == null){
            testModel = reader.readModel(path + testFile);
        }
        return testModel;
    }

    public String getPath(){
        return this.path;
    }
    
    public String getResultsPath(){
        return this.resultsPath;
    }
    
    public abstract String name();

    @Override
    public String toString(){
        return name();
    }
    
    public String getDetails(){
        return "original";
    }
}
