package dataset.Foursquare;

import es.uam.eps.ir.cars.model.ContinuousTimeModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.dataset.AbstractDataset;
import es.uam.eps.ir.dataset.DatasetIF;

/**
 *
 * @author pedro
 */
public class FoursquareQuadrants_oldDataset<U,I,C extends ContinuousTimeContextIF> extends AbstractDataset<U,I,C> implements DatasetIF<U,I,C>{

    public FoursquareQuadrants_oldDataset(String path, String file) {
        super(path, file);
    }
    
    public FoursquareQuadrants_oldDataset(String[] args) {
        super(args);
    }
    

    public FoursquareQuadrants_oldDataset() {
        super();
    }
    

    protected final void setReader(){
        reader=new ContinuousTimeModelReader<U,I,C>();
        ContinuousTimeModelReader<U,I,C> theReader=(ContinuousTimeModelReader<U,I,C>)reader;
        theReader.setDelimiter("\t");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setTimestampIndex(3);
        theReader.setTimestampFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        theReader.setContainsTitleLine(false);
        theReader.setImplicitData();
    }
    
    public final String name(){
        return "FoursquareQuadrants";
    }    
}
