package es.uam.eps.ir.metrics;

import es.uam.eps.ir.core.util.Average;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author pedro
 */
public class AveragedMetricResults<U> implements MetricResultsIF<U>{
    protected List<Integer> levels;
    protected Average averagedResult;
    protected Map<Integer,Average> averagedResultAtLevel;
    protected String metricName;

    public AveragedMetricResults(List<Integer> levels, double resultValue, Map<Integer, Double> resultValueAtLevel, String metricName) {
        this.levels = levels;
        this.metricName = metricName;
        averagedResult = new Average();
        averagedResult.add(resultValue);
        averagedResultAtLevel = new HashMap<Integer,Average>();
        if (levels != null){
            for (int level:levels){
                Average avg = new Average();
                avg.add(resultValueAtLevel.get(level));
                averagedResultAtLevel.put(level, avg);
            }
        }
    }

    public AveragedMetricResults(MetricResultsIF<U> metricResults) {
        this.levels = metricResults.getLevels();
        this.metricName = metricResults.shortName();
        averagedResult = new Average();
        averagedResult.add(metricResults.getResult());
        averagedResultAtLevel = new HashMap<Integer,Average>();
        if (levels != null){
            for (int level:levels){
                Average avg = new Average();
                avg.add(metricResults.getResult(level));
                averagedResultAtLevel.put(level, avg);
            }
        }
    }
    
    public void add(MetricResultsIF<U> metricResults){
        if (this.metricName.compareTo(metricResults.shortName()) != 0){
            throw new IllegalArgumentException(); 
        }
        if (levels != null && ! levels.equals(metricResults.getLevels())){            
            throw new IllegalArgumentException(); 
        }
        
        averagedResult.add(metricResults.getResult());
        
        if (levels != null){
            for (int level:levels){
                averagedResultAtLevel.get(level).add(metricResults.getResult(level));
            }
        }
    }
    
    public int N(){
        return averagedResult.N();        
    }
    
    @Override
    public double getResult(){
        return averagedResult.average();
    }
    
    @Override
    public double getResult(int level){
        return averagedResultAtLevel.get(level).average();
    }
    
    
    @Override
    public double getResult(U user){
        throw new UnsupportedOperationException("Not valid for averaged result");
    }
    
    @Override
    public double getResult(U user, int level){
        throw new UnsupportedOperationException("Not valid for averaged result");
    }

    public Set<U> getUsers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Integer> getLevels() {
        return this.levels;
    }

    public String formattedValue() {
        String resultString=String.format(Locale.US,"%.4f", averagedResult.average());
        String theString=resultString;
        if (levels != null){
            for (int level:levels){
                resultString=String.format(Locale.US,"%.4f", averagedResultAtLevel.get(level).average());
                theString=theString.concat("\t"+resultString);
            }
        }
        return theString;       
    }

    public String formattedName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String shortName() {
        return metricName;
    }

    public String columnFormat() {
        String resultString=String.format(Locale.US,"%.4f", averagedResult.average());
        String theString=metricName+"\t"+resultString+"\n";
        if (levels != null){
            for (int level:levels){
                resultString=String.format(Locale.US,"%.4f", averagedResultAtLevel.get(level).average());
                theString=theString.concat(metricName+"@"+level+"\t"+resultString+"\n");
            }
        }
        return theString;       
    }
    
    @Override
    public String toString(){
        String resultString=String.format(Locale.US,"%.4f", averagedResult.average());
        String theString=metricName+"="+ resultString;
        if (levels != null){
            for (int level:levels){
                resultString=String.format(Locale.US,"%.4f", averagedResultAtLevel.get(level).average());
                theString=theString.concat(", "+metricName+"@"+level+"="+resultString);
            }
        }
        return theString;        
    }
}
