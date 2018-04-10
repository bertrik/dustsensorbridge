package nl.sikken.bertrik;

import java.time.Instant;

import nl.sikken.bertrik.sensor.SensorMessage;

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
	 * Schedules a sensor measurement for upload (should be non-blocking).
	 * 
	 * @param now the current time
	 * @param message the sensor measurement
	 */
	void uploadMeasurement(Instant now, SensorMessage message);

}