package es.uam.eps.ir.dataset;

import java.util.List;

/**
 *
 * @author pedro
 */
public interface DatasetAttributesIF<U,I>{
    public List<AttributeDefinition> getUserAttributeDefinitions();
    public List<AttributeDefinition> getItemAttributeDefinitions();
    List<MultiValuedAttribute> getUserAttributes(U user);
    List<MultiValuedAttribute> getItemAttributes(I item);
    MultiValuedAttribute getUserAttribute(U user, String attributeName);
    MultiValuedAttribute getItemAttribute(I item, String attributeName);
    
    public void addUserAttributeDefinition(String name, int index, List<String> attributeValues);
    public void addItemAttributeDefinition(String name, int index, List<String> attributeValues);
    
    public void addUserAttribute(U user, MultiValuedAttribute attribute);
    public void addItemAttribute(I item, MultiValuedAttribute attribute);

    public void readAttributes();
}
