package es.uam.eps.ir.dataset;

import es.uam.eps.ir.cars.model.ContinuousTimeModelReader;
import es.uam.eps.ir.cars.model.GenericExplicitModel;
import es.uam.eps.ir.cars.model.GenericModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author pedro
 */
public class ExplicitTestDataset<U,I,C extends ContinuousTimeContextIF> extends AbstractDataset<U,I,C> implements DatasetIF<U,I,C>{

    public ExplicitTestDataset(String path, String file) {
        super(path, file);
    }
    
    public ExplicitTestDataset(String[] args) {
        super(args);
    }
    

    public ExplicitTestDataset() {
        super();
    }
    
//    @Override
//    public ModelIF<U, I, C> getModel() {
//        model = new GenericExplicitModel<U,I,C>();
//        ContinuousTimeContextIF context = new ContinuousTimeContext((long)0);
//        model.addPreference((U)(Integer)1, (I)(Integer)1, (float)3.0, (C)context);
//        model.addPreference((U)(Integer)1, (I)(Integer)2, (float)2.0, (C)context);
//        model.addPreference((U)(Integer)1, (I)(Integer)3, (float)4.0, (C)context);
//        model.addPreference((U)(Integer)2, (I)(Integer)4, (float)3.0, (C)context);
//        model.addPreference((U)(Integer)2, (I)(Integer)2, (float)2.0, (C)context);
//        model.addPreference((U)(Integer)2, (I)(Integer)5, (float)4.0, (C)context);
//        return model;
//    }
//    

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
        theReader.setRatingIndex(2);
        theReader.setTimestampIndex(3);
        theReader.setTimestampFormat("yyyy-MM-dd HH:mm:ss");
        theReader.setContainsTitleLine(true);
//        theReader.setIntegerKeys();
    }
    
    public final String name(){
        return this.getClass().getSimpleName();
    }    
}
