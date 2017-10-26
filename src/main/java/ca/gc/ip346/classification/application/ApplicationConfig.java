package ca.gc.ip346.classification.application;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
@ApplicationPath("/")
public class ApplicationConfig extends Application {
	// import static org.apache.logging.log4j.Level.*;
	private static final Logger logger = LogManager.getLogger(ApplicationConfig.class);


	@Context
	private ServletContext sc;

	@Override
	public Set<Class<?>> getClasses() {

		Set<Class<?>> resources = new java.util.HashSet<>();

		System.out.println("[01;35m" + "Food Classification REST services (cfg-classification-service) configuration starting: getClasses()" + "[00;00m");

		logger.debug("[01;03;36m" + "hello: " + sc.getInitParameter("RULESETS_HOME") + "[00;00m");
		logger.debug("[01;03;36m" + "cntxt: " + sc.getContextPath() + "[00;00m");
		logger.debug("[01;03;36m" + "paths: " + sc.getResourcePaths("/") + "[00;00m");

		//features
		//this will register Jackson JSON providers
		resources.add(org.glassfish.jersey.jackson.JacksonFeature.class);
		resources.add(com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider.class);

		resources.add(ca.gc.ip346.classification.resource.ClassificationResource.class);
		//==> we could also choose packages, see below getProperties()
		System.out.println("[01;35m" + "Food Classification REST services (cfg-classification-service) configuration ended successfully." + "[00;00m");

		return resources;
	}

	@Override
	public Set<Object> getSingletons() {
		return Collections.emptySet();
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new HashMap<>();

		//in Jersey WADL generation is enabled by default, but we don't
		//want to expose too much information about our apis.
		//therefore we want to disable wadl (http://localhost:8080/service/application.wadl should return http 404)
		//see https://jersey.java.net/nonav/documentation/latest/user-guide.html#d0e9020 for details
		properties.put("jersey.config.server.wadl.disableWadl", true);

		return properties;
	}
}
