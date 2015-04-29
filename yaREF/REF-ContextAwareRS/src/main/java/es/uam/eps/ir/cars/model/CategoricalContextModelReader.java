package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.context.CategoricalContext;
import es.uam.eps.ir.core.context.ContextContainer;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class CategoricalContextModelReader<U,I,C extends ContextIF> extends GenericModelReader<U,I,C> implements ModelReaderIF<U,I,C>{
    
    protected Map<Integer, ContextDefinition> indexContextMap;

    public CategoricalContextModelReader() {
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
        C context = (C) new ContextContainer();

        
        for (int ctxIndex : indexContextMap.keySet()){
            if (values[ctxIndex].equalsIgnoreCase("")){ 
                logger.log(Level.WARNING, "Ignoring line {0} (user:{1}, item:{2},ts:{3})", new Object[]{values.toString(), values[userIndex], values[itemIndex], values[ctxIndex]});
            }
            C contextValue = (C) new CategoricalContext(indexContextMap.get(ctxIndex), values[ctxIndex]);
            ((ContextContainer)context).add(contextValue);
        }
        
        return context;
    }
}
