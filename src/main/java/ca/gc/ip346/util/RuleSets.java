
package ca.gc.ip346.util;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class RuleSets implements ServletContextListener {
	private static final String file = "ca/gc/ip346/util/rulesets.properties";
	private final Properties props   = new Properties();

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			props.load(RuleSets.class.getClassLoader().getResourceAsStream(file));
		} catch(IOException e) {
			e.printStackTrace();
		}
		for (String prop : props.stringPropertyNames()) {
			if (System.getProperty(prop) == null) {
				System.setProperty(prop, props.getProperty(prop));
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

	public static String getHome() {
		Properties props = new Properties();
		String home      = null;
		try {
			props.load(RuleSets.class.getClassLoader().getResourceAsStream(file));
			home = props.getProperty("RULESETS_HOME");
		} catch(IOException e) {
			e.printStackTrace();
		}
		return home;
	}
}
