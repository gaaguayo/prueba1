package es.uam.eps.ir.core.context;

import java.util.List;

/**
 *
 * @author pedro
 */
public interface CategoricalContextDefinitionIF {
    public String getName();
    public int getValue(String nominalValue);
    public List<String> getNominalValues();    
    public String getNominalValue(int value);
}
