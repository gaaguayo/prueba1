package es.uam.eps.ir.dataset;

import es.uam.eps.ir.cars.model.TimestampedCategoricalContextModelReader;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pedro
 */
public class FoursquareQuadrantsCategoricalDataset<U,I,C extends ContextIF> extends AbstractDataset<U,I,C> implements ContextualDatasetIF<U,I,C>{

    public FoursquareQuadrantsCategoricalDataset(String path, String file) {
        super(path, file);
    }
    
    public FoursquareQuadrantsCategoricalDataset(String[] args) {
        super(args);
    }
    

    public FoursquareQuadrantsCategoricalDataset() {
        super();
    }
    

    @Override
    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected final void setReader(){
        reader = new TimestampedCategoricalContextModelReader<>();
        TimestampedCategoricalContextModelReader<U,I,C> theReader=(TimestampedCategoricalContextModelReader<U,I,C>)reader;
        theReader.setDelimiter("\t");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setTimestampIndex(3);
        theReader.setTimestampFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        
        int firstID=1;
        int lastID=300;
        List<String> IDs = new ArrayList<>();
        for (int i = firstID; i <= lastID; i++){
            IDs.add(Integer.toString(i));
        }
//        theReader.addContext("cuadrante", 2, IDs);
        theReader.setContainsTitleLine(false);
        theReader.setImplicitData();
//        theReader.setIntegerKeys();
    }
    
    public List<ContextDefinition> getContextDefinitions(){
        TimestampedCategoricalContextModelReader<U,I,C> theReader=(TimestampedCategoricalContextModelReader<U,I,C>)reader;
        return theReader.getContextDefinitions();
    }
    
    public final String name(){
        return "FoursquareQuadrantsCategorical";
    }    
}
