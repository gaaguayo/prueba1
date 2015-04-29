package es.uam.eps.ir.dataset.MovieLens1m;

import es.uam.eps.ir.cars.model.ContinuousTimeModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.dataset.AbstractDataset;
import es.uam.eps.ir.dataset.DatasetIF;

/**
 *
 * @author pedro
 */
public class Movielens1mDataset<U,I,C extends ContinuousTimeContextIF> extends AbstractDataset<U,I,C> implements DatasetIF<U,I,C>{

    public Movielens1mDataset(String path, String file) {
        super(path, file);
    }
    
    public Movielens1mDataset(String[] args) {
        super(args);
    }
    

    public Movielens1mDataset() {
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
        return "MovieLens1m";
    }    
}
