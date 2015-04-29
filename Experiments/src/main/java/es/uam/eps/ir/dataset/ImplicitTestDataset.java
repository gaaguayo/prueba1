package es.uam.eps.ir.dataset;

import es.uam.eps.ir.cars.model.ContinuousTimeModelReader;
import es.uam.eps.ir.cars.model.GenericImplicitModel;
import es.uam.eps.ir.cars.model.GenericModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author pedro
 */
public class ImplicitTestDataset<U,I,C extends ContinuousTimeContextIF> extends AbstractDataset<U,I,C> implements DatasetIF<U,I,C>{

    public ImplicitTestDataset(String path, String file) {
        super(path, file);
    }
    
    public ImplicitTestDataset(String[] args) {
        super(args);
    }
    

    public ImplicitTestDataset() {
        super();
    }

    @Override
    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected final void setReader(){
        reader=new ContinuousTimeModelReader<U,I,C>();
        ContinuousTimeModelReader<U,I,C> theReader=(ContinuousTimeModelReader<U,I,C>)reader;
        theReader.setDelimiter(",");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setTimestampIndex(2);
        theReader.setTimestampFormat("yyyy-MM-dd HH:mm:ss");
        theReader.setContainsTitleLine(true);
        theReader.setImplicitData();
//        theReader.setIntegerKeys();
    }
    
    public final String name(){
        return this.getClass().getSimpleName();
    }    
}
