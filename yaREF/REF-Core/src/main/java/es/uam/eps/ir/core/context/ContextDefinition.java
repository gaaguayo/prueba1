package es.uam.eps.ir.core.context;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pedro
 */
public class ContextDefinition implements CategoricalContextDefinitionIF,Comparable<ContextDefinition>{
    private String name;
    private final List<String> nominalValues;
    private static final String undefined = "undefined";

    public ContextDefinition() {
        this.nominalValues = new ArrayList();
    }

    public ContextDefinition(String name) {
        this();
        this.name = name;
    }

    public String getName(){
        return name;
    }
    
    public int getValue(String nominalValue){
        return nominalValues.indexOf(nominalValue);
    }
    
    public List<String> getNominalValues(){
        return nominalValues;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void addValue(String value){
        nominalValues.add(value);
    }

    public String getNominalValue(int value){
        if (value == -1){
            return undefined;
        }
        return nominalValues.get(value);
    }

    public int compareTo(ContextDefinition o) {
        return name.compareTo(o.getName());
    }
    
    @Override
    public String toString(){
        return this.name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + (this.nominalValues != null ? this.nominalValues.hashCode() : 0);
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
        final ContextDefinition other = (ContextDefinition) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.nominalValues != other.nominalValues && (this.nominalValues == null || !this.nominalValues.equals(other.nominalValues))) {
            return false;
        }
        return true;
    }
    
    
}
