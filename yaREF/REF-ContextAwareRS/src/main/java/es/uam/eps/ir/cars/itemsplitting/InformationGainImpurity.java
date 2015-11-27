package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public class InformationGainImpurity<U,I,C extends ContextIF> extends AbstractImpurityComputer<U,I,C>  implements ImpurityComputerIF<U, I, C> {

    public InformationGainImpurity() {
        super(0.9);
    }

    
    public InformationGainImpurity(double impurityThreshold, float valueThreshold) {
        super(impurityThreshold, valueThreshold);
    }
    
    public double getImpurity(Collection<PreferenceIF<U,I,C>> preferencesA, Collection<PreferenceIF<U,I,C>> preferencesB){

        int high = 0;
        int highA = 0; // number of high ratings (4,5) in preferencesA
        int highB = 0; // number of high ratings (4,5) in preferencesB
        
        for (PreferenceIF<U,I,C> preferenceA : preferencesA){
            if (preferenceA.getValue() >= valueThreshold){
                highA++;
                high++;
            }
        }
        
        for (PreferenceIF<U,I,C> preferenceB : preferencesB){
            if (preferenceB.getValue() >= valueThreshold){
                highB++;
                high++;
            }
        }
        
        double pHigh = high / (double)(preferencesA.size() + preferencesB.size());
        double pA = highA / (double)preferencesA.size();
        double pB = highB / (double)preferencesB.size();
        
        double p_low = 1.0 - pHigh;
        double pA_low = 1.0-pA;
        double pB_low = 1.0-pB;
        
        double H_item = -(pHigh * Math.log(pHigh) + p_low * Math.log(p_low));
        double H_A = -(pA * Math.log(pA) + pA_low * Math.log(pA_low));
        double H_B = -(pB * Math.log(pB) + pB_low * Math.log(pB_low));
        
        double t_IG = H_item - H_A + H_B;
        
        return t_IG;
    }

    public double impurityThreshold() {
        return impurityThreshold;
    }
    
    
}
  