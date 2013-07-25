package uk.ac.cam.sup;

import static org.junit.Assert.fail;

import org.junit.Test;

import uk.ac.cam.sup.exceptions.ModelNotFoundException;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.UserQuery;

public class UserQueryTest {
	@Test
	public void getNonExistingUser(){
		@SuppressWarnings("unused")
		User u;
		
		try {
			u = UserQuery.get("xxx");
		} catch (ModelNotFoundException e) {
			return;
		}
		fail();
	}
}
