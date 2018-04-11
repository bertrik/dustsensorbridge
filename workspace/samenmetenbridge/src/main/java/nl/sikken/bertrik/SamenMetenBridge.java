package nl.sikken.bertrik;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.sikken.bertrik.luftdaten.ILuftdatenApi;
import nl.sikken.bertrik.luftdaten.LuftdatenUploader;
import nl.sikken.bertrik.samenmeten.SamenMetenUploader;
import nl.sikken.bertrik.sensor.MqttListener;
import nl.sikken.bertrik.sensor.SensorInfo;
import nl.sikken.bertrik.sensor.SensorMessage;

/**
 * Bridge between the-things-network and the habhub network.
 * 
 */
public final class SamenMetenBridge {

    private static final Logger LOG = LoggerFactory.getLogger(SamenMetenBridge.class);
    private static final String CONFIG_FILE = "samenmetenbridge.properties";

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
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
        final ISamenMetenBridgeConfig config = readConfig(new File(CONFIG_FILE));
        final SamenMetenBridge app = new SamenMetenBridge(config);

        Thread.setDefaultUncaughtExceptionHandler(app::handleUncaughtException);

        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    /**
     * Constructor.
     * 
     * @param config the application configuration
     */
    private SamenMetenBridge(ISamenMetenBridgeConfig config) {
        this.mqttListener = new MqttListener(this::handleSensorMessage, config.getMqttUrl(), config.getMqttTopic());
        
        // general sensor info 
        SensorInfo sensorInfo = new SensorInfo(config.getSensorLat(), config.getSensorLon());
        
        // samenmeten.net
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
	        ILuftdatenApi luftDatenApi = LuftdatenUploader.newRestClient(config.getLuftdatenUrl(), 
	        		config.getLuftdatenTimeout(), "1", config.getLuftdatenId());
	        IUploader luftDatenUploader = new LuftdatenUploader(luftDatenApi, config.getLuftdatenVersion());
	        uploaders.add(luftDatenUploader);
        }
    }

    /**
     * Starts the application.
     * 
     * @throws MqttException in case of a problem starting MQTT client
     */
    private void start() throws MqttException {
        LOG.info("Starting SamenMeten bridge application");

        // start sub-modules
        uploaders.forEach(u -> u.start());
        mqttListener.start();

        LOG.info("Started SamenMeten bridge application");
    }

    /**
	 * Stops the application.
	 * 
	 * @throws MqttException
	 */
	private void stop() {
	    LOG.info("Stopping SamenMeten bridge application");

	    mqttListener.stop();
	    executor.shutdown();
        uploaders.forEach(u -> u.stop());

	    LOG.info("Stopped SamenMeten bridge application");
	}

	/**
     * Handles an incoming MQTT message
     * 
     * @param topic the topic on which the message was received
     * @param textMessage the message contents
     */
    private void handleSensorMessage(String topic, String textMessage) {
        try {
        	final Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        	// decode from JSON
            final SensorMessage message = mapper.readValue(textMessage, SensorMessage.class);
            
            // send payload telemetry data in the background (to avoid blocking the MQTT callback)
            executor.submit(() -> uploadMeasurement(now, message));
        } catch (IOException e) {
            LOG.warn("JSON unmarshalling exception '{}' for {}", e.getMessage(), textMessage);
        }
    }
    
    /**
     * Performs the actual uploads of sensor data sequentially in the background.
     * 
     * @param now the time stamp
     * @param message the message
     */
    private void uploadMeasurement(Instant now, SensorMessage message) {
        for (IUploader uploader : uploaders) {
        	uploader.uploadMeasurement(now, message);
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
    
    private static ISamenMetenBridgeConfig readConfig(File file) throws IOException {
        final SamenMetenBridgeConfig config = new SamenMetenBridgeConfig();
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
