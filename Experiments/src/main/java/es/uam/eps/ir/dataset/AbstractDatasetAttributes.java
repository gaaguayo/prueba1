package es.uam.eps.ir.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pedro
 */
public abstract class AbstractDatasetAttributes<U, I> implements DatasetAttributesIF<U,I>{
    protected Map<Integer,AttributeDefinition> userAttributeDefinitions;
    protected Map<Integer,AttributeDefinition> itemAttributeDefinitions;
    protected Map<U, List<MultiValuedAttribute>> userAttributesMap;
    protected Map<I, List<MultiValuedAttribute>> itemAttributesMap;
    protected DatasetIF dataset;
    protected String delimiter;
    protected boolean integerKeys=false;

    public AbstractDatasetAttributes(DatasetIF dataset) {
        this();
        this.dataset = dataset;
    }

    public AbstractDatasetAttributes() {
        userAttributeDefinitions = new HashMap<Integer,AttributeDefinition>();
        itemAttributeDefinitions = new HashMap<Integer,AttributeDefinition>();
        userAttributesMap = new HashMap<U, List<MultiValuedAttribute>>();
        itemAttributesMap = new HashMap<I, List<MultiValuedAttribute>>();
    }

    public void addUserAttributeDefinition(String name, int index, List<String> attributeValues) {
        AttributeDefinition definition = new AttributeDefinition(name);
        for (String value : attributeValues){
            definition.addValue(value);
        }
        userAttributeDefinitions.put(index, definition);
    }

    public void addItemAttributeDefinition(String name, int index, List<String> attributeValues) {
        AttributeDefinition definition = new AttributeDefinition(name);
        for (String value : attributeValues){
            definition.addValue(value);
        }
        itemAttributeDefinitions.put(index, definition);
    }

    public void addUserAttribute(U user, MultiValuedAttribute attribute) {
        List<MultiValuedAttribute> userAttributes = userAttributesMap.get(user);
        if (userAttributes == null){
            userAttributes = new ArrayList<MultiValuedAttribute>();
        }
        userAttributes.add(attribute);
        userAttributesMap.put(user, userAttributes);
    }

    public void addItemAttribute(I item, MultiValuedAttribute attribute) {
        List<MultiValuedAttribute> itemAttributes = itemAttributesMap.get(item);
        if (itemAttributes == null){
            itemAttributes = new ArrayList<MultiValuedAttribute>();
        }
        itemAttributes.add(attribute);
        itemAttributesMap.put(item, itemAttributes);
    }

    
    public List<AttributeDefinition> getUserAttributeDefinitions() {
        List<AttributeDefinition> defs = new ArrayList<AttributeDefinition>(userAttributeDefinitions.values());
        return defs;
        
    }

    public List<AttributeDefinition> getItemAttributeDefinitions() {
        List<AttributeDefinition> defs = new ArrayList<AttributeDefinition>(itemAttributeDefinitions.values());
        return defs;
    }

    public List<MultiValuedAttribute> getUserAttributes(U user) {
        return this.userAttributesMap.get(user);
    }

    public List<MultiValuedAttribute> getItemAttributes(I item) {
        return this.itemAttributesMap.get(item);
    }

    public MultiValuedAttribute getUserAttribute(U user, String attributeName) {
        List<MultiValuedAttribute> userAttributes = this.userAttributesMap.get(user);
        for (MultiValuedAttribute att : userAttributes){
            if (att.getName().equalsIgnoreCase(attributeName)){
                return att;
            }
        }
        return null;
    }

    public MultiValuedAttribute getItemAttribute(I item, String attributeName) {
        List<MultiValuedAttribute> itemAttributes = this.itemAttributesMap.get(item);
        for (MultiValuedAttribute att : itemAttributes){
            if (att.getName().equalsIgnoreCase(attributeName)){
                return att;
            }
        }
        return null;
    }
    
    public void setIntegerKeys(){this.integerKeys = true;}
    public boolean getIntegerKeys() {return integerKeys;}
    
    
    public abstract void readAttributes();
    
}
