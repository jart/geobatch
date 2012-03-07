package it.geosolutions.geobatch.imagemosaic;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.geotools.TestData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.org.apache.xerces.internal.xs.XSException;

/**
 * Test the ImageMosaicCommand
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 */
public class ImageMosaicCommandTest {
	ImageMosaicCommand cmd = null;
	File cmdFile;

	@Before
	public void setUp() throws Exception {
		// create in memory object
		List<File> addList = new ArrayList<File>();
		addList.add(TestData.file(this,
				"time_mosaic/world.200406.3x5400x2700.tiff"));
		List<File> delList = new ArrayList<File>();
		delList.add(TestData.file(this,
				"time_mosaic/world.200406.3x5400x2700.tiff"));
		cmd = new ImageMosaicCommand(
				new File(TestData.file(this, null), "test"), addList, delList);
		
		cmdFile = new File(TestData.file(this, null), "test_cmd_out.xml");
	}

	@After
	public void tearDown() throws Exception {
		cmd = null;
		FileUtils.deleteQuietly(cmdFile);
	}

	@Test
	public final void testSerialize() throws IOException {
		try {
			final String path = cmdFile.getAbsolutePath();

			// change params
			
			// GeoServerActionConfig
			cmd.setGeoserverUID("admin");
			
			// GeoServerActionConfiguration
			cmd.setCrs("EPSG:4326");
			
			// ImageMosaicConfiguration
			cmd.setAllowMultithreading(true);
			
			File out = ImageMosaicCommand.serialize(cmd, path);

			if (!out.exists())
				fail("Unable to serialize object to: " + path);

			ImageMosaicCommand cmd2 = ImageMosaicCommand.deserialize(cmdFile);
			if (cmd2 == null)
				fail("Unable to deserialize object from: " + path);

		} catch (XSException e) {
			fail(e.getMessage());
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (SecurityException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public final void testToString() {
		Assert.assertNotNull("null cmd object", cmd);
		Assert.assertNotNull(cmd.toString());
	}

	@Test
	public final void testClone() {
		ImageMosaicCommand cmd2 = cmd.clone();
		Assert.assertNotNull(cmd2);
	}

}