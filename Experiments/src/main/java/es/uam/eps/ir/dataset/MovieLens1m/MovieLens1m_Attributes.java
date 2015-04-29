package es.uam.eps.ir.dataset.MovieLens1m;

import es.uam.eps.ir.dataset.*;
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
public class MovieLens1m_Attributes<U,I> extends AbstractDatasetAttributes<U,I>{

    public MovieLens1m_Attributes(DatasetIF dataset) {
        super(dataset);
        this.delimiter = "::";
    }
        
    @Override
    public void readAttributes() {
        Logger logger = Logger.getLogger("ExperimentLog");
        addItemAttributeDefinition("Genre", 0, Arrays.asList("Action","Adventure","Animation","Children's","Comedy","Crime","Documentary","Drama","Fantasy","Film-Noir","Horror","Musical","Mystery","Romance","Sci-Fi","Thriller","War","Western"));
        
        String path = dataset.getPath();
        String fileName = path + "movies.dat"; // item attributes file
        
        String line;
        int count = 0;
        try{
            FileReader file=new FileReader(fileName);
            BufferedReader buffer=new BufferedReader(file);
            logger.log(Level.INFO, "Reading attribute data (from {0})", fileName);
            int itemIndex = 0;
            while ( (line=buffer.readLine())!=null){
                ++count;
                String[] values;
                values=line.split(this.delimiter);
                
                I itemKey;

                if (values[itemIndex].equalsIgnoreCase("")){
                    logger.log(Level.WARNING, "Ignoring line {0} (item:{1})", new Object[]{line, values[itemIndex]});
                    continue;
                }
                
                if (integerKeys){
                    itemKey=(I)(Integer)Integer.parseInt(values[itemIndex]);
                }
                else {
                    itemKey=(I)values[itemIndex];
                }
                
                // Genre                        
                List<String> itemGenreValues = new ArrayList<String>();
                itemGenreValues.addAll(Arrays.asList(values[2].split("\\|")));
                
                MultiValuedAttribute itemGenres = new MultiValuedAttribute(itemAttributeDefinitions.get(0), itemGenreValues);
                
                this.addItemAttribute(itemKey, itemGenres);
                
            }
            logger.log(Level.INFO, "Processed {0} lines", count);
            
        }catch (java.io.FileNotFoundException e){
            System.out.println("File "+fileName+ " not found! "+e);
            for (StackTraceElement ste:e.getStackTrace()){
                System.out.println(ste);
            }
            System.exit(1);
        }catch (java.io.IOException e){
            System.out.println("Problem with IO! "+e);
            for (StackTraceElement ste:e.getStackTrace()){
                System.out.println(ste);
            }
            System.exit(1);
        }
        
    }
    
}
