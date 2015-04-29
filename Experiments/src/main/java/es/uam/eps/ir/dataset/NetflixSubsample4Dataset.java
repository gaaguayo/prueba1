package es.uam.eps.ir.dataset;

import es.uam.eps.ir.cars.model.TrecFormatModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author pedro
 */
public class NetflixSubsample4Dataset<U,I,C extends ContinuousTimeContextIF> extends AbstractDataset<U,I,C> implements DatasetIF<U,I,C>{
    private final int sample = 4;

    public NetflixSubsample4Dataset(String path, String file) {
        super(path, file);
    }
    
    public NetflixSubsample4Dataset(String[] args) {
        super(args);
    }
    

    public NetflixSubsample4Dataset() {
        super();
    }
    

    @Override
    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected final void setReader(){
        reader=new TrecFormatModelReader<U,I,C>(true);
    }
    
    public final String name(){
        return "Netflix_LathiaSubsample_"+sample;
    }    
}
