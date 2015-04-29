package es.uam.eps.ir.core.model;

import es.uam.eps.ir.core.context.ContextIF;

/**
 *
 * @author Alejandro Bellogin
 */
public interface PreferenceIF<U, I, C extends ContextIF>{

    public U getUser();

    public I getItem();

    public Float getValue();

    public C getContext();
}
