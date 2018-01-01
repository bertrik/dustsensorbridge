package nl.sikken.bertrik.sensor;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for SensorMessage.
 */
public final class SensorMessageTest {

	/**
	 * Verifies the toString() method.
	 */
	@Test
	public void testToString() {
		SensorMessage message = new SensorMessage(new SensorPmTriplet(0, 1, 2), new SensorPmTriplet(3, 4, 5));
		String s = message.toString();
		Assert.assertNotNull(s);
	}
	
}
