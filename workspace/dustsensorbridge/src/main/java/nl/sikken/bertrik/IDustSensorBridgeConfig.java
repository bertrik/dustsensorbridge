package nl.sikken.bertrik;

/**
 * Configuration interface for the application.
 */
public interface IDustSensorBridgeConfig {

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
     * @return URL of the samenmeten db
     */
    String getSamenMetenUrl();
    
    /**
     * @return the samenmeten id
     */
    String getSamenMetenId();
    
    /**
     * @return user name for the samenmeten db
     */
    String getSamenMetenUsername();

    /**
     * @return password for the samenmeten db
     */
    String getSamenMetenPassword();

	String getLuftdatenUrl();
	int getLuftdatenTimeout();
	String getLuftdatenId();
	String getLuftdatenVersion();

}
