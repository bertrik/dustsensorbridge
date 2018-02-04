package nl.sikken.bertrik.sensor;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test for SensorMessage.
 */
public final class SensorMessageTest {

	/**
	 * Verifies the toString() method.
	 */
	@Test
	public void testToString() {
		SensorMessage message = new SensorMessage(new SensorPmTriplet(0, 1, 2), new SensorBmeMessage(25.0, 50, 1018));
		String s = message.toString();
		Assert.assertNotNull(s);
	}
	
	/**
	 * Verifies that a JSON sensor message can be parsed.
	 * 
	 * @throws IOException in case of a parsing error
	 */
	@Test
	public void testParse() throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream("/sensor_message.json");
		ObjectMapper mapper = new ObjectMapper();
		SensorMessage sensorMessage = mapper.readValue(is, SensorMessage.class);
		Assert.assertNotNull(sensorMessage);
	}
	
}
