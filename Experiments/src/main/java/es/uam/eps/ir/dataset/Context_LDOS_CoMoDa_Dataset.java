package es.uam.eps.ir.dataset;

import es.uam.eps.ir.dataset.Context_IRG.*;
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
public class Context_LDOS_CoMoDa_Dataset<U,I,C extends ContextIF> extends AbstractDataset<U,I,C> implements ContextualDatasetIF<U,I,C>{

    public Context_LDOS_CoMoDa_Dataset(String path, String file) {
        super(path, file);
    }
    
    public Context_LDOS_CoMoDa_Dataset(String[] args) {
        super(args);
    }
    

    public Context_LDOS_CoMoDa_Dataset() {
        super();
    }
    

    @Override
    public ModelIF<U, I, C> getPredefinedTestModel(String testName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected final void setReader(){
        reader = new CategoricalContextModelReader<U,I,C>();
        CategoricalContextModelReader<U,I,C> theReader=(CategoricalContextModelReader<U,I,C>)reader;
        theReader.setDelimiter("\t");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setRatingIndex(2);
        theReader.addContext("time", 8, Arrays.asList("morning","afternoon","evening","night","undefined"));
        theReader.addContext("daytype", 9, Arrays.asList("workday","weekend","undefined"));
        theReader.addContext("season", 10, Arrays.asList("spring","summer","autumn","winter","undefined"));
        theReader.addContext("location", 11, Arrays.asList("home","public_place","friend_house","winter","undefined"));
        theReader.addContext("weather", 12, Arrays.asList("sunny","rainy","stormy","snowy","cloudy","undefined"));
        theReader.addContext("social", 13, Arrays.asList("alone","partner","friends","colleagues","parents","public","family","undefined"));
        theReader.addContext("endEmo", 14, Arrays.asList("sad","happy","scared","suprised","angry","disgusted","neutral","undefined"));
        theReader.addContext("dominantEmo", 15, Arrays.asList("sad","happy","scared","suprised","angry","disgusted","neutral","undefined"));
        theReader.addContext("mood", 16, Arrays.asList("positive","neutral","negative","undefined"));
        theReader.addContext("physical", 17, Arrays.asList("healthy","ill","undefined"));
        theReader.addContext("decision", 18, Arrays.asList("user_decision","given_movie","undefined"));
        theReader.addContext("interaction", 19, Arrays.asList("first","n_th","undefined"));
        theReader.setContainsTitleLine(true);
//        theReader.setIntegerKeys();
    }
    
    public List<ContextDefinition> getContextDefinitions(){
        CategoricalContextModelReader<U,I,C> theReader=(CategoricalContextModelReader<U,I,C>)reader;
        return theReader.getContextDefinitions();
    }
    
    public final String name(){
        return "Context_LDOS_CoMoDa";
    }    
}
