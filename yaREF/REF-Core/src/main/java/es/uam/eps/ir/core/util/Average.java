package es.uam.eps.ir.core.util;

/**
 * Incremental computation of average and variance
 * @see <a href="http://math.stackexchange.com/questions/106700/incremental-averageing">math.stackexchange.com</a>
 * @see <a href="http://jvminside.blogspot.com.es/2010/01/incremental-average-calculation.html">jvminside.blogspot.com.es</a>
 * @see <a href="http://stackoverflow.com/questions/1058813/on-line-iterator-algorithms-for-estimating-statistical-median-mode-skewnes">stackoverflow.com</a>
 * 
 * @author pedro
 */
public class Average{
    int values = 0;
    double average = 0.0;
    double S = 0.0;
    public static final boolean processNaNs = false;
    
    public void add(double value){
        if (!processNaNs && Double.isNaN(value)) { return; } //NaNs are not processed
        values++;
        if (values == 1){
            average = value;
        }
        else{
//            average = ((average * (double)values) + value) / (double)(values + 1);
            S += (value - average)*(value - average);
            average += (value - average)/(double)values;
        }
    }
    
    public int N(){
        return values;
    }
    
    public double average(){
        return average;
    }
    
    public double variance(){
        return (values > 1 ? S / (double)(values -1): 0.0);
    }
    
    public double SD(){
        return Math.sqrt(variance());
    }
    
    @Override
    public String toString(){
        return "" + average + "\t" + SD();
    }
}
