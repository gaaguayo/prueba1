package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.model.impl.GenericImplicitModel;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author Pedro G. Campos
 */
public class TrecFormatImplicitModelReader<U,I,C extends ContextIF> extends TrecFormatModelReader<U,I,C> {

    public TrecFormatImplicitModelReader(boolean COMPRESSED) {
        super(COMPRESSED);
    }
    
    @Override
    protected ModelIF<U,I,C> getModelInstance(){
        return new GenericImplicitModel<U,I,C>();
    }
}
