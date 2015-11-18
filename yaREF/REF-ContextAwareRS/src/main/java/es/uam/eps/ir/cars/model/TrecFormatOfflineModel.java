package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.model.impl.GenericExplicitModel;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceAggregationFunction;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Pedro G. Campos
 */
public class TrecFormatOfflineModel<U,I,C extends ContextIF> implements ModelIF<U,I,C> {
    ModelIF<U,I,C> internalModel;
    String file;
    PrintStream ps;


    @SuppressWarnings("CallToThreadDumpStack")
    public TrecFormatOfflineModel(Class<? extends ModelIF<U,I,C>> modelClass) {
        try {
            internalModel = modelClass.newInstance();
        }
        catch (Exception e){
            System.err.println("There was a problem instatiating the model" + e);
            e.printStackTrace();
            internalModel = new GenericExplicitModel<U,I,C>();
        }
        
    }
    
    public void addPreference(U user, I item, Float pref, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<U> getUsers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<I> getItems() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Float getPreferenceValue(U user, I item, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<? extends PreferenceIF<U, I, C>> getPreferencesFromUser(U user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<? extends PreferenceIF<U, I, C>> getPreferencesFromItem(I item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromUser(U user, PreferenceAggregationFunction<U, I, C> f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromItem(I item, PreferenceAggregationFunction<U, I, C> f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<? extends PreferenceIF<U, I, C>> getPreferences(U user, I item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
