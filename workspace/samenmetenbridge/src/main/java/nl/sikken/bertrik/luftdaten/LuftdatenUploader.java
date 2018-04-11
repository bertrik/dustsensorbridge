package nl.sikken.bertrik.luftdaten;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.sikken.bertrik.IUploader;
import nl.sikken.bertrik.luftdaten.dto.LuftdatenItem;
import nl.sikken.bertrik.luftdaten.dto.LuftdatenMessage;
import nl.sikken.bertrik.sensor.SensorMessage;

/**
 * Uploader for luftdaten.info
 */
public final class LuftdatenUploader implements IUploader {
	
	private static final Logger LOG = LoggerFactory.getLogger(LuftdatenUploader.class);
	
	private final ObjectMapper mapper = new ObjectMapper();
	private final ILuftdatenApi restClient;
	private final String softwareVersion;

	/**
	 * Constructor.
	 * 
	 * @param restClient the REST client
	 * @param softwareVersion the software version
	 */
	public LuftdatenUploader(ILuftdatenApi restClient, String softwareVersion) {
		this.restClient = restClient;
		this.softwareVersion = softwareVersion;
	}
	
	/**
	 * Creates a new REST client.
	 * 
	 * @param url the URL of the server, e.g. "https://api.luftdaten.info"
	 * @param timeout the timeout (ms)
	 * @param pin the value of the "X-Pin" header
	 * @param id the value of the "X-Sensor" header
	 * @return a new REST client.
	 */
	public static ILuftdatenApi newRestClient(String url, int timeout, String pin, String id) {
        final WebTarget target = ClientBuilder.newClient().property(ClientProperties.CONNECT_TIMEOUT, timeout)
                .property(ClientProperties.READ_TIMEOUT, timeout).target(url);
		Map<String, Object> headers = new HashMap<>();
		headers.put("X-Pin", pin);
		headers.put("X-Sensor", id);
		LOG.info("Creating new REST client for URL '{}' with timeout {} and headers {}", url, timeout, headers);
		return WebResourceFactory.newResource(ILuftdatenApi.class, target, false,
				new MultivaluedHashMap<String, Object>(headers), Collections.<Cookie> emptyList(), new Form());
	}

    /* (non-Javadoc)
     * @see nl.sikken.bertrik.IUploader#uploadMeasurement(java.time.Instant, nl.sikken.bertrik.sensor.SensorMessage)
     */
    public void uploadMeasurement(Instant now, SensorMessage message) {
    	LuftdatenMessage luftDatenMessage = new LuftdatenMessage(softwareVersion);
    	luftDatenMessage.addItem(new LuftdatenItem("P1", (double)message.getPms().getPm10()));
    	luftDatenMessage.addItem(new LuftdatenItem("P2", (double)message.getPms().getPm2_5()));
    	try {
    		LOG.info("Sending luftdaten.info message '{}'", mapper.writeValueAsString(luftDatenMessage));
    		String result = restClient.pushSensorData(luftDatenMessage);
    		LOG.info("Result: {}", result);
    	} catch (WebApplicationException | JsonProcessingException e) {
    		LOG.warn("Caught exception '{}'", e.getMessage());
    	}
    }

	@Override
	public void start() {
		LOG.info("Starting Luftdaten.info uploader");
	}

	@Override
	public void stop() {
		LOG.info("Stopping Luftdaten.info uploader");
	}
	
}
