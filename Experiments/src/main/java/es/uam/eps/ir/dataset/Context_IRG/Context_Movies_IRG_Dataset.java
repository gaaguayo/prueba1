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
public class Context_Movies_IRG_Dataset<U,I,C extends ContextIF> extends AbstractDataset<U,I,C> implements ContextualDatasetIF<U,I,C>{

    public Context_Movies_IRG_Dataset(String path, String file) {
        super(path, file);
    }
    
    public Context_Movies_IRG_Dataset(String[] args) {
        super(args);
    }
    

    public Context_Movies_IRG_Dataset() {
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
        theReader.setRatingIndex(85);
        theReader.addContext("Day", 82, Arrays.asList("morning","afternoon","night","indifferent"));
        theReader.addContext("Week", 83, Arrays.asList("workday","weekend","indifferent"));
        theReader.addContext("Company", 84, Arrays.asList("alone","couple","family","friends","indifferent"));
        theReader.setContainsTitleLine(true);
//        theReader.setIntegerKeys();
    }
    
    public List<ContextDefinition> getContextDefinitions(){
        CategoricalContextModelReader<U,I,C> theReader=(CategoricalContextModelReader<U,I,C>)reader;
        return theReader.getContextDefinitions();
    }
    
    public final String name(){
        return "Context_Movies_IRG";
    }    
}
