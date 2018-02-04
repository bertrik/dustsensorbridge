package nl.sikken.bertrik.sensor;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Set of meteo sensor values from a BME280.
 */
public final class SensorBmeMessage {

	@JsonProperty("t")
	private double temp;
	@JsonProperty("rh")
	private double rh;
	@JsonProperty("p")
	private double pressure;

	private SensorBmeMessage() {
		// jackson constructor
	}
	
	/**
	 * @param temp temperature (celcius)
	 * @param rh relative humidity (percent)
	 * @param pressure (mbar or hectopascal)
	 */
	public SensorBmeMessage(double temp, double rh, double pressure) {
		this();
		this.temp = temp;
		this.rh = rh;
		this.pressure = pressure;
	}

	public double getTemp() {
		return temp;
	}

	public double getRh() {
		return rh;
	}

	public double getPressure() {
		return pressure;
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "{t=%f,rh=%f,p=%f}", temp, rh, pressure);
	}
	
}
