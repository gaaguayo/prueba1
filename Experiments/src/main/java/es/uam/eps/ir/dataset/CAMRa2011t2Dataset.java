package es.uam.eps.ir.dataset;

import es.uam.eps.ir.cars.model.ContinuousTimeModelReader;
import es.uam.eps.ir.cars.model.GenericModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;

/**
 *
 * @author pedro
 */
public class CAMRa2011t2Dataset<U,I,C extends ContinuousTimeContextIF> extends AbstractDataset<U,I,C> implements DatasetIF<U,I,C>{

    public CAMRa2011t2Dataset(String path, String file) {
        super(path, file);
    }
    
    public CAMRa2011t2Dataset(String[] args) {
        super(args);
    }
    

    public CAMRa2011t2Dataset() {
        super();
    }
        
    protected final void setReader(){
        reader=new ContinuousTimeModelReader<U,I,C>();
        ContinuousTimeModelReader<U,I,C> theReader=(ContinuousTimeModelReader<U,I,C>)reader;
        theReader.setDelimiter(",");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setRatingIndex(2);
        theReader.setTimestampIndex(3);
        theReader.setTimestampFormat("\"yyyy-MM-dd HH:mm:ss\"");
        theReader.setContainsTitleLine(true);        
//        theReader.setIntegerKeys();
    }
    
    public final String name(){
        return "CAMRa2011t2";
    }    
}
