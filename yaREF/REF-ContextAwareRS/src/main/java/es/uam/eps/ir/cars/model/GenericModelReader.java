package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.model.impl.GenericImplicitModel;
import es.uam.eps.ir.core.model.impl.GenericExplicitModel;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.context.EmptyContext;
import es.uam.eps.ir.core.model.ModelIF;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class GenericModelReader<U,I,C extends ContextIF> implements ModelReaderIF<U,I,C>{
    protected String delimiter;
    protected int userIndex;
    protected int itemIndex;
    protected int ratingIndex = -1;
    protected int tagsIndex = -1;
    protected String tagsDelimiter;
    protected boolean implicitData=false;
    protected boolean skipTitleLine=false;
    protected boolean integerKeys=false;

    protected boolean titleLineFlag;
    
    public String getDelimiter() {return delimiter;}
    public void setDelimiter(String delimiter) {this.delimiter = delimiter;}
    public void setContainsTitleLine(boolean contains) {this.skipTitleLine = contains;}
    public void setUserIndex(int index) {this.userIndex = index;}
    public void setItemIndex(int index) {this.itemIndex = index;}
    public void setRatingIndex(int index) {this.ratingIndex = index;}
    public void setTagsIndex(int index) {this.tagsIndex = index;}
    public void setTagsDelimiter(String delimiter) {this.tagsDelimiter = delimiter;}
    public void setIntegerKeys(){this.integerKeys = true;}
    public void setImplicitData(){this.implicitData = true;}

    public int getItemIndex() {return itemIndex;}
    public int getRatingIndex() {return ratingIndex;}
    public boolean getContainsTitleLine() {return skipTitleLine;}
    public String getTagsDelimiter() {return tagsDelimiter;}
    public int getTagsIndex() {return tagsIndex;}
    public boolean isTitleLineFlag() {return titleLineFlag;}
    public int getUserIndex() {return userIndex;}
    public boolean getIntegerKeys() {return integerKeys;}
    public boolean getImplicitData() {return implicitData;}
    
    protected C processContextData(String[] values){
        return (C) EmptyContext.getEmptyContext();
    }
    
    protected ModelIF<U,I,C> initModel(){
        if (implicitData){
            return new GenericImplicitModel<U,I,C>();
        }
        else{
            return new GenericExplicitModel<U,I,C>();            
        }
    }
    
    protected Float processRating(final ModelIF<U,I,C> model, final String[] values, final U userKey, final I itemKey, final C context){
        Float rating;
        if (ratingIndex > -1){
            if (values[ratingIndex].equalsIgnoreCase("")) { return null; }
            rating=new Float(values[ratingIndex]);
        }
        else{
            rating = new Float(1);
        }
        return rating;
    }

    public ModelIF<U,I,C> readModel(String fileName) {
        Logger logger = Logger.getLogger("ExperimentLog");
        
        ModelIF<U,I,C> model = initModel();
        String line;
        
        int count=0;

        titleLineFlag=this.skipTitleLine;
        try{
            FileReader file=new FileReader(fileName);
            BufferedReader buffer=new BufferedReader(file);
            logger.log(Level.INFO, "Reading feedback matrix data (from {0})", fileName);            
            while ( (line=buffer.readLine())!=null){
                ++count;
                if (titleLineFlag) {titleLineFlag=false; continue;}
                String[] values;
                values=line.split(this.delimiter);
                U userKey;
                I itemKey;
                Float rating;
                List<String> tags;


                if (values[userIndex].equalsIgnoreCase("") || values[itemIndex].equalsIgnoreCase("")){
                    logger.log(Level.WARNING, "Ignoring line {0} (user:{1}, item:{2})", new Object[]{line, values[userIndex], values[itemIndex]});
                    continue;
                }
                
                if (integerKeys){
                    userKey=(U)(Integer)Integer.parseInt(values[userIndex]);
                    itemKey=(I)(Integer)Integer.parseInt(values[itemIndex]);
                }
                else {
                    userKey=(U)values[userIndex];
                    itemKey=(I)values[itemIndex];
                }

                if (tagsIndex > -1){
                    if (values[tagsIndex].equalsIgnoreCase("")) { continue; }
                    
                    tags=new ArrayList();
                    tags.addAll(Arrays.asList(values[tagsIndex].split(tagsDelimiter)));
                }
                
                C context = processContextData(values);
                
                rating = processRating(model, values, userKey, itemKey, context);
                
                model.addPreference(userKey, itemKey, rating, context);

                if (count%100000==0) { System.out.print("."); }
                if (count%1000000==0) {
                    System.out.println();
                    logger.log(Level.INFO, "Processed {0} lines", count);
                }
//                if (count>1000000) break;
            }
            logger.log(Level.INFO, "Processed {0} lines", count);
            
        }catch (java.io.FileNotFoundException e){
            System.err.println("File "+fileName+ " not found! "+e);
            for (StackTraceElement ste:e.getStackTrace()){
                System.err.println(ste);
            }
            System.exit(1);
        }catch (java.io.IOException e){
            System.err.println("Problem with IO! "+e);
            for (StackTraceElement ste:e.getStackTrace()){
                System.err.println(ste);
            }
            System.exit(1);
        }
        return model;
    }    
}
