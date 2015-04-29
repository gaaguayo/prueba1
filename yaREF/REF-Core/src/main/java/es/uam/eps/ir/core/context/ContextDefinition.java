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
}
