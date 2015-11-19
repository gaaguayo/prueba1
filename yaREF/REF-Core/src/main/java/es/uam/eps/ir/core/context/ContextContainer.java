package es.uam.eps.ir.core.context;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pedro
 */
public class ContextContainer implements ContextIF {
    List<ContextIF> contexts;

    public ContextContainer() {
        contexts = new ArrayList<ContextIF>();
    }
    
    public void add(ContextIF ctx){
        contexts.add(ctx);
    }
    
    public List<ContextIF> getContexts(){
        return contexts;
    }
    
    public CategoricalContext getCategoricalContext(ContextDefinition ctxDef){
        int index = 0;
        for (ContextIF context : contexts){
            if (context instanceof CategoricalContext){
                String ctxName = ((CategoricalContext)context).getName();
                if (ctxDef.getName().equalsIgnoreCase(ctxName)){
                    break;
                }
            }
            index++;
        }

        CategoricalContext ctx = (CategoricalContext)getContexts().get(index);
        return ctx;
    }

    public int compareTo(ContextIF o) {
        if (o instanceof ContextContainer){
            ContextContainer cc = (ContextContainer) o;
            if (cc.getContexts().size() != contexts.size()){
                return Integer.MAX_VALUE;
            }
            for (int i = 0; i < contexts.size(); i++){
                int value = contexts.get(i).compareTo(cc.getContexts().get(i));
                if (value != 0){
                    return value;
                }
            }
            return 0;
        }
        else{
            return Integer.MAX_VALUE;
        }
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (ContextIF ctx : contexts){
            sb.append(ctx.toString()).append(" ");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.contexts != null ? this.contexts.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContextContainer other = (ContextContainer) obj;
        if (this.contexts != other.contexts && (this.contexts == null || !this.contexts.equals(other.contexts))) {
            return false;
        }
        return true;
    }
    
    
}
