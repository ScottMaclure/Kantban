package services;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static matchers.Matchers.recentDate;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Test;

import play.test.UnitTest;

public class SystemTimeTest extends UnitTest {
	
	@After
	public void resetAll() {
		SystemTime.reset();
	}
	
    /**
     * Test that we can get current time
     */
    @Test
    public void currentTimeTest() {
    	Date now = SystemTime.asDate();
    	assertThat(now, recentDate());
    	assertThat(SystemTime.asMillis(), is(greaterThanOrEqualTo(now.getTime())));
    	assertThat(SystemTime.asMillis() - now.getTime(), is(lessThan(100L)));
    }
    
    /**
     * Test that setting the date to tomorrow works
     */
    @Test
    public void nextDayTest() {
    	
    	Date now = SystemTime.asDate();
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(now);
    	cal.add(Calendar.HOUR, 24);
    	Date nextDay = cal.getTime();
    	// ensure that we really have tomorrow's date from the calendar
    	assertTrue(nextDay.after(now));
    	assertThat(now.getTime() + 24 * 3600 * 1000, is(nextDay.getTime()));
    	
    	// now set the date
    	SystemTime.setDate(nextDay);
    	
    	// get back the date and millis, and check them
    	Date fakeNow = SystemTime.asDate();    	
    	Long fakeMillis = SystemTime.asMillis();
    	assertThat(fakeNow, not(recentDate()));
    	assertThat(fakeNow.getTime(), is(nextDay.getTime()));
    	assertThat(fakeMillis, is(nextDay.getTime()));
    	assertTrue(fakeNow.after(new Date()));

    	// add another day
    	cal.add(Calendar.HOUR, 24);
    	nextDay = cal.getTime();
    	assertThat(now.getTime() + 48 * 3600 * 1000, is(nextDay.getTime()));
    	
    	// set the millis
    	SystemTime.setMillis(nextDay.getTime());

    	// and check
    	fakeNow = SystemTime.asDate();    	
    	assertThat(fakeNow, not(recentDate()));
    	assertThat(fakeNow.getTime(), is(nextDay.getTime()));
    	assertTrue(fakeNow.after(new Date())); 	
    }
    
    /**
     * Test that reset() works
     */
    @Test
    public void testReset() {
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DAY_OF_YEAR, 1);
    	Date tomorrow = cal.getTime();
    	SystemTime.setDate(tomorrow);
    	
    	assertThat(SystemTime.asDate(), not(recentDate()));

    	SystemTime.reset();
    	
    	assertThat(SystemTime.asDate(), recentDate());
    }
}
