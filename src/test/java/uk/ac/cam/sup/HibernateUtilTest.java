package uk.ac.cam.sup;

import org.junit.Test;

import uk.ac.cam.cl.dtg.teaching.hibernate.HibernateUtil;

public class HibernateUtilTest extends GenericTest {
	
	@Test
	public void initializationDoesNotThrowExceptions () {
		HibernateUtil.getInstance().getSF();
	}
	
}
