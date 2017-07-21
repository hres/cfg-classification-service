package ca.gc.ip346.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.mongodb.MongoClient;

public class MongoClientFactory {
	private static Properties getProps() throws IOException {
		Properties props = new Properties();
		InputStream in   = MongoClientFactory.class.getClassLoader().getResourceAsStream("ca/gc/ip346/util/mongodb.properties");
		props.load(in);
		in.close();
		return props;
	}

	public static MongoClient getMongoClient() {
		String host  = null;
		Integer port = null;
		try {
			host = getProps().getProperty("host");
			port = Integer.parseInt(getProps().getProperty("port"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new MongoClient(host, port);
	}

	public static String getDatabase() {
		String database = null;
		try {
			database = getProps().getProperty("database");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return database;
	}

	public static String getCollection() {
		String collection = null;
		try {
			collection = getProps().getProperty("collection");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return collection;
	}
}
