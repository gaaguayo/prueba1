package es.uam.eps.ir.dataset;

import es.uam.eps.ir.cars.model.ContinuousTimeModelReader;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pedro
 */
public class Movielens100kDataset<U,I,C extends ContinuousTimeContextIF> extends AbstractDataset<U,I,C> implements ContextualDatasetIF<U,I,C>{

    public Movielens100kDataset(String path, String file) {
        super(path, file);
    }
    
    public Movielens100kDataset(String[] args) {
        super(args);
    }
    

    public Movielens100kDataset() {
        super();
    }
    

    @Override
    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected final void setReader(){
        reader=new ContinuousTimeModelReader<U,I,C>();
        ContinuousTimeModelReader<U,I,C> theReader=(ContinuousTimeModelReader<U,I,C>)reader;
        theReader.setDelimiter("\t");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setRatingIndex(2);
        theReader.setTimestampIndex(3);
        theReader.setTimestampFormat("\"yyyy-MM-dd HH:mm:ss\"");
        theReader.setContainsTitleLine(false);
//        theReader.setIntegerKeys();
    }
    
    public final String name(){
        return "MovieLens100k";
    }    

    @Override
    public List<ContextDefinition> getContextDefinitions() {
        return new ArrayList();
    }
}
