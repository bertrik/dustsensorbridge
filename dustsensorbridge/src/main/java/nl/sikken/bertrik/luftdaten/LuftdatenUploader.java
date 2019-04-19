package nl.sikken.bertrik.luftdaten;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.sikken.bertrik.IUploader;
import nl.sikken.bertrik.luftdaten.dto.LuftdatenItem;
import nl.sikken.bertrik.luftdaten.dto.LuftdatenMessage;
import nl.sikken.bertrik.sensor.dto.SensorMessage;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Uploader for luftdaten.info
 */
public final class LuftdatenUploader implements IUploader {
	
	private static final Logger LOG = LoggerFactory.getLogger(LuftdatenUploader.class);
	
	// the "PIN" we upload dust data to
	private static final String PIN = "1";

	private final ObjectMapper mapper = new ObjectMapper();
	private final ILuftdatenApi restClient;
	private final String softwareVersion;
	private final String sensorIdOverride;

	/**
	 * Constructor.
	 * 
	 * @param restClient the REST client
	 * @param softwareVersion the software version
	 * @param sensorIdOverride the sensor id override (empty means automatic sensor id)
	 */
	public LuftdatenUploader(ILuftdatenApi restClient, String softwareVersion, String sensorIdOverride) {
		this.restClient = restClient;
		this.softwareVersion = softwareVersion;
		this.sensorIdOverride = sensorIdOverride;
	}
	
	/**
	 * Creates a new REST client.
	 * 
	 * @param url the URL of the server, e.g. "https://api.luftdaten.info"
	 * @param timeout the timeout (ms)
	 * @return a new REST client.
	 */
	public static ILuftdatenApi newRestClient(String url, int timeout) {
		OkHttpClient client = new OkHttpClient().newBuilder()
				.connectTimeout(timeout, TimeUnit.MILLISECONDS)
				.writeTimeout(timeout, TimeUnit.MILLISECONDS)
				.readTimeout(timeout, TimeUnit.MILLISECONDS)
				.build();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(url)
				.addConverterFactory(ScalarsConverterFactory.create())
				.addConverterFactory(JacksonConverterFactory.create())
				.client(client)
				.build();
		
		return retrofit.create(ILuftdatenApi.class);
	}
	
	@Override
    public void uploadMeasurement(Instant now, int sensorId, SensorMessage message) {
    	LuftdatenMessage luftDatenMessage = new LuftdatenMessage(softwareVersion);
    	luftDatenMessage.addItem(new LuftdatenItem("P1", message.getPms().getPm10()));
    	luftDatenMessage.addItem(new LuftdatenItem("P2", message.getPms().getPm2_5()));
    	luftDatenMessage.addItem(new LuftdatenItem("P0", message.getPms().getPm1_0()));
    	String sensor;
    	if (!sensorIdOverride.isEmpty()) {
    		sensor = sensorIdOverride; 
    	} else {
    		sensor = String.format(Locale.US, "esp8266-%d", sensorId);
    	}
    	try {
    		LOG.info("Sending for {} to pin {}: '{}'", sensor, PIN, mapper.writeValueAsString(luftDatenMessage));
    		Response<String> response = restClient.pushSensorData(PIN, sensor, luftDatenMessage).execute();
    		if (response.isSuccessful()) {
    			LOG.info("Result success: {}", response.body());
    		} else {
    			LOG.warn("Request failed: {}", response.message());
    		}
    	} catch (IOException e) {
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
