package nl.sikken.bertrik;

import java.time.Instant;

import nl.sikken.bertrik.sensor.dto.SensorMessage;

/**
 * Uploader interface.
 */
public interface IUploader {

	/**
	 * Starts this sub-module.
	 */
	void start();

	/**
	 * Stops this sub-module.
	 */
	void stop();

	/**
	 * Uploads a sensor measurement.
	 * 
	 * @param now the current time
	 * @param sensorId the sensor id
	 * @param message the sensor measurement
	 */
	void uploadMeasurement(Instant now, int sensorId, SensorMessage message);

}