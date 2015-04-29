package es.uam.eps.ir.dataset;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.dataset.Context_IRG.Context_Movies_IRG_Dataset;
import es.uam.eps.ir.dataset.Context_IRG.Context_Musicians_IRG_Dataset;
import es.uam.eps.ir.dataset.MovieLens1m.Movielens1mDataset;
import es.uam.eps.ir.dataset.MovieLens1m.Movielens1m_60UsersDataset;

/**
 *
 * @author pedro
 */
public class CommonDatasets<U,I,C extends ContextIF> {
    public enum DATASET{
        
        // Timestamped
        CAMRa2010_FT,
        CAMRa2011t1,
        CAMRa2011t2,
        CAMRa2011t1hh,
        CAMRa2011t2hh,
        CAMRa2011Full,
        LastFM_Time,
        MovieLens100k,
        MovieLens1m,
        MovieLens10m,
        MovieLens1m_60users,
        MovieLens1m_TFM,
        MovieLens1m_TFMSplit5,
        MovieLens1m_TFMSplit5_validation,
        Netflix,
        NetflixSubsample1,
        NetflixSubsample2,
        NetflixSubsample3,
        NetflixSubsample4,
        NetflixSubsample5,
        TV1,
        TV2,
        ImplicitTestDataset,
        ExplicitTestDataset,
        Eurekakids,
        
        // Contextual
        Context_Movies_IRG,
        Context_Musicians_IRG,
        Context_LDOS_CoMoDa,
        Context_Adomavicius,
    }
    
    private String[] args;
    private int sample;

    public CommonDatasets(String[] args) {
        this.args = args;
    }
    
    public DatasetIF<U,I,C> getDataset(DATASET dataset_name){
        DatasetIF<U,I,C> dataset = null;
        switch (dataset_name){
            
            //Timestamped
            case CAMRa2010_FT:
                dataset = new CAMRa2010_FT_Dataset(args);
                break;
            case CAMRa2011t1:
                dataset = new CAMRa2011t1Dataset(args);
                break;
            case CAMRa2011t2:
                dataset = new CAMRa2011t2Dataset(args);
                break;
            case CAMRa2011t1hh:
                dataset = new CAMRa2011t1hhDataset(args);
                break;
            case CAMRa2011t2hh:
                dataset = new CAMRa2011t2hhDataset(args);
                break;
            case CAMRa2011Full:
                dataset = new CAMRa2011FullDataset(args);
                break;
            case LastFM_Time:
                dataset = new LastfmTimeDataset(args);
                break;
            case MovieLens100k:
                dataset = new Movielens100kDataset(args);
                break;
            case MovieLens1m:
                dataset = new Movielens1mDataset(args);
                break;
            case MovieLens10m:
                dataset = new Movielens10mDataset(args);
                break;
            case MovieLens1m_60users:
                dataset = new Movielens1m_60UsersDataset(args);
                break;
            case MovieLens1m_TFM:
                dataset = new Movielens1m_TFMDataset(args);
                break;
            case MovieLens1m_TFMSplit5:
                dataset = new Movielens1m_TFMSplit5Dataset(args);
                break;
            case MovieLens1m_TFMSplit5_validation:
                dataset = new Movielens1m_TFMSplit5_validationDataset(args);
                break;
            case Netflix:
                dataset = new NetflixDataset(args);
                break;
            case NetflixSubsample1:
                dataset = new NetflixSubsample1Dataset(args);
                break;
            case NetflixSubsample2:
                dataset = new NetflixSubsample2Dataset(args);
                break;
            case NetflixSubsample3:
                dataset = new NetflixSubsample3Dataset(args);
                break;
            case NetflixSubsample4:
                dataset = new NetflixSubsample4Dataset(args);
                break;
            case NetflixSubsample5:
                dataset = new NetflixSubsample5Dataset(args);
                break;
            case TV1:
                dataset = new TV1Dataset(args);
                break;
            case TV2:
                dataset = new TV2Dataset(args);
                break;
            case ImplicitTestDataset:
                dataset = new ImplicitTestDataset(args);
                break;
            case ExplicitTestDataset:
                dataset = new ExplicitTestDataset(args);
                break;
            case Eurekakids:
                dataset = new EurekakidsDataset(args);
                break;
                
            //Contextual
            case Context_Movies_IRG:
                dataset = new Context_Movies_IRG_Dataset(args);
                break;
            case Context_Musicians_IRG:
                dataset = new Context_Musicians_IRG_Dataset(args);
                break;
            case Context_LDOS_CoMoDa:
                dataset = new Context_LDOS_CoMoDa_Dataset(args);
                break;
            case Context_Adomavicius:
                dataset = new Context_Adomavicius_Dataset(args);
                break;
                
        }
        
        return dataset;
    }
    
    
}
