package es.uam.eps.ir.dataset;

import es.uam.eps.ir.dataset.Context_IRG.*;
import es.uam.eps.ir.cars.model.CategoricalContextModelReader;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.dataset.AbstractDataset;
import es.uam.eps.ir.dataset.ContextualDatasetIF;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pedro
 */
public class Context_Adomavicius_Dataset<U,I,C extends ContextIF> extends AbstractDataset<U,I,C> implements ContextualDatasetIF<U,I,C>{

    public Context_Adomavicius_Dataset(String path, String file) {
        super(path, file);
    }
    
    public Context_Adomavicius_Dataset(String[] args) {
        super(args);
    }
    

    public Context_Adomavicius_Dataset() {
        super();
    }
    

    @Override
    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected final void setReader(){
        reader = new CategoricalContextModelReader<U,I,C>();
        CategoricalContextModelReader<U,I,C> theReader=(CategoricalContextModelReader<U,I,C>)reader;
        theReader.setDelimiter("\t");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setRatingIndex(2);
        theReader.addContext("company", 6, Arrays.asList("friends","parents","girlfriend-boyfriend","alone","siblings","spouse","children","colleagues"));
        theReader.addContext("daytype", 7, Arrays.asList("weekend","weekday","undefined"));
        theReader.addContext("weekend", 8, Arrays.asList("release","non_release","undefined"));
        theReader.setContainsTitleLine(true);
//        theReader.setIntegerKeys();
    }
    
    public List<ContextDefinition> getContextDefinitions(){
        CategoricalContextModelReader<U,I,C> theReader=(CategoricalContextModelReader<U,I,C>)reader;
        return theReader.getContextDefinitions();
    }
    
    public final String name(){
        return "Context_Adomavicius";
    }    
}
