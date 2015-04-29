package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.util.Average;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public class ProportionImpurity<U,I,C extends ContextIF> implements ImpurityComputerIF<U, I, C> {
    private final double impurityThreshold;
    private final float valueThreshold = 4;

    public ProportionImpurity() {
        impurityThreshold = 1.65;
    }
    
    public ProportionImpurity(double threshold) {
        impurityThreshold = threshold;
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
        
        double pA = highA / (double)preferencesA.size();
        double pB = highB / (double)preferencesB.size();
        double p = (pA * preferencesA.size() + pB * preferencesB.size()) / (double)(preferencesA.size() + preferencesB.size());

        double numerator = pA - pB;
        double denominator = Math.sqrt(p * (1 - p) * (1.0 / preferencesA.size() + 1.0 / preferencesB.size()));
        
        double t_prop = numerator / denominator;
        
        return t_prop;
    }

    public double impurityThreshold() {
        return impurityThreshold;
    }
    
}
