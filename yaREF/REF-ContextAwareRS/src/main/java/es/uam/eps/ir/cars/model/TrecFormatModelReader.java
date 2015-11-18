package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.model.impl.GenericExplicitModel;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.model.ModelIF;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author pedro
 */
public class TrecFormatModelReader<U,I,C extends ContextIF> implements ModelReaderIF<U,I,C>{
    private final boolean COMPRESSED;
    protected String timestampFormat;

    public TrecFormatModelReader(boolean COMPRESSED) {
        this.COMPRESSED = COMPRESSED;
    }
    public void setTimestampFormat(String format) {this.timestampFormat = format;}
    public String getTimestampFormat() {return timestampFormat;}

    protected ModelIF<U,I,C> getModelInstance(){
        return new GenericExplicitModel<U,I,C>();
    }
            
    public ModelIF<U, I, C> readModel(String file) {
        Logger logger = Logger.getLogger("ExperimentLog");
        
        ModelIF<U,I,C> model=getModelInstance();

        String fileName=file;
        if (COMPRESSED){
            fileName += ".gz";
        }
        BufferedReader reader;
        
        logger.log(Level.INFO, "Reading feedback matrix data (from {0})", fileName);            
        SimpleDateFormat df=new SimpleDateFormat();
        df.applyPattern(this.timestampFormat);
        
        try{
            InputStreamReader isr;
            if (COMPRESSED){
                isr = new InputStreamReader(
                    new GZIPInputStream(new FileInputStream(fileName)));
            }
            else{
                isr = new InputStreamReader(
                    new FileInputStream(fileName));
            }
            //Opening
            reader=new BufferedReader(isr);
            
            // reading
            String line;
            int count=0;
            while( (line=reader.readLine() ) != null){
                ++count;
                String[] values = line.split("\t");
                U userKey=(U)values[0];
                I itemKey=(I)values[1];
                float rating=Float.parseFloat(values[2]);
                Date date=null;
                try {
                    date = df.parse(values[3]); // Netflix dataset (yyyy-mm-dd)
                } catch (java.text.ParseException e){
                    date = new Date(Long.parseLong(values[3])); // Netflix Subsamples (long timestamp)
                }
                C context = (C)new ContinuousTimeContext(date.getTime());                
                
                model.addPreference(userKey, itemKey, rating, context);

            
                if (count%100000==0) { System.out.print("."); }
                if (count%1000000==0) {
                    System.out.println();
                    logger.log(Level.INFO, "Processed {0} lines", count);
                }
            }
            logger.log(Level.INFO, "Processed {0} lines", count);
            
            // Closing
            reader.close();
            
        }catch (java.io.FileNotFoundException e){
            System.err.println("File "+fileName+ " not found! "+e);
            for (StackTraceElement ste:e.getStackTrace()){
                System.err.println(ste);
            }
            System.exit(1);
        } catch (java.io.IOException e){
            System.err.println("Problem opening file "+fileName+": "+e);
            for (StackTraceElement ste:e.getStackTrace())
                System.err.println(ste);
        }
        
        return model;
    }
    
    public boolean getContainsTitleLine() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDelimiter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean getIntegerKeys() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getItemIndex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getRatingIndex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTagsDelimiter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getTagsIndex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getTimestampIndex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getUserIndex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setContainsTitleLine(boolean contains) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDelimiter(String delimiter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setIntegerKeys() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setItemIndex(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setRatingIndex(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTagsDelimiter(String delimiter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTagsIndex(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTimestampIndex(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setUserIndex(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setImplicitData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean getImplicitData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
