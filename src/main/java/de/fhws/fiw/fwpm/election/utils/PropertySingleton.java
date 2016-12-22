package de.fhws.fiw.fwpm.election.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by christianbraun on 12/07/16.
 */
public class PropertySingleton {
	private static Properties instance;
	private static InputStream inputStream;

	private PropertySingleton(){};

	public static Properties getInstance(boolean createNew, String fileName) throws IOException {
		if(instance == null || createNew){
			instance = new Properties();

			inputStream = PropertySingleton.class.getResourceAsStream("/" + fileName);
			instance.load(inputStream);
		}
			return instance;
	}

	public static Properties getInstance() throws IOException {
		return getInstance(false, "election.properties");
	}
}
