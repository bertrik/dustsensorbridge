package nl.sikken.bertrik.luftdaten.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LuftDaten message as uploaded through a POST.
 */
public final class LuftDatenMessage {
	
	@JsonProperty("software_version")
	private String softwareVersion;
	
	@JsonProperty("sensordatavalues")
	private final List<LuftDatenItem> items = new ArrayList<>();
	
	private LuftDatenMessage() {
		// jackson constructor
	}
	
	/**
	 * Constructor.
	 * 
	 * @param softwareVersion the software version
	 */
	public LuftDatenMessage(String softwareVersion) {
		this();
		this.softwareVersion = softwareVersion;
	}

	public void addItem(LuftDatenItem item) {
		items.add(item);
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public List<LuftDatenItem> getItems() {
		return Collections.unmodifiableList(items);
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "{softwareVersion=%s,items=%s}", softwareVersion, items);
	}
	
}
