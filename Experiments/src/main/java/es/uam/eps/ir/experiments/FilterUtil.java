package es.uam.eps.ir.experiments;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.filter.DatasetFilterIF;
import es.uam.eps.ir.filter.KnownDataFilter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pedro
 */
public class FilterUtil<U,I,C extends ContextIF> implements DatasetFilterIF<U, I, C> {
    public enum FILTER{
        KnownData,
        UserMinRating,
        None
    }
    
    private final List<FILTER> filters = new ArrayList<FILTER>();
    private ModelIF<U,I,C> knownData;

    public FilterUtil() {
    }
    
    public FilterUtil addFilter(FILTER filter){
        filters.add(filter);
        return this;
    }
    
    public FilterUtil knownData(ModelIF<U,I,C> knownData){
        this.knownData = knownData;
        return this;
    }

    public ModelIF<U, I, C> filter(ModelIF<U, I, C> model) {
        ModelIF<U,I,C> filteredModel = model;
        
        for (FILTER filter: filters){
            if (filter == FILTER.None){
                continue;
            }
            DatasetFilterIF<U,I,C> _filter = getDatasetFilter(filter);
            filteredModel = _filter.filter(filteredModel);
        }
        
        return filteredModel;
    }
    
    private DatasetFilterIF<U,I,C> getDatasetFilter(FILTER filter){
        DatasetFilterIF<U,I,C> _filter = null;
        switch (filter){
            case KnownData:
                if (knownData == null){
                    throw new IllegalArgumentException("Must define the known data!");
                }
                _filter = new KnownDataFilter<U,I,C>(knownData);
                break;
        }
        return _filter;
    }
    
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        for (FILTER filter: filters){
            s.append(filter).append(",");
        }
        return s.toString();
    }
}
