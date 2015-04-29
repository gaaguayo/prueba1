package es.uam.eps.ir.dataset.MovieLens1m;

import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.dataset.ItemDataIF;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pedro
 */
public class Movielens1mMovieData implements ItemDataIF {
    private DatasetIF dataset;
    private Map<Object,Integer> movieYearMap;

    public Movielens1mMovieData(DatasetIF dataset) {
        this.dataset = dataset;
        movieYearMap = new HashMap<Object,Integer>();
        readData();
    }
    
    private void readData(){
        String file = dataset.getPath() + "movies.dat";
        BufferedReader in;
        try{
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                String[] toks = line.split("::");
                String yearString = toks[1];
                yearString = yearString.substring(yearString.length() - 5, yearString.length() - 1);
                Integer movieID = Integer.parseInt(toks[0]);
                Integer movieYear = Integer.parseInt(yearString);
                movieYearMap.put(movieID, movieYear);
            }
            in.close();
        } catch (FileNotFoundException e){
            System.err.println(file + " not found!");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e){
            System.err.println("There were problems reading " + file + "!");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public int year(Object movieID){
        return movieYearMap.get(movieID);
    }
}
