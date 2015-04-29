package es.uam.eps.ir.dataset.Context_IRG;

import es.uam.eps.ir.cars.model.CategoricalContextModelReader;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.dataset.AbstractDataset;
import es.uam.eps.ir.dataset.ContextualDatasetIF;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pedro
 */
public class Context_Musicians_IRG_Dataset<U,I,C extends ContextIF> extends AbstractDataset<U,I,C> implements ContextualDatasetIF<U,I,C>{

    public Context_Musicians_IRG_Dataset(String path, String file) {
        super(path, file);
    }
    
    public Context_Musicians_IRG_Dataset(String[] args) {
        super(args);
    }
    

    public Context_Musicians_IRG_Dataset() {
        super();
    }
    

    @Override
    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected final void setReader(){
        reader = new CategoricalContextModelReader<U,I,C>();
        CategoricalContextModelReader<U,I,C> theReader=(CategoricalContextModelReader<U,I,C>)reader;
        theReader.setDelimiter(",");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setRatingIndex(81);
        theReader.addContext("Day", 78, Arrays.asList("morning","afternoon","night"));
        theReader.addContext("Week", 79, Arrays.asList("workday","weekend"));
        theReader.addContext("Place", 80, Arrays.asList("work","home","car","disco"));
        theReader.setContainsTitleLine(true);
//        theReader.setIntegerKeys();
    }
    
    public List<ContextDefinition> getContextDefinitions(){
        CategoricalContextModelReader<U,I,C> theReader=(CategoricalContextModelReader<U,I,C>)reader;
        return theReader.getContextDefinitions();
    }
    
    public final String name(){
        return "Context_Musicians_IRG";
    }    
}
