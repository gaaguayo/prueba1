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
        theReader.addContext("company", 6, Arrays.asList("1","2","3","4","5","6","7","8")); // 1:"friends",2:"parents",3:"girlfriend-boyfriend",4:"alone",5:"siblings",6:"spouse",7:"children",8:"colleagues al work"
        theReader.addContext("daytype", 7, Arrays.asList("1","2","3")); // 1:"weekend",2:"weekday",3:"dont'remember"
        theReader.addContext("release_weekend", 8, Arrays.asList("1","2","3")); //1:"release",2:"non_release",3:"undefined"
        theReader.setContainsTitleLine(false);
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
