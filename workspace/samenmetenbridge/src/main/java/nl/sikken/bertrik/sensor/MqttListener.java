package nl.sikken.bertrik.sensor;

import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener process for receiving data from the TTN.
 */
public final class MqttListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(MqttListener.class);
    private static final long DISCONNECT_TIMEOUT_MS = 3000;
    
    private final String clientId;
    private final IMessageReceived callback;
    private final String url;
    private final String topic;

    private MqttClient mqttClient;

    /**
     * Constructor.
     * 
     * @param callback the interface for indicating a received message.
     * @param url the URL of the MQTT server
     */
    public MqttListener(IMessageReceived callback, String url, String topic) {
        this.clientId = MqttClient.generateClientId();
        this.callback = callback;
        this.url = url;
        this.topic = topic;
    }
    
    /**
     * Starts this module.
     * 
     * @throws MqttException in case something went wrong with MQTT 
     */
    public void start() throws MqttException {
        LOG.info("Starting MQTT listener");
        
        // connect
        LOG.info("Connecting to MQTT server {}", url);
        this.mqttClient = new MqttClient(url, clientId, new MemoryPersistence());
        final MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        mqttClient.connect(options);
        
        // subscribe
        LOG.info("Subscribing to topic '{}'", topic);
        mqttClient.subscribe(topic, this::messageArrived);
    }
    
    /**
     * Handles an incoming message.
     * 
     * @param topic the topic
     * @param mqttMessage the message
     * @throws Exception who knows?
     */
    private void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        final String message = new String(mqttMessage.getPayload(), StandardCharsets.US_ASCII);
        LOG.info("Message arrived on topic '{}': {}", topic, message);
        // forward it to our user
        callback.messageReceived(topic, message);
    }
    
    /**
     * Stops this module.
     */
    public void stop() {
        LOG.info("Stopping TTN listener");
        try {
            mqttClient.disconnect(DISCONNECT_TIMEOUT_MS);
        } catch (MqttException e) {
            // don't care, just log
            LOG.warn("Caught exception on disconnect: {}", e.getMessage());
        } finally {
            try {
                mqttClient.close();
            } catch (MqttException e) {
                // don't care, just log
                LOG.warn("Caught exception on close: {}", e.getMessage());
            }
        }
        LOG.info("Stopped TTN listener");
    }
    
}
