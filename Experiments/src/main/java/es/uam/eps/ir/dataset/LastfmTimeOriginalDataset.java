package es.uam.eps.ir.dataset;

import es.uam.eps.ir.cars.model.ContinuousTimeModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;

/**
 *
 * @author pedro
 */
public class LastfmTimeOriginalDataset<U,I,C extends ContinuousTimeContextIF> extends AbstractDataset<U,I,C> implements DatasetIF<U,I,C>{

    public LastfmTimeOriginalDataset(String path, String file) {
        super(path, file);
    }
    
    public LastfmTimeOriginalDataset(String[] args) {
        super(args);
    }
    

    public LastfmTimeOriginalDataset() {
        super();
    }
    

    protected final void setReader(){
        reader=new ContinuousTimeModelReader<U,I,C>();
        ContinuousTimeModelReader<U,I,C> theReader=(ContinuousTimeModelReader<U,I,C>)reader;
        theReader.setDelimiter("\t");
        theReader.setUserIndex(0);
        theReader.setItemIndex(3); // 3: Artist; 2: track
        theReader.setTimestampIndex(1);
        theReader.setTimestampFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        theReader.setContainsTitleLine(false);
        theReader.setImplicitData();
    }
    
    public final String name(){
        return "LastFM_Time";
    }    
}
