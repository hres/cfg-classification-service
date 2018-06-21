package ca.gc.ip346.classification.resource;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.currentDate;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.apache.logging.log4j.Level.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import ca.gc.ip346.classification.model.CanadaFoodGuideDataset;
import ca.gc.ip346.classification.model.Dataset;
import ca.gc.ip346.classification.model.Ruleset;
import ca.gc.ip346.util.MongoClientFactory;
import ca.gc.ip346.util.RuleSets;

@Path("/")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ClassificationResource {
	
	private static final Logger logger           = LogManager.getLogger(ClassificationResource.class);
	private List<String> rules                   = null;
	
	private MongoClient mongoClient              = null;
	private MongoCollection<Document> collection = null;
	private MongoCollection<Document> slots      = null;
	
	private Integer productionRulesetId          = null;
	
	public enum FoodRuleNames{
		  REFAMT("refamt"), 
		  FOP("fop"), 
		  SHORTCUT("shortcut"), 
		  THRESHOLDS("thresholds"),
		  INIT("init"),
		  TIER("tier");
		
		private String ruleName;

	    FoodRuleNames(String ruleName) {
	        this.ruleName = ruleName;
	    }

	    public String ruleName() {
	        return ruleName;
	    }
		  
	};
	
	
	/**
	 *
	 * Obtain the complete set of rulesets from kmodule.xml - a list of identifiers: one per ruleset
	 * Check to see if rulesets' identifiers exist in MongoDB and overwrite them if they don't
	 * TODO: implement GridFS when connecting to MongoDB
	 *
	 */
	public ClassificationResource() {
		
		mongoClient = MongoClientFactory.getMongoClient();
		collection  = mongoClient.getDatabase(MongoClientFactory.getDatabase()).getCollection(MongoClientFactory.getCollection());
		slots       = mongoClient.getDatabase(MongoClientFactory.getDatabase()).getCollection(MongoClientFactory.getAnotherCollection());

		MongoCursor<Document> cursorDocMap = slots.find(new Document("isProd", true).append("active", true)).iterator();
		
		rules = Arrays.asList(new String[]{"1","2","3","4","5","6","7","8",
				"9","10","11","12","13","14","15","16"
				});
		rules	=	new ArrayList<>(rules);
		
		
		if (slots.count() == 0) {
			logger.printf(DEBUG, "%s%s%s", "[01;03;35m", "CREATE ALL 16 SLOTS HERE!", "[00;00m");
			for (String rule : rules) {
				Document doc = new Document()
					.append("name", "Ruleset " + rule)
					.append("rulesetId", Integer.valueOf(rule))
					.append("isProd", rule.equals("1") ? true : false)
					.append("active", rule.equals("1") ? true : false);
				ObjectId id = (ObjectId)doc.get("_id");
				collection.updateOne(
						eq("_id", id),
						combine(
							set("isProd", rule.equals("1") ? true : false),
							set("active", rule.equals("1") ? true : false),
							currentDate("modifiedDate"))
						);
				slots.insertOne(doc);
			}
		} else {
			cursorDocMap = slots.find().iterator();
		}
	}

	@POST
	@Path("/classify/{rulesetId}")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// @JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> classifyDataset(@PathParam("rulesetId") Integer rulesetId, Dataset dataset) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		
		logger.error("current RuleSet Id: ================" + rulesetId);
		
		if (rulesetId == 0) {
			rulesetId = getProductionRulesetId();
			logger.error("get Production RuleSet Id: ================" + rulesetId);
		}
		
		if (rulesetId == null)
			logger.error("Mogondb get product Ruleset Id null");

		foods = FlagsEngine      .flagsEngine      .setReleaseIdAndRuleset(getRuleFilePath(), rulesetId.toString()) .setFlags (foods); // Step 1: RA Adjustment
		foods = InitEngine       .initEngine       .setReleaseIdAndRuleset(getRuleFilePath(), rulesetId.toString()) .setInit  (foods); // Step 2: Threshold Rule
		foods = AdjustmentEngine .adjustmentEngine .setReleaseIdAndRuleset(getRuleFilePath(), rulesetId.toString()) .adjust   (foods); // Step 3: Adjustments
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
	// @JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> getRulesets() {
		Map<String, Object> result         = new HashMap<String, Object>();
		List<Map<String, Object>> rulesets = new ArrayList<Map<String, Object>>();
		MongoCursor<Document> cursorDocMap = null;
		for (String rule : rules) {
			Map<String, Object> map = new HashMap<String, Object>();
			cursorDocMap            = slots.find(new Document("rulesetId", Integer.valueOf(rule)).append("active", true)).iterator();
			while (cursorDocMap.hasNext()) {
				Document doc      = cursorDocMap.next();
				ObjectId id       = (ObjectId)doc.get("_id");
				String name       = (String)doc.get("name");
				Integer rulesetId = (Integer)doc.get("rulesetId");
				Boolean isProd    = (Boolean)doc.get("isProd");
				Boolean active    = (Boolean)doc.get("active");
				map.put("id", id.toString());
				map.put("name", name);
				map.put("rulesetId", rulesetId);
				map.put("isProd", isProd);
				map.put("active", active);
				rulesets.add(map);
			}
		}
		result.put("rulesets", rulesets);

		mongoClient.close();

		return result;
	}

	@GET
	@Path("/rulesets/{id}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// @JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> getRuleset(@PathParam("id") String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		MongoCursor<Document> cursorDocMap = slots.find(new Document("rulesetId", Integer.valueOf(id))).iterator();
		while (cursorDocMap.hasNext()) {
			Document doc      = cursorDocMap.next();
			ObjectId identity = (ObjectId)doc.get("_id");
			String name       = (String)doc.get("name");
			Integer rulesetId = (Integer)doc.get("rulesetId");
			Boolean isProd    = (Boolean)doc.get("isProd");
			Boolean active    = (Boolean)doc.get("active");

			map.put("id", identity.toString());
			map.put("name", name);
			map.put("rulesetId", rulesetId);
			map.put("isProd", isProd);
			map.put("active", active);
		}

		mongoClient.close();

		return map;
	}

	@POST
	@Path("/rulesets")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// @JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> createRuleset(Ruleset ruleset) {
		Map<String, Object> msg = new HashMap<String, Object>();
		MongoCursor<Document> cursorDocMap = slots.find(new Document("rulesetId", ruleset.getRulesetId()).append("active", false).append("isProd", false)).iterator();
		List<Bson> firstLevelSets = new ArrayList<Bson>();
		if (!cursorDocMap.hasNext()) {
			msg.put("message", "Failed to create ruleset!");
		} else {
			firstLevelSets.add(set("name",   ruleset.getName()));
			firstLevelSets.add(set("active", ruleset.isActive()));
			firstLevelSets.add(set("isProd", ruleset.getIsProd()));
			slots.updateOne(eq("rulesetId",  ruleset.getRulesetId()), combine(firstLevelSets));

			msg.put("message", "Successfully created ruleset with ID: " + ruleset.getRulesetId().toString());
		}
		return msg;
	}

	@PUT
	@Path("/rulesets/{id}")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// @JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> updateRuleset(@PathParam("id") String id, Ruleset ruleset) {
		Map<String, Object> msg = new HashMap<String, Object>();
		MongoCursor<Document> cursorDocMap = slots.find(new Document("rulesetId", Integer.valueOf(id)).append("active", true)).iterator();
		List<Bson> firstLevelSets = new ArrayList<Bson>();
		int changes = 0;

		if (!cursorDocMap.hasNext()) {
			msg.put("message", "Failed to update ruleset with id: " + id);
		} else {
			Document doc = cursorDocMap.next();
			if (ruleset.getName() != null && !ruleset.getName().equals(doc.get("name"))) {
				firstLevelSets.add(set("name", ruleset.getName()));
				++changes;
			}
			if (ruleset.getIsProd() != null) {
				firstLevelSets.add(set("isProd", true));
				slots.updateOne(and(eq("active", true), eq("isProd", true)), set("isProd", false)); // reset existing default ruleset
				++changes;
			}

			if (changes != 0) {
				// TODO: firstLevelSets.add(currentDate("modifiedDate"));
				slots.updateOne(eq("rulesetId", Integer.valueOf(id)), combine(firstLevelSets));
			}
			msg.put("message", "Successfully updated ruleset with id: " + id);
		}

		return msg;
	}

	@DELETE
	@Path("/rulesets/{id}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// @JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public /* Response */ Map<String, Object> deleteRuleset(@PathParam("id") String id) {
		Map<String, Object> msg = new HashMap<String, Object>();
		MongoCursor<Document> cursorDocMap = slots.find(new Document("rulesetId", Integer.valueOf(id)).append("isProd", false).append("active", true)).iterator();
		List<Bson> firstLevelSets = new ArrayList<Bson>();

		if (!cursorDocMap.hasNext()) {
			msg.put("message", "Failed to delete ruleset with id: " + id);
		} else {
			firstLevelSets.add(set("active", false));
			slots.updateOne(eq("rulesetId", Integer.valueOf(id)), combine(firstLevelSets));
			msg.put("message", "Successfully deleted ruleset with id: " + id);
		}

		mongoClient.close();

		return msg;
	}

	@DELETE
	@Path("/rulesets")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// @JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public /* Response */ Map<String, String> deleteAllRulesets() {
		slots.deleteMany(new Document());

		mongoClient.close();

		Map<String, String> msg = new HashMap<String, String>();
		msg.put("message", "Successfully deleted all datasets");

		return msg;
	}

	@GET
	@Path("/rulesetshome")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// @JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, String> getRulesetsHome() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("rulesetshome", RuleSets.getHome());
		return map;
	}

	@GET
	@Path("/slot")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// @JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Integer> getAvailableSlot() {
		Integer slot                       = null;
		Map<String, Integer> map           = new HashMap<String, Integer>();
		MongoCursor<Document> cursorDocMap = null;
		for (String rule : rules) {
			cursorDocMap = slots.find(new Document("rulesetId", Integer.valueOf(rule)).append("active", false)).iterator();
			while (cursorDocMap.hasNext()) {
				Document doc = cursorDocMap.next();
				slot         = (Integer)doc.get("rulesetId");
				break;
			}
			if (slot != null) {
				break;
			}
		}
		map.put("slot", slot);

		mongoClient.close();

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
		map.put("msg", "new mongo connectivity test: " + mongoClient.getDatabase(MongoClientFactory.getDatabase()).runCommand(new Document("buildInfo", 1)).getString("version"));
		logger.debug("[01;03;31m" + "new mongo connectivity test: " + mongoClient.getAddress() + "[00;00m");
		logger.debug("[01;03;31m" + "new mongo connectivity test: " + mongoClient.getConnectPoint() + "[00;00m");
		logger.debug("[01;03;31m" + "new mongo connectivity test: " + mongoClient.getDatabase(MongoClientFactory.getDatabase()).runCommand(new Document("buildInfo", 1)).getString("version") + "[00;00m");
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

	private Integer getProductionRulesetId() {
		MongoCursor<Document> cursorDocMap = slots.find(new Document("isProd", true)).iterator();
		while (cursorDocMap.hasNext()) {
			Document doc = cursorDocMap.next();
			logger.error("current RuleSet Id: ================" + doc.getInteger("rulesetId"));
			logger.error("current RuleSet Name: ================" + doc.getString("name"));
			return doc.getInteger("rulesetId");
		}
		return null;
	}
	
	private String getRuleFilePath() {
		
		String ruleFilePath = null;
		try {
			InitialContext context = new InitialContext();
			Context xmlNode = (Context) context.lookup("java:comp/env");
			ruleFilePath = (String) xmlNode.lookup("ruleFilePath");
			
		}catch(Exception ex) {
			logger.error("Can't find ruleFilePath..." + ex);
		}
		return ruleFilePath;
	}
	
	
}
