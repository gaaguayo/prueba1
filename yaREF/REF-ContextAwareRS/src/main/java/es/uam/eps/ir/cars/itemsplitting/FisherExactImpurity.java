package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.util.Average;
import java.util.Collection;
import org.apache.commons.math3.util.ArithmeticUtils;

/**
 *
 * @author Pedro G. Campos
 */
public class FisherExactImpurity<U,I,C extends ContextIF> extends AbstractImpurityComputer<U,I,C>  implements ImpurityComputerIF<U, I, C> {

    public FisherExactImpurity(double impurityThreshold, float valueThreshold) {
        super(impurityThreshold, valueThreshold);
    }    
    public double getImpurity(Collection<PreferenceIF<U,I,C>> preferencesA, Collection<PreferenceIF<U,I,C>> preferencesB){

        int highA = 0; // number of high ratings (4,5) in preferencesA
        int highB = 0; // number of high ratings (4,5) in preferencesB
        int lowA = 0; // number of low ratings (1,2,3) in preferencesA
        int lowB = 0; // number of low ratings (1,2,3) in preferencesB
        
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
        
        lowA = preferencesA.size() - highA;
        lowB = preferencesB.size() - highB;
        
        double numerator = ArithmeticUtils.factorialDouble(highA + highB) * ArithmeticUtils.factorialDouble(lowA + lowB) * ArithmeticUtils.factorial(highA + lowA) * ArithmeticUtils.factorialDouble(highB + lowB);
        double denominator = ArithmeticUtils.factorialDouble(highA) * ArithmeticUtils.factorialDouble(highB) * ArithmeticUtils.factorialDouble(lowA) * ArithmeticUtils.factorialDouble(lowB) * ArithmeticUtils.factorialDouble(highA + lowA  + highB + lowB);
        double p = numerator / denominator;
        
        return p;
    }

    public double impurityThreshold() {
        return impurityThreshold;
    }
    
}
