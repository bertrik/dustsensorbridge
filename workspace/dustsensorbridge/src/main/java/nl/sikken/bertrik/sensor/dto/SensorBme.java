package nl.sikken.bertrik.sensor.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Set of meteo sensor values from a BME280.
 */
public final class SensorBme {

	@JsonProperty("t")
	private double temp;
	@JsonProperty("rh")
	private double rh;
	@JsonProperty("p")
	private double pressure;

	private SensorBme() {
		// jackson constructor
	}
	
	/**
	 * @param temp temperature (celcius)
	 * @param rh relative humidity (percent)
	 * @param pressure (mbar or hectopascal)
	 */
	public SensorBme(double temp, double rh, double pressure) {
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

	public boolean hasValidTemp() {
		return (temp > -100.0) && (temp < 100.0);
	}

	public boolean hasValidRh() {
		return (rh >= 0) && (rh <= 100.0);
	}

	public boolean hasValidPressure() {
		return (pressure > 800.0) && (pressure < 1200.0);
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "{t=%f,rh=%f,p=%f}", temp, rh, pressure);
	}
	
}
