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
import javax.ws.rs.DELETE;
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
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
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
	private ReleaseId releaseId                  = null;

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

		rules                   = new ArrayList<String>();
		KieServices ks          = KieServices.Factory.get();

		KieModuleModel kieModuleModel    = ks.newKieModuleModel();
		KieBaseModel kieBaseModel1       = kieModuleModel.newKieBaseModel("dtables.refamt");
		KieBaseModel kieBaseModel2       = kieModuleModel.newKieBaseModel("dtables.fop");
		KieBaseModel kieBaseModel3       = kieModuleModel.newKieBaseModel("dtables.shortcut");
		KieBaseModel kieBaseModel4       = kieModuleModel.newKieBaseModel("dtables.thresholds");
		KieBaseModel kieBaseModel5       = kieModuleModel.newKieBaseModel("dtables.init");
		KieBaseModel kieBaseModel6       = kieModuleModel.newKieBaseModel("dtables.tier");

		/* KieSessionModel kieSessionModel1 = */ kieBaseModel1.newKieSessionModel("ksession-process-refamt");
		/* KieSessionModel kieSessionModel2 = */ kieBaseModel2.newKieSessionModel("ksession-process-fop");
		/* KieSessionModel kieSessionModel3 = */ kieBaseModel3.newKieSessionModel("ksession-process-shortcut");
		/* KieSessionModel kieSessionModel4 = */ kieBaseModel4.newKieSessionModel("ksession-process-thresholds");
		/* KieSessionModel kieSessionModel5 = */ kieBaseModel5.newKieSessionModel("ksession-process-init");
		/* KieSessionModel kieSessionModel6 = */ kieBaseModel6.newKieSessionModel("ksession-process-tier");

		KieFileSystem kfs                = ks.newKieFileSystem();
		kfs.writeKModuleXML(kieModuleModel.toXML());
		this.releaseId = ks.newKieBuilder(kfs).buildAll().getKieModule().getReleaseId();

		logger.error("[01;03;31m" + "\n" + (kieModuleModel.toXML()) + "[00;00m");

		// KieContainer kContainer = ks.getKieClasspathContainer();
		KieContainer kContainer = ks.newKieContainer(this.releaseId);
		String pattern          = "(\\S+)-\\w+";

		logger.error("[01;03;31m" + "list out the complete set of rulesets:\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(kContainer.getKieBaseNames()) + "[00;00m");

		for (String kieBaseName : kContainer.getKieBaseNames()) {
			logger.error("[01;03;31m\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(kContainer.getKieSessionNamesInKieBase(kieBaseName)) + "[00;00m");
			for (String session : kContainer.getKieSessionNamesInKieBase(kieBaseName)) {
				String rule = session.replaceAll(pattern, "$1");
				rules.add(rule);
			}
			break;
		}

		logger.error("[01;03;31m" + "distinct rules:\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(rules) + "[00;00m");

		// Check to see if rulesets' identifiers exist in MongoDB and overwrite them if they don't

		MongoCursor<Document> cursorDocMap = null;
		List<String> ids = new ArrayList<String>();

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
							currentDate("modifiedDate"))
						);
				ids.add(rules.indexOf(rule), id.toString());
			} else {
				ids.add(rules.indexOf(rule), rule);
			}
		}

		logger.error("[01;03;31m\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(ids) + "[00;00m");
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
		// String ruleset = dataset.getRuleset();
		String ruleset = "ksession-process";

		/**
		 * TODO: make the ruleset ID part of the Dataset and pass in the ruleset ID
		 */
		foods = FlagsEngine      .flagsEngine      .setReleaseIdAndRuleset(this.releaseId, ruleset) .setFlags (foods); // Step 1: RA Adjustment
		foods = InitEngine       .initEngine       .setReleaseIdAndRuleset(this.releaseId, ruleset) .setInit  (foods); // Step 2: Threshold Rule
		foods = AdjustmentEngine .adjustmentEngine .setReleaseIdAndRuleset(this.releaseId, ruleset) .adjust   (foods); // Step 3: Adjustments
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

	@DELETE
	@Path("/rulesets")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public /* Response */ Map<String, String> deleteAllRulesets() {
		collection.deleteMany(new Document());

		mongoClient.close();

		Map<String, String> msg = new HashMap<String, String>();
		msg.put("message", "Successfully deleted all datasets");

		return msg;
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
