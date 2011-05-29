package models;
import java.util.List;

import org.junit.Test;

public class CommentTest extends BasicModelTest {
	
    @Test
    public void newCommentTest() {
    	String commentText = "Some text to go into this comment";

    	Comment comment = new Comment(commentText, getDefaultUser());
    	assertNotNull(comment);
     	comment.save();
    	
    	List<Comment> comments = Comment.findAll();
    	assertEquals(1, comments.size());
    	comment = comments.get(0);
    	assertNotNull(comment);
    	assertEquals(commentText, comment.comment);
    	assertDateFresh(comment.createdOn);
    	assertEquals(getDefaultUser(), comment.createdUser);
    }

}
