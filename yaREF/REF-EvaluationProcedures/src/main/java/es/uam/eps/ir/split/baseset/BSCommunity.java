package es.uam.eps.ir.split.baseset;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public class BSCommunity<U,I,C  extends ContextIF> implements BaseSetGeneratorIF<U,I,C>{

    public Collection<ModelIF<U, I, C>> getBaseSets(ModelIF<U, I, C> model) {
        Collection<ModelIF<U, I, C>> _collection = new ArrayList<ModelIF<U, I, C>>();
        _collection.add(model);
        return _collection;
    }

    @Override
    public String toString(){
        return "communityCentric";
    }
}
