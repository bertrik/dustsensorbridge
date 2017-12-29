package nl.sikken.bertrik;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    private final MqttListener mqttListener;
    private final SamenMetenUploader samenMetenUploader;
    private final SensorInfo sensorInfo;
    private final ObjectMapper mapper;

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
        this.samenMetenUploader = new SamenMetenUploader(config.getInfluxUrl(), config.getInfluxUsername(),
                config.getInfluxPassword());
        this.sensorInfo = new SensorInfo(config.getSensorId(), config.getSensorLat(), config.getSensorLon());
        this.mapper = new ObjectMapper();
    }

    /**
     * Starts the application.
     * 
     * @throws MqttException in case of a problem starting MQTT client
     */
    private void start() throws MqttException {
        LOG.info("Starting SamenMeten bridge application");

        // start sub-modules
        samenMetenUploader.start();
        mqttListener.start();

        LOG.info("Started SamenMeten bridge application");
    }

    /**
     * Handles an incoming MQTT message
     * 
     * @param topic the topic on which the message was received
     * @param textMessage the message contents
     */
    private void handleSensorMessage(String topic, String textMessage) {
        try {
        	final Instant now = Instant.now();

        	// decode from JSON
            final SensorMessage message = mapper.readValue(textMessage, SensorMessage.class);
            
            // send payload telemetry data
            samenMetenUploader.uploadMeasurement(sensorInfo, message, now);
        } catch (IOException e) {
            LOG.warn("JSON unmarshalling exception '{}' for {}", e.getMessage(), textMessage);
        }
    }

    /**
     * Stops the application.
     * 
     * @throws MqttException
     */
    private void stop() {
        LOG.info("Stopping SamenMeten bridge application");
        mqttListener.stop();
        samenMetenUploader.stop();
        LOG.info("Stopped SamenMeten bridge application");
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
