package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.util.Average;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public class ProportionImpurity<U,I,C extends ContextIF> extends AbstractImpurityComputer<U,I,C>  implements ImpurityComputerIF<U, I, C> {

    public ProportionImpurity() {
        super(1.65);
    }

    
    public ProportionImpurity(double impurityThreshold, float valueThreshold) {
        super(impurityThreshold, valueThreshold);
    }

    public double getImpurity(Collection<PreferenceIF<U,I,C>> preferencesA, Collection<PreferenceIF<U,I,C>> preferencesB){

        int highA = 0; // number of high ratings (4,5) in preferencesA
        int highB = 0; // number of high ratings (4,5) in preferencesB
        
        for (PreferenceIF<U,I,C> preferenceA : preferencesA){
            if (preferenceA.getValue() >= valueThreshold){
                highA++;
            }
        }
        
        Average avgB = new Average();
        for (PreferenceIF<U,I,C> preferenceB : preferencesB){
            if (preferenceB.getValue() >= valueThreshold){
                highB++;
            }
        }
        
        double pA = (double)highA / (double)preferencesA.size();
        double pB = (double)highB / (double)preferencesB.size();
        double p = (double)(pA * preferencesA.size() + pB * preferencesB.size()) / (double)(preferencesA.size() + preferencesB.size());

        double numerator = pA - pB;
        double denominator = Math.sqrt(p * (1.0 - p) * ( (1.0 / (double)preferencesA.size()) + (1.0 / (double)preferencesB.size())));
        
        double t_prop = Math.abs(numerator / denominator);
        
        return t_prop;
    }

    public double impurityThreshold() {
        return impurityThreshold;
    }
    
}
