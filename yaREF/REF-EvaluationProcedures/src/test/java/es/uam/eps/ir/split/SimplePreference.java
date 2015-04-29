package es.uam.eps.ir.split;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;

/**
 *
 * @author Pedro G. Campos
 */
public class SimplePreference<U,I,C extends ContextIF> implements PreferenceIF<U,I,C>{

    private U user;
    private I item;
    private Float value;
    private C context;

    public SimplePreference(U user, I item, Float value, C context) {
        this.user = user;
        this.item = item;
        this.value = value;
        this.context = context;
    }

    public U getUser() {
        return user;
    }

    public I getItem() {
        return item;
    }

    public Float getValue() {
        return value;
    }

    public C getContext() {
        return context;
    }
    
}
