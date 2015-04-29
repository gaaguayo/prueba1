package es.uam.eps.ir.dataset;

/**
 *
 * @author pedro
 */
public class CategoricalAttribute {
    
    private AttributeDefinition definition;
    private int value;

    public CategoricalAttribute(AttributeDefinition definition, String nominalValue) {
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

    public int compareTo(CategoricalAttribute o) {
        if (o instanceof CategoricalAttribute){
            CategoricalAttribute ca = (CategoricalAttribute) o;
            int _value = definition.compareTo(ca.definition);
            if (_value == 0){
                _value = this.value - ca.value;
            }
            return _value;
        }
        else{
            return Integer.MAX_VALUE;
        }
    }

}
