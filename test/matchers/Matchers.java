package matchers;


import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

public class Matchers {
	
	/**
	 * Custom matcher to compare whether dates are close.
	 * <p>
	 * If no reference date is given, it compares to now 
	 * (i.e. whether the other date is recent, or slightly in the future.)
	 * <p>
	 * If no maximim difference value is given, 1000 ms is used.
	 * 
	 * @author mgjv
	 *
	 */
	private final static class IsCloseTo extends TypeSafeMatcher<Date> {
		
		Date reference;
		long maxDifference; // milliseconds
		final static private long DEFAULT_MAX_DIFFERENCE = 1000L;
		
		IsCloseTo(Date reference, long maxDifference) {
			this.reference = reference;
			this.maxDifference = maxDifference;
		}
		IsCloseTo(Date reference) {
			this(reference, DEFAULT_MAX_DIFFERENCE);
		}
		IsCloseTo(long maxDifference) {
			this(new Date(), maxDifference);
		}
		IsCloseTo() {
			this(new Date(), DEFAULT_MAX_DIFFERENCE);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("not within " + maxDifference + 
					" ms from the reference (" + reference + ")");
		}
	
		@Override
		public boolean matchesSafely(Date date) {
			return Math.abs(reference.getTime() - date.getTime()) < maxDifference;
		}
	}
	
	@Factory
	public static <T> Matcher<Date> closeTo(Date date, long maxDifference) {
		return new IsCloseTo(date, maxDifference);
	}
	@Factory
	public static <T> Matcher<Date> closeTo(Date date) {
		return new IsCloseTo(date);
	}
	@Factory
	public static <T> Matcher<Date> recentDate(long maxDifference) {
		return new IsCloseTo(maxDifference);
	}
	@Factory
	public static <T> Matcher<Date> recentDate() {
		return new IsCloseTo();
	}
}
