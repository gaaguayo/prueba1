package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.util.Average;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public class ChiSquaredImpurity<U,I,C extends ContextIF> implements ImpurityComputerIF<U, I, C> {
    private final double impurityThreshold;
    private final float valueThreshold = 4;

    public ChiSquaredImpurity() {
        impurityThreshold = 1.65;
    }
    
    public ChiSquaredImpurity(double threshold) {
        impurityThreshold = threshold;
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
        
        int total = preferencesA.size() + preferencesA.size();
        int expectedHighA = (highA + highB) * (highA + lowA) / total;
        int expectedHighB = (highA + highB) * (highB + lowB) / total;
        int expectedLowA = (lowA + lowB) * (highA + lowA) / total;
        int expectedLowB = (lowA + lowB) * (highB + lowB) / total;
        
        double chiSq = Math.pow(highA - expectedHighA, 2) / expectedHighA +
                       Math.pow(highB - expectedHighB, 2) / expectedHighB +
                       Math.pow(lowA - expectedLowA, 2) / expectedLowA +
                       Math.pow(lowB - expectedLowB, 2) / expectedLowB;
        
 
        return chiSq;
    }

    public double impurityThreshold() {
        return impurityThreshold;
    }
    
}
