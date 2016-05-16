package dataset.Foursquare;

import es.uam.eps.ir.cars.model.TimestampedCategoricalContextModelReader;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.dataset.AbstractDataset;
import es.uam.eps.ir.dataset.ContextualDatasetIF;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pedro
 */
public class FoursquareQuadrantsDataset<U,I,C extends ContextIF> extends AbstractDataset<U,I,C> implements ContextualDatasetIF<U,I,C>{

    public FoursquareQuadrantsDataset(String path, String file) {
        super(path, file);
    }
    
    public FoursquareQuadrantsDataset(String[] args) {
        super(args);
    }
    

    public FoursquareQuadrantsDataset() {
        super();
    }
    

    @Override
    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected final void setReader(){
        reader = new TimestampedCategoricalContextModelReader<U,I,C>();
        TimestampedCategoricalContextModelReader<U,I,C> theReader=(TimestampedCategoricalContextModelReader<U,I,C>)reader;
        theReader.setDelimiter("\t");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setTimestampIndex(2);
        theReader.setTimestampFormat("dd-MM-yyyy HH:mm:ss");
        theReader.setRatingIndex(8);
        
//        int firstID=1;
//        int lastID=2;
//        List<String> IDs = new ArrayList<String>();
//        for (int i = firstID; i <= lastID; i++){
//            IDs.add(Integer.toString(i));
//        }
//        theReader.addContext("quadrant", 2, IDs);
        
        theReader.addContext("quadrant2", 5, Arrays.asList("1","2"));
        theReader.addContext("quadrant4", 6, Arrays.asList("1","2","3","4"));
        theReader.addContext("quadrant8", 7, Arrays.asList("1","2","3","4","5","6","7","8"));
        
        theReader.setContainsTitleLine(false);
//        theReader.setImplicitData();
//        theReader.setIntegerKeys();
    }
    
    public List<ContextDefinition> getContextDefinitions(){
        TimestampedCategoricalContextModelReader<U,I,C> theReader=(TimestampedCategoricalContextModelReader<U,I,C>)reader;
        return theReader.getContextDefinitions();
    }
    
    public final String name(){
        return "FoursquareQuadrants";
    }    
}
