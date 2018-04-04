package nl.sikken.bertrik.luftdaten;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import nl.sikken.bertrik.luftdaten.dto.LuftDatenMessage;
import nl.sikken.bertrik.sensor.SensorBmeMessage;
import nl.sikken.bertrik.sensor.SensorMessage;
import nl.sikken.bertrik.sensor.SensorPmTriplet;

/**
 * Unit tests of LuftDatenUploader.
 */
public final class LuftDatenUploaderTest {

	/**
	 * Verifies the upload process up until the REST client.
	 */
	@Test
	public void testUpload() {
		ILuftDatenApi api = Mockito.mock(ILuftDatenApi.class);
		LuftDatenUploader uploader = new LuftDatenUploader(api, "0.0");
		SensorMessage message = 
				new SensorMessage(new SensorPmTriplet(0.0, 2.5, 10.0), new SensorBmeMessage(0.0, 0, 1000.0));
		uploader.uploadMeasurement(Instant.now(), message);
		
		ArgumentCaptor<LuftDatenMessage> captor = ArgumentCaptor.forClass(LuftDatenMessage.class);
		Mockito.verify(api).pushSensorData(captor.capture());
		
		LuftDatenMessage actual = captor.getValue();
		Assert.assertEquals("0.0", actual.getSoftwareVersion());
		Assert.assertEquals("P1", actual.getItems().get(0).getName());
		Assert.assertEquals(10.0, actual.getItems().get(0).getValue(), 0.01);
	}

}
