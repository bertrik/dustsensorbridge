package nl.sikken.bertrik.luftdaten.dto;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test for LuftDatenMessage.
 */
public final class LuftDatenMessageTest {

	private static final Logger LOG = LoggerFactory.getLogger(LuftDatenMessageTest.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	/**
	 * Verifies serialization.
	 * @throws IOException 
	 */
	@Test
	public void testSerialize() throws IOException {
		// serialize
		LuftDatenMessage msg = new LuftDatenMessage("1.0");
		msg.addItem(new LuftDatenItem("P1", 45.1));
		String s = MAPPER.writeValueAsString(msg);
		LOG.info("serialized: {}", s);

		// decode
		LuftDatenMessage decoded = MAPPER.readValue(s, LuftDatenMessage.class);

		Assert.assertEquals(msg.getSoftwareVersion(), decoded.getSoftwareVersion());
		Assert.assertEquals(msg.getItems().get(0).getName(), decoded.getItems().get(0).getName());
		Assert.assertEquals(msg.getItems().get(0).getValue(), decoded.getItems().get(0).getValue());
	}
	
}
