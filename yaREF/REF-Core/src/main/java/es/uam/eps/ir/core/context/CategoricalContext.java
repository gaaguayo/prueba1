package es.uam.eps.ir.core.context;

/**
 *
 * @author pedro
 */
public class CategoricalContext implements ContextIF{
    
    private ContextDefinition definition;
    private int value;

    public CategoricalContext(ContextDefinition definition, String nominalValue) {
        this.definition = definition;
        this.value = definition.getValue(nominalValue);
    }

    public String getName() {
        return definition.getName();
    }

    public int getValue() {
        return value;
    }

    public String getNominalValue() {
        return definition.getNominalValue(value);
    }
    
    public void setValue(String nominalValue){
        value = definition.getValue(nominalValue);
    }
    
    @Override
    public String toString(){
        return definition.getName() + ":" + definition.getNominalValue(value);
    }

    public int compareTo(ContextIF o) {
        if (o instanceof CategoricalContext){
            CategoricalContext cc = (CategoricalContext) o;
            int _value = definition.compareTo(cc.definition);
            if (_value == 0){
                _value = value - cc.value;
            }
            return _value;
        }
        else{
            return Integer.MAX_VALUE;
        }
    }

}
