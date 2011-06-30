import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import models.State;
import models.Story;
import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
 
@OnApplicationStart
public class Bootstrap extends Job {
 
    public void doJob() throws Exception {
        // Check if the database is empty
        if(User.count() == 0) {
        	Fixtures.deleteDatabase();
            Fixtures.loadModels("initial-data.yml");
            
            // Fix up all the statistics
        	// updateStatistics is not public, for good reasons.
        	Method updateStats = null;
			updateStats = State.class.getDeclaredMethod("updateStatistics", (Class<State>[]) null);
        	updateStats.setAccessible(true);
            for (State state: State.<State>findAll()) {
            	updateStats.invoke(state);
            }
        }
    }
 
}
