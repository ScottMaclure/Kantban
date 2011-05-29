package models;
import java.util.Date;

import org.junit.Before;

import play.test.Fixtures;
import play.test.UnitTest;

public abstract class BasicModelTest extends UnitTest {
	
	@Before
	public void clean() {
		Fixtures.deleteDatabase();
	}
	
	protected User getDefaultUser() {
		User user = User.find("byName", "Foo Bar").first();
		if (user == null) {
			user = new User("foo@bar.com", "password", "Foo Bar");
	    	user.save();
		}
    	return user;
	}
	
	/**
	 * Assert that a date was created very recently (< 500 ms ago).
	 * @param date
	 */
	protected void assertDateFresh(Date date) {
		assertDateClose(date, new Date());
	}
	/**
	 * Assert that two dates are chronological, and less than 500 ms apart.
	 * @param date1
	 * @param date2
	 */
	protected void assertDateClose(Date date1, Date date2) {
		assertDateClose(date1, date2, 500);
	}
	/**
	 * Assert that two dates are chronological, and less than maxDifference ms apart.
	 * @param date1
	 * @param date2
	 * @param maxDifference
	 */
	protected void assertDateClose(Date date1, Date date2, long maxDifference) {
		assertTrue(date1.before(date2));
		assertTrue(date2.getTime() - date1.getTime() < maxDifference);
	}
}
