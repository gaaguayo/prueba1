package es.uam.eps.ir.dataset;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pedro
 */
public class MultiValuedAttribute {
    
    private AttributeDefinition definition;
    private List<Integer> values;

    public MultiValuedAttribute(AttributeDefinition definition, List<String> nominalValues) {
        this.definition = definition;
        this.values = new ArrayList<Integer>();
        for (String value : nominalValues){
            this.values.add(definition.getValue(value));
        }
    }

    public String getName() {
        return definition.getName();
    }

    public List<Integer> getValues() {
        return values;
    }
    
    public boolean containsValue(int value){
        return values.contains(value);
    }
    
    public boolean containsNominalValue(String nominalValue){
        for (int value : values){
            if (definition.getNominalValue(value).equalsIgnoreCase(nominalValue)){
                return true;
            }
        }
        return false;
    }

    public List<String> getNominalValues() {
        List<String> nominalValues = new ArrayList<String>();
        for (int value : values){
            nominalValues.add(definition.getNominalValue(value));
        }
        return nominalValues;
    }
    
    public void addValue(String nominalValue){
        values.add(definition.getValue(nominalValue));
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(definition.getName());
        for (int value : values){
            sb.append(",").append(definition.getNominalValue(value));
        }
        return sb.toString();
    }

}
