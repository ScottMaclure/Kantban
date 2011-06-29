package services;

import java.util.Date;


/**
 * Provide current time
 * <p>
 * This class is here so we can override date 
 * values for testing purposes.
 * <p>
 * To get the current date, call one of the as() methods. The number 
 * of milliseconds returned is the same as Date.getTime(), i.e. the number 
 * of milliseconds since 1 January 1970.  
 * <p>
 * To override what the as() methods return, call one of the set() methods 
 * first, and call reset() to return to normal behaviour. 
 * 
 * @author mgjv
 */
public class SystemTime {
	private static Long millis;
	
    public static long asMillis() {
    	return millis != null ? millis : System.currentTimeMillis();
    }
    public static int asSeconds() {
    	return (int) asMillis()/1000;
    }
    public static Date asDate() {
        return new Date(asMillis());
    }
    
    /**
     * Set the time to the given milliseconds
     * @param millis
     */
    public static void setMillis(Long millis) {
        SystemTime.millis = millis;
    }
    /**
     * Set the time to the given date
     * @param date
     */
    public static void setDate(Date date) {
        SystemTime.millis = date.getTime();
    }
    /**
     * Reset the class to 'normal' current time behaviour.
     */
    public static void reset() {
        setMillis(null);
    }
}
