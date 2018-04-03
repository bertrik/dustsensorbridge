package nl.sikken.bertrik.sensor;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
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
		SensorMessage message = new SensorMessage(new SensorPmTriplet(0.0, 1.0, 2.0),
				new SensorBmeMessage(25.0, 50, 1018));
		String s = message.toString();
		Assert.assertNotNull(s);
	}
	
	/**
	 * Verifies that a JSON sensor message can be parsed.
	 * 
	 * @throws IOException in case of a parsing error
	 */
	@Test
//	@Ignore("not sure why test fails...")
	public void testParse() throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream("sensor_message.json");
		ObjectMapper mapper = new ObjectMapper();
		SensorMessage sensorMessage = mapper.readValue(is, SensorMessage.class);
		Assert.assertNotNull(sensorMessage);
	}
	
	/**
	 * Verifies serialization.
	 * @throws JsonProcessingException 
	 */
	@Test
	public void testSerialize() throws JsonProcessingException {
		SensorPmTriplet pms = new SensorPmTriplet(1.0, 2.5, 10.0);
		SensorBmeMessage bme = new SensorBmeMessage(14.0, 15.0, 1001.0);
		SensorMessage message = new SensorMessage(pms, bme);
		ObjectMapper mapper = new ObjectMapper();
		String s = mapper.writeValueAsString(message);
		Assert.assertNotNull(s);
	}
	
}
