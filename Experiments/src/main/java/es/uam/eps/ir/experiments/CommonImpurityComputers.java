package es.uam.eps.ir.experiments;

import es.uam.eps.ir.cars.itemsplitting.ChiSquaredImpurity;
import es.uam.eps.ir.cars.itemsplitting.FisherExactImpurity;
import es.uam.eps.ir.cars.itemsplitting.ImpurityComputerIF;
import es.uam.eps.ir.cars.itemsplitting.InformationGainImpurity;
import es.uam.eps.ir.cars.itemsplitting.MeanImpurity;
import es.uam.eps.ir.cars.itemsplitting.ProportionImpurity;
import es.uam.eps.ir.core.context.ContextIF;

/**
 *
 * @author pedro
 */
public class CommonImpurityComputers<U,I,C extends ContextIF> {
    public enum IMPURITY{
        ChiSquared,
        FisherExact,
        InformationGain,
        Mean,
        Proportion,
        NoSplitting
    }
        
    // Threshold parameter
    private float valueThreshold = 4;
    private double impurityThreshold = 1.0;
        

    public CommonImpurityComputers() {
    }
    
    public CommonImpurityComputers<U,I,C> impurityThreshold(double threshold){
        this.impurityThreshold = threshold;
        return this;
    }
    
    public CommonImpurityComputers<U,I,C> valueThreshold(float threshold){
        this.valueThreshold = threshold;
        return this;
    }
        
    public ImpurityComputerIF<U,I,C> getComputer(IMPURITY impurity){
        ImpurityComputerIF<U,I,C> computer = null;
        switch (impurity){
            case ChiSquared:
                computer = new ChiSquaredImpurity(impurityThreshold, valueThreshold);
                break;
            case FisherExact:
                computer = new FisherExactImpurity(impurityThreshold, valueThreshold);
                break;
            case InformationGain:
                computer = new InformationGainImpurity(impurityThreshold, valueThreshold);
                break;
            case Mean:
                computer = new MeanImpurity(impurityThreshold, valueThreshold);
                break;
            case Proportion:
                computer = new ProportionImpurity(impurityThreshold, valueThreshold);
                break;
            case NoSplitting:
                break;
        }
        return computer;
    }
}
