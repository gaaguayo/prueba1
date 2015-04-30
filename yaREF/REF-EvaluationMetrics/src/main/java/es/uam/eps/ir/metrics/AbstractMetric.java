package es.uam.eps.ir.metrics;

import java.util.Map;

/**
 * 
 * @author pedro
 */
public abstract class AbstractMetric<U,I,C> implements MetricIF<U,I,C>{
    protected double metric;
    protected Map<U,Double> userMetric;
    protected boolean computationFinished;

    
    protected AbstractMetric(){
        this.metric=0.0;
        this.computationFinished=false;
    }
    
    @Override
    public String toString(){
        return shortName();       
    }
    
    public abstract String shortName();
    
    
    protected synchronized void init(){
        this.computationFinished=false;
    }
    
    protected void finishComputation(){
        this.computationFinished=true;        
    }
}
