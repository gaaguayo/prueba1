package es.uam.eps.ir.dataset.Context_IRG;

import es.uam.eps.ir.dataset.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class Context_Movies_IRG_Attributes<U,I> extends AbstractDatasetAttributes<U,I>{

    public static List<String> GENRES = Arrays.asList("action","adventure","sci-fi","fantasy","futuristic","historical","western","spaghetti western","crime","war","thriller","mystery","horror","drama","melodrama","comedy","parody","family","animation","anime","musical","romance","erotic","film noir","neo noir","documentary");
    public Context_Movies_IRG_Attributes(DatasetIF dataset) {
        super(dataset);
        this.delimiter = "\\t";
    }
        
    @Override
    public void readAttributes() {
        Logger logger = Logger.getLogger("ExperimentLog");
        addItemAttributeDefinition("Genre", 0, GENRES);
        
        String path = dataset.getPath();
        String fileName = path + "item_genres_movies.txt"; // item attributes file
        
        Map<I,List<String>> itemGenresMap = new HashMap<I,List<String>>();
        
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
                
                List<String> itemGenreValues = itemGenresMap.get(itemKey);
                if (itemGenreValues == null){
                    itemGenreValues = new ArrayList<String>();
                }
                // Genre
                Integer genreIndex = Integer.parseInt(values[1])-1;
                itemGenreValues.add(GENRES.get(genreIndex));
                itemGenresMap.put(itemKey, itemGenreValues);
            }
            
//            for (I itemKey : itemGenresMap.keySet()){
            for (I itemKey : (Collection<I>)dataset.getModel().getItems()){
                List<String> itemGenreValues = itemGenresMap.get(itemKey);
                if (itemGenreValues == null){
                    itemGenreValues = new ArrayList<String>();
                }
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
