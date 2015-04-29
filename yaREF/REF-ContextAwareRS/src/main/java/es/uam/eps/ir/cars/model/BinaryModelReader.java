package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author Pedro G. Campos
 */
public class BinaryModelReader<U,I,C extends ContextIF> extends GenericModelReader<U,I,C> implements ModelReaderIF<U,I,C> {

    @Override
    protected Float processRating(final ModelIF<U,I,C> model, final String[] values, final U userKey, final I itemKey, final C context){
        return new Float(1);
    }
    
}
