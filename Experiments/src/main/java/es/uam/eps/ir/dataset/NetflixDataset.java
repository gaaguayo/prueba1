package es.uam.eps.ir.dataset;

import es.uam.eps.ir.cars.model.ContinuousTimeModelReader;
import es.uam.eps.ir.cars.model.TrecFormatModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author pedro
 */
public class NetflixDataset<U,I,C extends ContinuousTimeContextIF> extends AbstractDataset<U,I,C> implements DatasetIF<U,I,C>{

    public NetflixDataset(String path, String file) {
        super(path, file);
    }
    
    public NetflixDataset(String[] args) {
        super(args);
    }
    

    public NetflixDataset() {
        super();
    }
    

    @Override
    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected final void setReader(){
        reader=new TrecFormatModelReader<U,I,C>(true);
        TrecFormatModelReader<U,I,C> theReader=(TrecFormatModelReader<U,I,C>)reader;
        theReader.setTimestampFormat("yyyy-MM-dd");
    }
    
    public final String name(){
        return "Netflix";
    }    
}
