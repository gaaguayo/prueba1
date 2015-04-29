package es.uam.eps.ir.metrics;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author pedro
 */
public class BasicMetricResults<U> implements MetricResultsIF<U> {
    
    /*
     * Levels at which metric results are stores
     * Used with ranking metrics (e.g. Precision, Recall).
     */
    protected List<Integer> levels;
    
    /*
     * Stores metric result among all users, regardless level
     */
    protected double result;
    
    /*
     * Stores metric results among all users, at specified levels
     */
    protected Map<Integer,Double> resultAtLevel;
    

    /*
     * Stores metric results from each user, regardless level
     */
    protected Map<U,Double> userResultMap;
    
    /*
     * Stores metric results from each user, at defined levels.
     */
    protected Map<U,Double[]> userResultAtLevelMap;
    
    
    /*
     * Name of the metric
     */
    protected String metricName;

    public BasicMetricResults(List<Integer> levels, double resultValue, Map<Integer, Double> resultValueAtLevel, Map<U, Double> userResultMap, Map<U, Double[]> userResultAtLevelMap, String metricName) {
        this.levels = levels;
        this.result = resultValue;
        this.resultAtLevel = resultValueAtLevel;
        this.userResultMap = userResultMap;
        this.userResultAtLevelMap = userResultAtLevelMap;
        this.metricName = metricName;
    }
    
    @Override
    public double getResult(U user){
        if (userResultMap.containsKey(user)){
            return userResultMap.get(user);
        }
        else{
            return Double.NaN;
        }
    }
    
    @Override
    public double getResult(U user, int level){
        if (levels != null && userResultAtLevelMap.containsKey(user)){
            try {
                return userResultAtLevelMap.get(user)[levels.indexOf(level)];
            } catch (NullPointerException e){
                return Double.NaN;
            }
        }
        else{
            return Double.NaN;
        }
    }
    
    @Override
    public double getResult(){
        return result;
    }
    
    @Override
    public double getResult(int level){
        return resultAtLevel.get(level);
    }
    
    @Override
    public Set<U> getUsers(){
        return this.userResultMap.keySet();
    }

    @Override
    public String toString(){
        String resultString=String.format(Locale.US,"%.4f", result);
        String theString=metricName+"="+ resultString;
        if (levels != null){
            for (int level:levels){
                resultString=String.format(Locale.US,"%.4f", resultAtLevel.get(level));
                theString=theString.concat(", "+metricName+"@"+level+"="+resultString);
            }
        }
        return theString;       
    }
    
    public String formattedValue(){
        String resultString=String.format(Locale.US,"%.4f", result);
        String theString=resultString;
        if (levels != null){
            for (int level:levels){
                resultString=String.format(Locale.US,"%.4f", resultAtLevel.get(level));
                theString=theString.concat("\t"+resultString);
            }
        }
        return theString;       
    }
    
    public String formattedName(){
        String theString=metricName;
        if (levels != null){
            for (int level:levels){
                theString=theString.concat("\t"+metricName+"@"+level);
            }
        }
        return theString;       
    }
    
    public String columnFormat(){
        String resultString=String.format(Locale.US,"%.4f", result);
        String theString=metricName+"\t"+resultString+"\n";
        if (levels != null){
            for (int level:levels){
                resultString=String.format(Locale.US,"%.4f", resultAtLevel.get(level));
                theString=theString.concat(metricName+"@"+level+"\t"+resultString+"\n");
            }
        }
        return theString;       
    }

    public String shortName() {
        return metricName;
    }

    public List<Integer> getLevels() {
        return this.levels;
    }
    
    
}
