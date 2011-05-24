package models;
import java.util.List;

import org.junit.Test;

public class UserTest extends BasicModelTest {

    @Test
    public void newUserTest() {
    	
    	User user = new User("foo@bar.com", "Foo Bar");
    	assertNotNull(user);
    	user.save();

    	List<User> users = User.findAll();
    	assertEquals(1, users.size());
    	user = users.get(0);
    	assertNotNull(user);
    	assertEquals("foo@bar.com", user.emailAddress);
    	assertEquals("Foo Bar", user.name);
    	assertEquals(true, user.active);
    }

}
