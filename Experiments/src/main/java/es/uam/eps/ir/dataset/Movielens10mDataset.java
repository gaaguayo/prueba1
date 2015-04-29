package es.uam.eps.ir.dataset;

import es.uam.eps.ir.cars.model.ContinuousTimeModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author pedro
 */
public class Movielens10mDataset<U,I,C extends ContinuousTimeContextIF> extends AbstractDataset<U,I,C> implements DatasetIF<U,I,C>{

    public Movielens10mDataset(String path, String file) {
        super(path, file);
    }
    
    public Movielens10mDataset(String[] args) {
        super(args);
    }
    

    public Movielens10mDataset() {
        super();
    }
    

    @Override
    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected final void setReader(){
        reader = new ContinuousTimeModelReader<U,I,C>();
        ContinuousTimeModelReader<U,I,C> theReader=(ContinuousTimeModelReader<U,I,C>)reader;
        theReader.setDelimiter("::");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setRatingIndex(2);
        theReader.setTimestampIndex(3);
        theReader.setTimestampFormat("\"yyyy-MM-dd HH:mm:ss\"");
        theReader.setContainsTitleLine(false);
//        theReader.setIntegerKeys();
    }
    
    public final String name(){
        return "MovieLens10m";
    }    
}
