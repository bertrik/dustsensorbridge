package nl.sikken.bertrik;

/**
 * Configuration interface for the application.
 */
public interface ISamenMetenBridgeConfig {

    String getSensorId();
    
    Double getSensorLat();
    
    Double getSensorLon();
    
    /**
     * @return the URL of the MQTT server
     */
    String getMqttUrl();
    
    String getMqttTopic();
    
    /**
     * @return URL of the habitat server
     */
    String getInfluxUrl();
    
    String getInfluxUsername();

    String getInfluxPassword();

    String getInfluxDbName();


}
