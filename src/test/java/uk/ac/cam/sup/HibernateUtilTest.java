package uk.ac.cam.sup;

import org.junit.Test;

import uk.ac.cam.sup.util.HibernateUtil;

public class HibernateUtilTest extends GenericTest {
	
	@Test
	public void initializationDoesNotThrowExceptions () {
		HibernateUtil.getSF();
	}
	
}
