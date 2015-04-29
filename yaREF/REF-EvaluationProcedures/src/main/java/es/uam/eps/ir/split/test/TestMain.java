package es.uam.eps.ir.split.test;

import es.uam.eps.ir.cars.model.ContinuousTimeModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 * 
 * @author pedro
 */
public class TestMain {

    public static void main(String args[]){
        ModelIF<Object, Object, ContinuousTimeContextIF> model = getMovieLens1mData();
        MovieLens100kSplitterTest.test(model);
    }

    /*
     * This method is supposed to load MovieLens 1 million dataset data into a ModelIF implementation
     * This method must be conveniently overwritten 
     */
    public static ModelIF<Object, Object, ContinuousTimeContextIF> getMovieLens1mData(){
        ModelIF<Object, Object, ContinuousTimeContextIF> model = null;
        
        // TODO
        ContinuousTimeModelReader<Object, Object, ContinuousTimeContextIF> theReader = new ContinuousTimeModelReader<Object, Object, ContinuousTimeContextIF>();
        theReader.setDelimiter("\t");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setRatingIndex(2);
        theReader.setTimestampIndex(3);
        theReader.setTimestampFormat("\"yyyy-MM-dd HH:mm:ss\"");
        theReader.setContainsTitleLine(false);
        theReader.setIntegerKeys();
        String pathToData = "/datos/datasets/MovieLens/100k/u.data";
        model = theReader.readModel(pathToData);
        return model;
    }
}
