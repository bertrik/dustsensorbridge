package nl.sikken.bertrik;

/**
 * Configuration interface for the application.
 */
public interface ISamenMetenBridgeConfig {

    /**
     * @return the unique sensor id
     */
    String getSensorId();
    
    /**
     * @return the sensor latitude
     */
    Double getSensorLat();
    
    /**
     * @return the sensor longitude
     */
    Double getSensorLon();
    
    /**
     * @return the URL of the MQTT server
     */
    String getMqttUrl();
    
    /**
     * @return the MQTT topic to listen on
     */
    String getMqttTopic();
    
    /**
     * @return URL of the influx db
     */
    String getInfluxUrl();
    
    /**
     * @return user name for the influx db
     */
    String getInfluxUsername();

    /**
     * @return password for the influx db
     */
    String getInfluxPassword();

}
