import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class ApplicationTest extends FunctionalTest {

    @Test
    public void testHomePage() {
        Response response = GET("/");
        assertStatus(200, response);
    }
    
    @Test
    public void testThatProjectPageRedirects() {
        Response response = GET("/project/1");
        assertStatus(302, response);
        
        // Why can't I simply have a method that translates a 'relative' URL into a real one
        // assertHeaderEquals("Location", "foobar", response);
        String location = response.headers.get("Location").value();
        assertNotNull(location);
        assertTrue(location.contains("login"));
    }
    
}