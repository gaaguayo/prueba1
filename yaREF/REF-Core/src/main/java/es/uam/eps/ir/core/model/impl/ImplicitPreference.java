package es.uam.eps.ir.core.model.impl;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.DescriptionIF;
import es.uam.eps.ir.core.model.PreferenceIF;

/**
 *
 * @author Pedro G. Campos
 */
public class ImplicitPreference<U, I, C extends ContextIF> implements PreferenceIF<U, I, C>, Comparable<PreferenceIF<U,I,C>>,DescriptionIF{
    private U user;
    private I item;
    private C context;
    private Float pref;

    public ImplicitPreference(U user, I item, C context, Float pref) {
        this.user = user;
        this.item = item;
        this.context = context;
        this.pref = pref;
    }

    public C getContext() {
        return context;
    }

    public I getItem() {
        return item;
    }

    public U getUser() {
        return user;
    }

    public Float getValue() {
        return pref;
    }

    public int compareTo(PreferenceIF<U, I, C> o) {
        int result;
        result = ((Comparable)this.getUser()).compareTo(o.getUser());
        
        if (result == 0){
            result = ((Comparable)this.getItem()).compareTo(o.getItem());
        }

        if (result == 0){
            result = ((Comparable)this.getContext()).compareTo(o.getContext());
        }
        
        if (result == 0){
            result = ((Comparable)this.getValue()).compareTo(o.getValue());
        }
       
        return result;
    }

    @Override
    public String toString(){
        return user + "\t" + item + "\t" + pref + "\t" + context;
    }
    
    public String description(){
        return "user\titem\tvalue\t"+context.getClass().getSimpleName();
    }
}
