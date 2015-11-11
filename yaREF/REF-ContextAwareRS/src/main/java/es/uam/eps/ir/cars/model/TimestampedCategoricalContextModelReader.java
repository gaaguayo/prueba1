package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.context.CategoricalContext;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.context.TimestampedContextContainer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class TimestampedCategoricalContextModelReader<U,I,C extends ContextIF> extends GenericModelReader<U,I,C> implements ModelReaderIF<U,I,C>{ 
    protected Map<Integer, ContextDefinition> indexContextMap;

    protected int timestampIndex = -1;
    protected String timestampFormat;
    
    public void setTimestampIndex(int index) {this.timestampIndex = index;}
    public void setTimestampFormat(String format) {this.timestampFormat = format;}
    
    public String getTimestampFormat() {return timestampFormat;}
    public int getTimestampIndex() {return timestampIndex;}    
    
    public TimestampedCategoricalContextModelReader() {
        indexContextMap = new HashMap<Integer, ContextDefinition>();
    }    
    
    public void addContext(String contextName, int index, List<String> contextValues) {
        ContextDefinition ctxDef = new ContextDefinition(contextName);
        for (String contextValue : contextValues){
            ctxDef.addValue(contextValue);
        }
        this.indexContextMap.put(index, ctxDef);
    }
    
    public List<ContextDefinition> getContextDefinitions(){
        List<ContextDefinition> defs = new ArrayList<ContextDefinition>(indexContextMap.values());
        return defs;
    }
    
    @Override
    protected C processContextData(String[] values){
        Logger logger = Logger.getLogger("ExperimentLog");
        Date date=null;
        SimpleDateFormat df=new SimpleDateFormat();
        df.applyPattern(this.timestampFormat);
        
        try{
            if (values[timestampIndex].equalsIgnoreCase("")){ 
                logger.log(Level.WARNING, "Ignoring line {0} (user:{1}, item:{2},ts:{3})", new Object[]{values.toString(), values[userIndex], values[itemIndex], values[timestampIndex]});
            }
            date=df.parse(values[timestampIndex]);
        } catch (java.text.ParseException e){
            // assumes UNIX's epoch as timestamp
            date=new Date(new Long(values[timestampIndex])*(long)1000);
        }
        C context = (C) new TimestampedContextContainer(date.getTime());
        
        for (int ctxIndex : indexContextMap.keySet()){
            if (values[ctxIndex].equalsIgnoreCase("")){ 
                logger.log(Level.WARNING, "Ignoring line {0} (user:{1}, item:{2},ts:{3})", new Object[]{values.toString(), values[userIndex], values[itemIndex], values[ctxIndex]});
            }
            C contextValue = (C) new CategoricalContext(indexContextMap.get(ctxIndex), values[ctxIndex]);
            ((TimestampedContextContainer)context).add(contextValue);
        }
        
        return context;
    }
}
