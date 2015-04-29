package es.uam.eps.ir.cars.inferred;

/**
 *
 * @author pedro
 */
public class ContinuousTimeContextComputerBuilder {
    
    public enum TimeContext{
        PeriodOfDay,
        PeriodOfDay1,
        PeriodOfDay2,
        PeriodOfDay3,
        PeriodOfDay4,
        Meridian,
        DayOfWeek,
        PeriodOfWeek,
        PeriodOfYear,
        Christmas,
        Holiday
    }
    
    public static ContextualAttributeComputerIF getContextComputer(TimeContext timeContext) {
        switch(timeContext){
            case PeriodOfDay:
                return new PeriodOfDayAttributeComputer();
            case PeriodOfDay1:
                return new PeriodOfDayAttributeComputer1();
            case PeriodOfDay2:
                return new PeriodOfDayAttributeComputer2();
            case PeriodOfDay3:
                return new PeriodOfDayAttributeComputer3();
            case PeriodOfDay4:
                return new PeriodOfDayAttributeComputer4();
            case Meridian:
                return new MeridianAttributeComputer();
            case DayOfWeek:
                return new DayOfWeekAttributeComputer();
            case PeriodOfWeek:
                return new PeriodOfWeekAttributeComputer();
            case PeriodOfYear:
                return new PeriodOfYearAttributeComputer();
            case Christmas:
                return new ChristmasAttributeComputer();
            case Holiday:
                return new HolidayAttributeComputer();
            default:
                return null;
        }
    }    
}
