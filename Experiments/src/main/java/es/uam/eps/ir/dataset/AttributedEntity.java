package es.uam.eps.ir.dataset;

import java.util.List;

/**
 *
 * @author pedro
 */
public interface AttributedEntity<T> {
    List<CategoricalAttribute> getAttributes(T entity);
    CategoricalAttribute getAttribute(T entity, String attributeName);
}
