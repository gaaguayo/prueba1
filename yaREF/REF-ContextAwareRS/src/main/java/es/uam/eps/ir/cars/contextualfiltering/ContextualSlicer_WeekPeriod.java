package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This class creates Contextual segments (slices) from a {@link ModelIF}
 * based on the Weekday or Weekend time property.
 *
 * @author Pedro G. Campos <pcampossoto@gmail.com>
 * Creation date: 19-feb-2012
 */
public class ContextualSlicer_WeekPeriod<U,I,C extends ContextIF> extends AbstractContextualSlicer<U,I,C> implements ContextualSlicerIF<U,I,C>{

    public ContextualSlicer_WeekPeriod() {
        this.segmentNames=new String[]{"Weekday","Weekend"};
    }

    @Override
    public boolean isInSegment(PreferenceIF<U,I,C> pref, ContextualSegmentIF segment) {
        boolean inSegment=false;
        Long date= ((ContinuousTimeContextIF)pref.getContext()).getTimestamp();
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        switch ((Segment)segment){
            case WEEKDAY:
                if (dayOfWeek == Calendar.MONDAY ||
                    dayOfWeek == Calendar.TUESDAY ||
                    dayOfWeek == Calendar.WEDNESDAY ||
                    dayOfWeek == Calendar.THURSDAY ||
                    (dayOfWeek == Calendar.FRIDAY)){ // && hourOfDay < 13) ){
                    inSegment=true;
                }
                break;
            case WEEKEND:
                if (//dayOfWeek == Calendar.THURSDAY ||
                    //(dayOfWeek == Calendar.FRIDAY && hourOfDay >= 13) ||
                    dayOfWeek == Calendar.SATURDAY ||
                    dayOfWeek == Calendar.SUNDAY){
                    inSegment=true;
                }
                break;
        }
        
        return inSegment;
    }
    
    public enum Segment implements ContextualSegmentIF{
        WEEKDAY,
        WEEKEND;
    }
    
    public List<ContextualSegmentIF> getSegments(){
        List<ContextualSegmentIF> segments=new ArrayList(Arrays.asList(Segment.values()));
        return segments;
    }

    public ContextualSegmentIF getSegment(C context){
        ContextualSegmentIF segment=null;
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( ((ContinuousTimeContextIF)context).getTimestamp() );
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        
        switch (dayOfWeek){
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
            case Calendar.FRIDAY:
                segment = Segment.WEEKDAY;
                break;
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:
                segment = Segment.WEEKEND;                
                break;
        }
        return segment;
    }
    
//    public Date getDateInContext(ContextualSegmentIF segment){
//        final Calendar cal = Calendar.getInstance();
//        switch ((Segment)segment){
//            case WEEKDAY:
//                cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
//                break;
//            case WEEKEND:
//                cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
//                break;
//        }
//        return cal.getTime();
//    }
    
    @Override
    public String toString(){
        return "ContextualSlicer_WeekPeriod";
    }
}
