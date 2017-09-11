package ca.gc.ip346.classification.cors;

import static org.apache.logging.log4j.Level.DEBUG;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class CORSFilter implements ContainerResponseFilter {
	private static final Logger logger = LogManager.getLogger(CORSFilter.class);

	@Override
	public void filter(final ContainerRequestContext request, final ContainerResponseContext response) throws IOException {
		response.getHeaders().add("Access-Control-Allow-Origin",      "*");
		response.getHeaders().add("Access-Control-Allow-Headers",     "ORIGIN, CONTENT-TYPE, ACCEPT, AUTHORIZATION, ACCESS_CONTROL_ALLOW_ORIGIN, X-REQUESTED-WITH");
		response.getHeaders().add("Access-Control-Allow-Credentials", "true");
		response.getHeaders().add("Access-Control-Allow-Methods",     "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		response.getHeaders().add("Access-Control-Max-Age",           "1209600");
		response.getHeaders().add("Referrer-Policy",                  "no-referrer");

		logger.printf(DEBUG, "%s%s%s", "[01;03;33m", "Romario's CORS Magic!! - *", "[00;00;00m");
		logger.printf(DEBUG, "%s%s%s", "[01;03;33m", "Romario's CORS Magic!! - *", "[00;00;00m");
		logger.printf(DEBUG, "%s%s%s", "[01;03;33m", "Romario's CORS Magic!! - *", "[00;00;00m");
		logger.printf(DEBUG, "%s%s%s", "[01;03;33m", "Romario's CORS Magic!! - *", "[00;00;00m");
	}
}
