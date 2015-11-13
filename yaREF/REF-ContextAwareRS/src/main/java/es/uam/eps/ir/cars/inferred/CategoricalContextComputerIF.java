package es.uam.eps.ir.cars.inferred;

import weka.core.Attribute;

/**
 *
 * @author pedro
 */
public interface CategoricalContextComputerIF<C> {
    String getAttributeName();
    String getAttributeType();
    double getAttributeValue(C context);
    String getAttributeNominalValue(C context);
    Attribute getAttribute();
}
