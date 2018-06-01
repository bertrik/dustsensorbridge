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

	/**
	 * @return the URL of the luftdaten.info API
	 */
	String getLuftdatenUrl();
	
	/**
	 * @return timeout (ms) for accessing the luftdaten.info API
	 */
	int getLuftdatenTimeout();
	
	/**
	 * @return the luftdaten id to send to luftdaten info (empty to use the MQTT topic)
	 */
	String getLuftdatenIdOverride();
	
}
