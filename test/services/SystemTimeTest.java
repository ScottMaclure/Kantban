package services;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static models.Matchers.recentDate;

import java.util.Calendar;
import java.util.Date;


import org.junit.Test;

import play.test.UnitTest;

public class SystemTimeTest extends UnitTest {
	
    @Test
    public void currentTimeTest() throws InterruptedException {
    	Date now = SystemTime.asDate();
    	assertThat(now, recentDate());
    	assertThat(SystemTime.asMillis(), is(greaterThanOrEqualTo(now.getTime())));
    	assertThat(SystemTime.asMillis() - now.getTime(), is(lessThan(100L)));
    	
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(now);
    	cal.add(Calendar.DAY_OF_YEAR, 1);
    	Date tomorrow = cal.getTime();
    	
    	assertTrue(tomorrow.after(now));
    	assertThat(now.getTime() + 24 * 3600 * 1000, is(tomorrow.getTime()));
    	
    	SystemTime.setDate(tomorrow);
    	
    	Date fakeNow = SystemTime.asDate();    	
    	Long fakeMillis = SystemTime.asMillis();
    	
    	assertThat(fakeNow, not(recentDate()));
    	assertThat(fakeNow.getTime(), is(tomorrow.getTime()));
    	assertThat(fakeMillis, is(tomorrow.getTime()));
    	assertTrue(fakeNow.after(new Date()));
    	
    	SystemTime.reset();
    	
    	now = SystemTime.asDate();
    	assertThat(now, recentDate());
    	assertThat(SystemTime.asMillis(), is(greaterThanOrEqualTo(now.getTime())));
    	assertThat(SystemTime.asMillis() - now.getTime(), is(lessThan(100L)));
    }
    
}
