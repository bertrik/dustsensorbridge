package nl.sikken.bertrik.sensor;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for MqttTopicParser.
 */
public final class MqttTopicParserTest {
	
	@Test
	public void testBasic() {
		MqttTopicParser parser = new MqttTopicParser("bertrik/dust/12345");
		Assert.assertEquals("12345", parser.getLast());
	}
	
	@Test
	public void testEmpty() {
		MqttTopicParser parser = new MqttTopicParser("");
		Assert.assertEquals("", parser.getLast());
	}
	
	@Test
	public void testSlash() {
		MqttTopicParser parser = new MqttTopicParser("/");
		Assert.assertEquals("", parser.getLast());
	}

}
