package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.model.DescriptionIF;
import es.uam.eps.ir.core.model.impl.ExplicitPreference;
import es.uam.eps.ir.core.context.ContextIF;

/**
 *
 * @author pedro
 */
public class WeightedExplicitPreference<U,I,C extends ContextIF> extends ExplicitPreference<U,I,C> implements WeightedPreferenceIF<U,I,C>, DescriptionIF {
    private double weight;

    public WeightedExplicitPreference(double weight, U user, I item, C context, Float pref) {
        super(user, item, context, pref);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString(){
        return super.toString() + "\t" + weight;
    }

    @Override
    public String description() {
        return super.description() + "\t" + "weight";
    }
    
}
