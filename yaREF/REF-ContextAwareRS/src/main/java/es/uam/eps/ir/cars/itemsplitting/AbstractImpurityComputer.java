package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.core.context.ContextIF;

/**
 *
 * @author pedro
 */
public abstract class AbstractImpurityComputer<U,I,C extends ContextIF> implements ImpurityComputerIF<U, I, C> {
    protected final double impurityThreshold;
    protected final float valueThreshold;

    public AbstractImpurityComputer(double impurityThreshold) {
        this.impurityThreshold = impurityThreshold;
        this.valueThreshold = 4;
    }
    
    public AbstractImpurityComputer(double impurityThreshold, float valueThreshold) {
        this.impurityThreshold = impurityThreshold;
        this.valueThreshold = valueThreshold;
    }    
}
