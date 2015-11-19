package es.uam.eps.ir.core.model.impl;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.DescriptionIF;
import es.uam.eps.ir.core.model.PreferenceIF;

/**
 *
 * @author pedro
 */
public class ExplicitPreference<U, I, C extends ContextIF> implements PreferenceIF<U, I, C>, Comparable<PreferenceIF<U,I,C>>,DescriptionIF{
    private U user;
    private I item;
    private C context;
    private Float pref;

    public ExplicitPreference(U user, I item, C context, Float pref) {
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 67 * hash + (this.item != null ? this.item.hashCode() : 0);
        hash = 67 * hash + (this.context != null ? this.context.hashCode() : 0);
        hash = 67 * hash + (this.pref != null ? this.pref.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExplicitPreference<?, ?, ?> other = (ExplicitPreference<?, ?, ?>) obj;
        if (this.user != other.user && (this.user == null || !this.user.equals(other.user))) {
            return false;
        }
        if (this.item != other.item && (this.item == null || !this.item.equals(other.item))) {
            return false;
        }
        if (this.context != other.context && (this.context == null || !this.context.equals(other.context))) {
            return false;
        }
        if (this.pref != other.pref && (this.pref == null || !this.pref.equals(other.pref))) {
            return false;
        }
        return true;
    }
    
    
}
