package models;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static models.Matchers.*;
import org.junit.Test;

public class CommentTest extends BasicModelTest {
	
    @Test
    public void newCommentTest() {
    	String commentText = "Some text to go into this comment";

    	Comment comment = new Comment(getDefaultStory(), commentText, getDefaultUser());
    	assertThat(comment, notNullValue());
     	comment.save();
    	
    	List<Comment> comments = Comment.findAll();
    	assertThat(comments.size(), is(1));
    	comment = comments.get(0);
    	assertThat(comment, notNullValue());
    	assertThat(comment.text, is(commentText));
    	assertThat(comment.createdOn, is(recentDate()));
    	assertThat(comment.createdUser, is(getDefaultUser()));
    	assertThat(comment.getStory(), is(getDefaultStory()));
    }

}
