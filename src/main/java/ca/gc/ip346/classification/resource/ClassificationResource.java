package ca.gc.ip346.classification.resource;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.currentDate;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
// import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import ca.gc.ip346.classification.model.CanadaFoodGuideDataset;
import ca.gc.ip346.classification.model.Dataset;
import ca.gc.ip346.util.MongoClientFactory;

@Path("/")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ClassificationResource {
	private static final Logger logger           = LogManager.getLogger(ClassificationResource.class);
	private List<String> rules                   = null;
	private MongoClient mongoClient              = null;
	private MongoCollection<Document> collection = null;

	/**
	 *
	 * Obtain the complete set of rulesets from kmodule.xml - a list of identifiers
	 * Check to see if rulesets' identifiers exist in MongoDB and overwrite them if they don't
	 *
	 */
	public ClassificationResource() {
		mongoClient = MongoClientFactory.getMongoClient();
		collection  = mongoClient.getDatabase(MongoClientFactory.getDatabase()).getCollection(MongoClientFactory.getCollection());
		logger.error("[01;03;31m" + "mongo connectivity test: " + mongoClient.getDB(MongoClientFactory.getDatabase()).command("buildInfo").getString("version") + "[00;00m");

		rules = new ArrayList<String>();
		KieServices ks          = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		String pattern = "(\\S+)-\\w+";

		logger.error("[01;03;31m\n" + "list out the complete set of rulesets" + "[00;00m");
		logger.error("[01;03;31m\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(kContainer.getKieBaseNames()) + "[00;00m");
		for (String kieBaseName : kContainer.getKieBaseNames()) {
			logger.error("[01;03;31m\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(kContainer.getKieSessionNamesInKieBase(kieBaseName)) + "[00;00m");
			for (String session : kContainer.getKieSessionNamesInKieBase(kieBaseName)) {
				String rule = session.replaceAll(pattern, "$1");
				rules.add(rule);
			}
			break;
		}

		logger.error("[01;03;31m\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(rules) + "[00;00m");

		// Check to see if rulesets' identifiers exist in MongoDB and overwrite them if they don't

		MongoCursor<Document> cursorDocMap = null;

		for (String rule : rules) {
			Boolean isNotValidRulesetId = true;

			if (ObjectId.isValid(rule)) {
				System.out.println("[01;31m" + "Valid hexadecimal representation of RulesetId " + rule + "[00;00m");

				cursorDocMap = collection.find(new Document("_id", new ObjectId(rule))).iterator();
				if (!cursorDocMap.hasNext()) {
					// create new item and replace current rule value with new _id
					isNotValidRulesetId = false;
				}
			}

			if (isNotValidRulesetId) {
				Document doc = new Document()
					.append("name",     null)
					.append("isProd",   null)
					.append("location", null);
				collection.insertOne(doc);
				ObjectId id = (ObjectId)doc.get("_id");
				collection.updateOne(
						eq("_id", id),
						combine(
							set("name", null),
							set("comments", null),
							currentDate("modifiedDate"))
						);
				rules.add(rules.indexOf(rule), id.toString());
			}
		}

		logger.error("[01;03;31m\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(rules) + "[00;00m");
	}

	// @OPTIONS
	// @Path("/classify")
	// @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// public Map<String, String> classifyDatasetPreflight() {
		// Map<String, String> msg = new HashMap<String, String>();
		// msg.put("message", "options-catch-all");
		// return msg;
	// }

	@POST
	@Path("/classify")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> classifyDataset(Dataset dataset) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CanadaFoodGuideDataset> foods = dataset.getData();

		foods = FlagsEngine      .flagsEngine      .setFlags (foods); // Step 1: RA Adjustment
		foods = InitEngine       .initEngine       .setInit  (foods); // Step 2: Threshold Rule
		foods = AdjustmentEngine .adjustmentEngine .adjust   (foods); // Step 3: Adjustments
		foods = prepareCfgCode(foods);

		List<CanadaFoodGuideDataset> foodResults = foods;
		dataset.setStatus("Classified");
		map.put("data",     foodResults);
		map.put("name",     dataset.getName());
		map.put("status",   dataset.getStatus());
		map.put("env",      dataset.getEnv());
		map.put("owner",    dataset.getOwner());
		map.put("comments", dataset.getComments());
		return map;
	}

	@GET
	@Path("/rulesets")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> getRulesets() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rules", rules);
		return map;
	}

	@POST
	@Path("/flags")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> flagsDataset(Dataset dataset) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		List<CanadaFoodGuideDataset> foodResults = FlagsEngine.flagsEngine.setFlags(foods);
		map.put("data", foodResults);
		return map;
	}

	@POST
	@Path("/init")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> initDataset(Dataset dataset) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		List<CanadaFoodGuideDataset> foodResults = InitEngine.initEngine.setInit(foods);
		map.put("data", foodResults);
		return map;
	}

	@POST
	@Path("/adjustment")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> adjustmentDataset(Dataset dataset) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		List<CanadaFoodGuideDataset> foodResults = AdjustmentEngine.adjustmentEngine.adjust(foods);
		map.put("data", foodResults);
		return map;
	}

	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, String> test() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("msg","Test suceeded" );
		return map;
	}

	private List<CanadaFoodGuideDataset> prepareCfgCode(List<CanadaFoodGuideDataset> foods) {
		for (CanadaFoodGuideDataset food : foods) {
			Integer temp = food.getCfgCode();
			food.setCfgCode(food.getClassifiedCfgCode());
			food.setClassifiedCfgCode(temp);
		}
		return foods;
	}
}
