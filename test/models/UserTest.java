package models;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static models.Matchers.*;
import org.junit.Test;

public class UserTest extends BasicModelTest {

    @Test
    public void newUserTest() {
    	
    	User user = new User("foo@bar.com", "password", "Foo Bar");
    	assertThat(user, notNullValue());
    	user.save();

    	List<User> users = User.findAll();
    	assertThat(users.size(), is(1));
    	user = users.get(0);
    	assertThat(user, notNullValue());
    	assertThat(user.emailAddress, is("foo@bar.com"));
    	assertThat(user.name, is("Foo Bar"));
    	assertThat(user.active, is(true));
    	
    	user = User.connect("foo@bar.com", "password");
    	assertThat(user, notNullValue());
    	
    	assertThat(user.getGravatarId(), is("f3ada405ce890b6f8204094deb12d8a8"));
    	
    	// Note -- fix this when updating the salt
    	assertThat(user.password, is("86c7898ce1a826eb53076699877a6eda"));
    }


    @Test
    public void connectUserTest() {
    	
    	User user = new User("foo@bar.com", "password", "Foo Bar");
    	assertThat(user, notNullValue());
    	user.save();
    	user = User.connect("foo@bar.com", "password");
    	assertThat(user, notNullValue());
    	user = User.connect("foo@bar.com", "=password-");
    	assertThat(user, nullValue());
    }

}
