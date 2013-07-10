package uk.ac.cam.sup;

import org.junit.Test;

public class HibernateUtilTest extends GenericTest {
	
	@Test
	public void initializationDoesNotThrowExceptions () {
		HibernateUtil.getSF();
	}
	
}
