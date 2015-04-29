package es.uam.eps.ir.dataset;

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
public class MovieLens100k_Attributes<U,I> extends AbstractDatasetAttributes<U,I>{

    public MovieLens100k_Attributes(DatasetIF dataset) {
        super(dataset);
        this.delimiter = "\\|";
    }
        
    @Override
    public void readAttributes() {
        Logger logger = Logger.getLogger("ExperimentLog");
        addItemAttributeDefinition("Genre", 0, Arrays.asList("Action","Adventure","Animation","Children's","Comedy","Crime","Documentary","Drama","Fantasy","Film-Noir","Horror","Musical","Mystery","Romance","Sci-Fi","Thriller","War","Western"));
        
        String path = dataset.getPath();
        String fileName = path + "u.item"; // item attributes file
        
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
                if (values[6].equalsIgnoreCase("1")){ itemGenreValues.add("Action"); }
                if (values[7].equalsIgnoreCase("1")){ itemGenreValues.add("Adventure"); }
                if (values[8].equalsIgnoreCase("1")){ itemGenreValues.add("Animation"); }
                if (values[9].equalsIgnoreCase("1")){ itemGenreValues.add("Children's"); }
                if (values[10].equalsIgnoreCase("1")){ itemGenreValues.add("Comedy"); }
                if (values[11].equalsIgnoreCase("1")){ itemGenreValues.add("Crime"); }
                if (values[12].equalsIgnoreCase("1")){ itemGenreValues.add("Documentary"); }
                if (values[13].equalsIgnoreCase("1")){ itemGenreValues.add("Drama"); }
                if (values[14].equalsIgnoreCase("1")){ itemGenreValues.add("Fantasy"); }
                if (values[15].equalsIgnoreCase("1")){ itemGenreValues.add("Film-Noir"); }
                if (values[16].equalsIgnoreCase("1")){ itemGenreValues.add("Horror"); }
                if (values[17].equalsIgnoreCase("1")){ itemGenreValues.add("Musical"); }
                if (values[18].equalsIgnoreCase("1")){ itemGenreValues.add("Mystery"); }
                if (values[19].equalsIgnoreCase("1")){ itemGenreValues.add("Romance"); }
                if (values[20].equalsIgnoreCase("1")){ itemGenreValues.add("Sci-Fi"); }
                if (values[21].equalsIgnoreCase("1")){ itemGenreValues.add("Thriller"); }
                if (values[22].equalsIgnoreCase("1")){ itemGenreValues.add("War"); }
                if (values[23].equalsIgnoreCase("1")){ itemGenreValues.add("Western"); }
                
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
