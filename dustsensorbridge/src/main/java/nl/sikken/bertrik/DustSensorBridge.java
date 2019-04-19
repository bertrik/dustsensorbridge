package nl.sikken.bertrik;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.sikken.bertrik.luftdaten.ILuftdatenApi;
import nl.sikken.bertrik.luftdaten.LuftdatenUploader;
import nl.sikken.bertrik.samenmeten.SamenMetenUploader;
import nl.sikken.bertrik.sensor.MqttListener;
import nl.sikken.bertrik.sensor.MqttTopicParser;
import nl.sikken.bertrik.sensor.SensorInfo;
import nl.sikken.bertrik.sensor.dto.SensorMessage;

/**
 * Bridge between the-things-network and the habhub network.
 * 
 */
public final class DustSensorBridge {

    private static final Logger LOG = LoggerFactory.getLogger(DustSensorBridge.class);
    private static final String CONFIG_FILE = "dustsensorbridge.properties";
	private static final String SOFTWARE_VERSION = "0.2";

	private final ObjectMapper mapper = new ObjectMapper();
    private final MqttListener mqttListener;
    private final List<IUploader> uploaders = new ArrayList<>();

    /**
     * Main application entry point.
     * 
     * @param arguments application arguments (none taken)
     * @throws IOException in case of a problem reading a config file
     * @throws MqttException in case of a problem starting MQTT client
     */
    public static void main(String[] arguments) throws IOException, MqttException {
        final IDustSensorBridgeConfig config = readConfig(new File(CONFIG_FILE));
        final DustSensorBridge app = new DustSensorBridge(config);

        Thread.setDefaultUncaughtExceptionHandler(app::handleUncaughtException);

        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    /**
     * Constructor.
     * 
     * @param config the application configuration
     */
    private DustSensorBridge(IDustSensorBridgeConfig config) {
        this.mqttListener = new MqttListener(this::handleSensorMessage, config.getMqttUrl(), config.getMqttTopic());
        
        // general sensor info 
        SensorInfo sensorInfo = new SensorInfo(config.getSensorLat(), config.getSensorLon());
        
        // samenmeten
        if (!config.getSamenMetenUrl().isEmpty()) {
        	LOG.info("Adding SamenMeten uploader");
			ServerInfo samenMetenInfo = new ServerInfo(config.getSamenMetenUrl(), config.getSamenMetenUsername(),
					config.getSamenMetenPassword(), config.getSamenMetenId());
	        IUploader samenMetenUploader = new SamenMetenUploader(samenMetenInfo, sensorInfo);
	        uploaders.add(samenMetenUploader);
        }
        
        // luftdaten.info
        if (!config.getLuftdatenUrl().isEmpty()) {
        	LOG.info("Adding Luftdaten uploader");
	        ILuftdatenApi luftDatenApi = 
	        		LuftdatenUploader.newRestClient(config.getLuftdatenUrl(), config.getLuftdatenTimeout());
			IUploader luftDatenUploader = new LuftdatenUploader(luftDatenApi, SOFTWARE_VERSION,
					config.getLuftdatenIdOverride());
	        uploaders.add(luftDatenUploader);
        }
    }

    /**
     * Starts the application.
     * 
     * @throws MqttException in case of a problem starting MQTT client
     */
    private void start() throws MqttException {
        LOG.info("Starting DustSensor bridge application");

        // start sub-modules
        uploaders.forEach(u -> u.start());
        mqttListener.start();

        LOG.info("Started DustSensor bridge application");
    }

    /**
	 * Stops the application.
	 * 
	 * @throws MqttException
	 */
	private void stop() {
	    LOG.info("Stopping DustSensor bridge application");

	    mqttListener.stop();
        uploaders.forEach(u -> u.stop());

	    LOG.info("Stopped DustSensor bridge application");
	}

	/**
     * Handles an incoming MQTT message.
     * 
     * This method is called in a thread separate from the MQTT thread so is allowed to take some time.
     * 
     * @param instant the time stamp of message reception
     * @param topic the topic on which the message was received
     * @param textMessage the message contents
     */
    void handleSensorMessage(Instant instant, String topic, String textMessage) {
    	final Instant now = instant.truncatedTo(ChronoUnit.SECONDS);

    	// decode topic
    	int sensorId;
    	try {
    		MqttTopicParser topicParser = new MqttTopicParser(topic);
    		sensorId = Integer.parseInt(topicParser.getLast(), 16);
    	} catch (NumberFormatException e) {
    		LOG.warn("Could not parse topic '{}'", topic);
    		return;
    	}

    	// decode message from JSON
    	try {
            SensorMessage message = mapper.readValue(textMessage, SensorMessage.class);
            
            // send payload telemetry data in the background (to avoid blocking the MQTT callback)
            if (message.getPms() != null) {
                for (IUploader uploader : uploaders) {
                	uploader.uploadMeasurement(now, sensorId, message);
                }
            } else {
            	LOG.warn("Ignoring message on topic '{}', no PMS data", topic);
            }
        } catch (IOException e) {
            LOG.warn("JSON unmarshalling exception '{}' for {}", e.getMessage(), textMessage);
        }
    }

    /**
     * Handles uncaught exceptions: log it and stop the application.
     * 
     * @param t the thread
     * @param e the exception
     */
    private void handleUncaughtException(Thread t, Throwable e) {
        LOG.error("Caught unhandled exception, application will be stopped ...", e);
        stop();
    }
    
    private static IDustSensorBridgeConfig readConfig(File file) throws IOException {
        final DustSensorBridgeConfig config = new DustSensorBridgeConfig();
        try (FileInputStream fis = new FileInputStream(file)) {
            config.load(fis);
        } catch (IOException e) {
            LOG.warn("Failed to load config {}, writing defaults", file.getAbsoluteFile());
            try (FileOutputStream fos = new FileOutputStream(file)) {
                config.save(fos);
            }
        }
        return config;
    }

}
