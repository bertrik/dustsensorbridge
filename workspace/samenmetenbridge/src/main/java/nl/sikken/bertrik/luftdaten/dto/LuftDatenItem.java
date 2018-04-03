package nl.sikken.bertrik.luftdaten.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One luftdaten.info measurement item.
 */
public final class LuftDatenItem {

	@JsonProperty("value_type")
	private String name;
	@JsonProperty("value")
	private Double value;
	
	private LuftDatenItem() {
		// jackson constructor
	}
	
	/**
	 * Constructor.
	 * 
	 * @param name the item name
	 * @param value the item value
	 */
	public LuftDatenItem(String name, Double value) {
		this();
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public Double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "{name=%s,value=%s}", name, value);
	}
	
}
