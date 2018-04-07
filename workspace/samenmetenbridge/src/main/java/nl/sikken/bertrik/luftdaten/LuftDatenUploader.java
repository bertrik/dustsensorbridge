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
import nl.sikken.bertrik.luftdaten.dto.LuftDatenItem;
import nl.sikken.bertrik.luftdaten.dto.LuftDatenMessage;
import nl.sikken.bertrik.sensor.SensorMessage;

/**
 * Uploader for luftdaten.info
 */
public final class LuftDatenUploader implements IUploader {
	
	private static final Logger LOG = LoggerFactory.getLogger(LuftDatenUploader.class);
	
	private final ObjectMapper mapper = new ObjectMapper();
	private final ILuftDatenApi restClient;
	private final String softwareVersion;

	/**
	 * Constructor.
	 * 
	 * @param restClient the REST client
	 * @param softwareVersion the software version
	 */
	public LuftDatenUploader(ILuftDatenApi restClient, String softwareVersion) {
		this.restClient = restClient;
		this.softwareVersion = softwareVersion;
	}
	
	/**
	 * Creates a new REST client.
	 * 
	 * @param url the URL of the server, e.g. "https://api.luftdaten.info"
	 * @param timeout the timeout (ms)
	 * @param id the sensor id
	 * @return a new REST client.
	 */
	public static ILuftDatenApi newRestClient(String url, int timeout, String id) {
        final WebTarget target = ClientBuilder.newClient().property(ClientProperties.CONNECT_TIMEOUT, timeout)
                .property(ClientProperties.READ_TIMEOUT, timeout).target(url);
		Map<String, Object> headers = new HashMap<>();
		headers.put("X-Pin", "1");
		headers.put("X-Sensor", id);
		LOG.info("Creating new REST client with headers {}", headers);
		return WebResourceFactory.newResource(ILuftDatenApi.class, target, false,
				new MultivaluedHashMap<String, Object>(headers), Collections.<Cookie> emptyList(), new Form());
	}

    /**
     * Uploads a measurement message.
     * @param now the current time
     * @param message the message
     */
    public void uploadMeasurement(Instant now, SensorMessage message) {
    	LuftDatenMessage luftDatenMessage = new LuftDatenMessage(softwareVersion);
    	luftDatenMessage.addItem(new LuftDatenItem("P1", (double)message.getPms().getPm10()));
    	luftDatenMessage.addItem(new LuftDatenItem("P2", (double)message.getPms().getPm2_5()));
    	try {
    		LOG.info("Sending luftdaten.info message '{}'", mapper.writeValueAsString(luftDatenMessage));
    		restClient.pushSensorData(luftDatenMessage);
    	} catch (WebApplicationException | JsonProcessingException e) {
    		LOG.warn("Caught {}", e.getMessage());
    	}
    }

	@Override
	public void start() {
		LOG.info("Starting LuftDaten.info uploader");
	}

	@Override
	public void stop() {
		LOG.info("Stopping LuftDaten.info uploader");
	}
	
}
