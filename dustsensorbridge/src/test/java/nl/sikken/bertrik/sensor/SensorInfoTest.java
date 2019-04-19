package nl.sikken.bertrik.sensor;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for SensorInfo.
 */
public class SensorInfoTest {

	/**
	 * Verifies toString method()
	 */
	@Test
	public void testToString() {
		SensorInfo info = new SensorInfo(52.0, 4.7);
		String s = info.toString();
		Assert.assertNotNull(s);
	}
	
}
