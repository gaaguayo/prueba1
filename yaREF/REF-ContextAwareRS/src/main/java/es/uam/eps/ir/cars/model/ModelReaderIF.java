package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author pedro
 */
public interface ModelReaderIF<U,I,C extends ContextIF> {
    public void setDelimiter(String delimiter);
    public String getDelimiter();
    public void setContainsTitleLine(boolean contains);
    public boolean getContainsTitleLine();
    public void setUserIndex(int index);
    public int getUserIndex();
    public void setItemIndex(int index);
    public int getItemIndex();
    //public void setTimestampIndex(int index);
    //public int getTimestampIndex();
    //public void setTimestampFormat(String format);
    //public String getTimestampFormat();
    public void setRatingIndex(int index);
    public int getRatingIndex();
    public void setTagsIndex(int index);
    public int getTagsIndex();
    public void setTagsDelimiter(String delimiter);
    public String getTagsDelimiter();
    public void setIntegerKeys();
    public boolean getIntegerKeys();
    public void setImplicitData();
    public boolean getImplicitData();
    public ModelIF<U, I, C> readModel(String file);    
}
