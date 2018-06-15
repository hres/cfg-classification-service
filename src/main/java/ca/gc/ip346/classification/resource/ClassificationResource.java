package ca.gc.ip346.classification.resource;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.currentDate;
import static com.mongodb.client.model.Updates.set;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

import static org.kie.api.conf.DeclarativeAgendaOption.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

// import com.fasterxml.jackson.databind.SerializationFeature;
// import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.google.gson.GsonBuilder;
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
	private List<String> sessions                = null;
	private MongoClient mongoClient              = null;
	private MongoCollection<Document> collection = null;
	private MongoCollection<Document> slots      = null;
	private ReleaseId releaseId                  = null;
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

		logger.debug("[01;03;31m" + "collection: "                  + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(collection.count())                   + "[00;00m");
		logger.debug("[01;03;31m" + "slots: "                       + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(slots.count())                        + "[00;00m");
		logger.debug("[01;03;31m" + "new mongo connectivity test: " + mongoClient.getDatabase(MongoClientFactory.getDatabase()).runCommand(new Document("buildInfo", 1)).getString("version") + "[00;00m");

		MongoCursor<Document> cursorDocMap = slots.find(new Document("isProd", true).append("active", true)).iterator();
		while (cursorDocMap.hasNext()) {
			Document doc      = cursorDocMap.next();
			productionRulesetId = (Integer)doc.get("rulesetId");
			logger.debug("[01;03;31m" + "ruleset:\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(doc) + "[00;00m");
		}
		
		rules                         = new ArrayList<String>();
		sessions                      = new ArrayList<String>();
		List<String> rools            = new ArrayList<String>(Arrays.asList("refamt", "fop", "shortcut", "thresholds", "init", "tier"));
		KieServices ks                = KieServices.Factory.get();
		KieModuleModel kieModuleModel = ks.newKieModuleModel();
		
		List<KieBaseModel> arr = new ArrayList<KieBaseModel>();
		List<KieModuleModel> arrKieModuleModel = new ArrayList<KieModuleModel>();
		KieFileSystem kfs = ks.newKieFileSystem();
		//ini
		int ruleSetId = 0;
		String baseName = null;
		String packageName = null;
		String sessionName = null;
		
		try {
			String file = null;
			String pName = null;
			for (String rule : rools) {
				for (int i = 0; i < 16; ++i) {
					file = "/opt/ruleset/cfg-classification-rulesets/rulesets/dtables/" + rule + "/" + (i + 1) + "/" + rule + (i + 1) + ".xls";
					pName = "src/main/resources/dtables/" + rule + "/" + (i + 1) + "/" + rule + (i + 1) + ".xls";;
					kfs.write(ks.getResources().newFileSystemResource(file)
							.setResourceType(ResourceType.DTABLE));
				}
			}
	
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
		}
		
		for (int i = 0, ruleId = i; i < 16; ++i) {
			ruleSetId = i + 1;
			for (String rule : rools) {
				baseName = "dtables." + rule + "." + ruleSetId;
				packageName = "dtables.tier";
				sessionName = "ksession-process-" + ruleSetId + "-" + rule;
				
				KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(baseName).addPackage(packageName).setDeclarativeAgenda(ENABLED);
				
				if (productionRulesetId.equals(ruleSetId)) {
					kieBaseModel.setDefault(true);
				}
				arr .add(kieBaseModel);
				arr .get(ruleId++) .newKieSessionModel(sessionName) .setDefault(productionRulesetId.equals(ruleSetId)) .setType(KieSessionModel.KieSessionType.STATEFUL) ;
			}
		}

		kfs.writeKModuleXML(kieModuleModel.toXML());
		
		// ks.newKieBuilder(kfs).buildAll();
		// this.releaseId = ks.getRepository().getDefaultReleaseId();
		//KieBuilder builder = ks.newKieBuilder(kfs).buildAll();
		KieBuilder builder = ks.newKieBuilder(kfs);
		
		Results results = builder.getResults();
		
		if( results.hasMessages( Message.Level.INFO ) ){
		    System.out.println( results.getMessages() );
		        //throw new IllegalStateException( "### errors ###" );
		}
		builder.buildAll();
		this.releaseId = builder.getKieModule().getReleaseId();
		//this.releaseId = ks.getRepository().getDefaultReleaseId();

		//logger.debug("[01;03;31m" + "dynamically built kmodule:\n" + (kieModuleModel.toXML()) + "[00;00m");
		// KieContainer kContainer = ks.getKieClasspathContainer(); // returns KieContainer for the classpath, this a global singleton
		KieContainer kContainer = ks.newKieContainer(this.releaseId);
		
		String pattern          = "ksession-process-(\\S+)-\\w+";

		//logger.debug("[01;03;31m" + "complete set of dynamically built rulesets:\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(kContainer.getKieBaseNames())                    + "[00;00m");
		// creates a new KieContainer for the classpath, regardless if there's already an existing one: ks.newKieClasspathContainer() versus ks.getKieClasspathContainer()
		//logger.debug("[01;03;31m" + "complete set of dynamically built rulesets:\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(ks.newKieClasspathContainer().getKieBaseNames()) + "[00;00m");

		/**
		 * six rulesets get created - this is arbitrary - six if using dynamically created KieModuleModel, not the classpath
		 * sixteen rulesets get created - this is arbitrary - sixteen if using the classpath
		 */
		for (String kieBaseName : kContainer.getKieBaseNames()) {
			for (String session : kContainer.getKieSessionNamesInKieBase(kieBaseName)) {
				sessions.add(session);
				//logger.debug("[01;03;31m" + "session to string:\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(session.toString())        + "[00;00m");
				String rule = session.replaceAll(pattern, "$1");
				logger.debug("========rule name......[01;03;31m" + "session.replaceAll():\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(rule)        + "[00;00m");
				rules.add(rule);
			}
			// break;
		}

		Comparator<String> cmp = new Comparator<String>() {
			public int compare(String o1, String o2) {
				return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
			}
		};

		Collections.sort(rules, cmp);
		//logger.debug("[01;03;31m" + "multiple rulesets:\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(rules) + "[00;00m");

		Set<String> distinctRules = new HashSet<String>(rules);
		rules                     = new ArrayList<String>(distinctRules);
		Collections.sort(rules, cmp);

		cmp = new Comparator<String>() {
			public int compare(String o1, String o2) {
				return Integer.valueOf(o1.replaceAll("ksession-process-", "").replaceAll("-\\w+", "")).compareTo(Integer.valueOf(o2.replaceAll("ksession-process-", "").replaceAll("-\\w+", "")));
			}
		};

		Collections.sort(sessions, cmp);

		//logger.debug("[01;03;33m" + "ruleset count: "      + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(rules.size())    + "[00;00m");
		//logger.debug("[01;03;31m" + "distinct rulesets:\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(rules)           + "[00;00m");
		//logger.debug("[01;03;31m" + "session count: "      + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(sessions.size()) + "[00;00m");
		//logger.debug("[01;03;31m" + "distinct sessions:\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(sessions)        + "[00;00m");

		// Check to see if rulesets' identifiers exist in MongoDB and create them if they don't

		/* MongoCursor<Document> */ cursorDocMap = null;
		// List<String> ids = new ArrayList<String>();

		/**
		 * kmodule provides a list of valid ruleset id's
		 * check to see if any of these ruleset id's exist in the local mongo
		 */
		/*
		if (collection.count() == 0) {
			Integer ruleSetCounter = 0;
			for (String rule : rules) {
				Boolean rulesetIdDoesNotExist = true;

				if (ObjectId.isValid(rule)) {
					logger.debug("[01;31m" + "Valid hexadecimal representation of RulesetId (rule) " + rule + "[00;00m");

					cursorDocMap = collection.find(new Document("_id", new ObjectId(rule))).iterator();
					if (cursorDocMap.hasNext()) {
						// always the case - so create all arbitrary rulesets
						// create new item and replace current rule value with new _id
						rulesetIdDoesNotExist = false;
					}
				}

				if (rulesetIdDoesNotExist) {
					++ruleSetCounter;
					logger.debug("[01;03;31m" + ruleSetCounter + "[00;00m");
					Document doc = new Document()
						.append("name",     "Ruleset " + ruleSetCounter.toString() + "")
						.append("isProd",   ruleSetCounter == 1 ? true : false)
						.append("location", null);
					collection.insertOne(doc);
					ObjectId id = (ObjectId)doc.get("_id");
					collection.updateOne(
							eq("_id", id),
							combine(
								set("isProd",   ruleSetCounter == 1 ? true : false),
								currentDate("modifiedDate"))
							);
					ids.add(rules.indexOf(rule), id.toString());
				} else {
					ids.add(rules.indexOf(rule), rule); // never happens?
				}
			}
		} else {
			cursorDocMap = collection.find().iterator();
			while (cursorDocMap.hasNext()) {
				Document doc = cursorDocMap.next();
				ObjectId id = (ObjectId)doc.get("_id");
				ids.add(id.toString());
				logger.debug("[01;31m" + "Valid hexadecimal representation of RulesetId (id) " + id + "[00;00m");
			}
		}
		 */

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

		// logger.debug("[01;03;33m" + "ruleset identifier(s) from mongodb:\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(ids) + "[00;00m");

		// // List<String> categories = new ArrayList<String>(Arrays.asList("refamt", "fop", "shortcut", "thresholds", "init", "tier"));
		// List<String> categories = new ArrayList<String>();
		// categories.add("refamt");
		// categories.add("fop");
		// categories.add("shortcut");
		// categories.add("thresholds");
		// categories.add("init");
		// categories.add("tier");

		// kieBaseModel1.removeKieSessionModel("ksession-process-refamt");     // not necessary since kieModuleModel is overwritten
		// kieBaseModel2.removeKieSessionModel("ksession-process-fop");        // not necessary since kieModuleModel is overwritten
		// kieBaseModel3.removeKieSessionModel("ksession-process-shortcut");   // not necessary since kieModuleModel is overwritten
		// kieBaseModel4.removeKieSessionModel("ksession-process-thresholds"); // not necessary since kieModuleModel is overwritten
		// kieBaseModel5.removeKieSessionModel("ksession-process-init");       // not necessary since kieModuleModel is overwritten
		// kieBaseModel6.removeKieSessionModel("ksession-process-tier");       // not necessary since kieModuleModel is overwritten

		// ks             = KieServices.Factory.get();
		// kieModuleModel = ks.newKieModuleModel(); // overwrites previous kieModuleModel

		// kieBaseModel1 = kieModuleModel.newKieBaseModel("dtables.refamt")     .addPackage("dtables.refamt")     .setDeclarativeAgenda(ENABLED) .setDefault(true);
		// kieBaseModel2 = kieModuleModel.newKieBaseModel("dtables.fop")        .addPackage("dtables.fop")        .setDeclarativeAgenda(ENABLED) .setDefault(true);
		// kieBaseModel3 = kieModuleModel.newKieBaseModel("dtables.shortcut")   .addPackage("dtables.shortcut")   .setDeclarativeAgenda(ENABLED) .setDefault(true);
		// kieBaseModel4 = kieModuleModel.newKieBaseModel("dtables.thresholds") .addPackage("dtables.thresholds") .setDeclarativeAgenda(ENABLED) .setDefault(true);
		// kieBaseModel5 = kieModuleModel.newKieBaseModel("dtables.init")       .addPackage("dtables.init")       .setDeclarativeAgenda(ENABLED) .setDefault(true);
		// kieBaseModel6 = kieModuleModel.newKieBaseModel("dtables.tier")       .addPackage("dtables.tier")       .setDeclarativeAgenda(ENABLED) .setDefault(true);

		// List<KieBaseModel> bases = new ArrayList<KieBaseModel>();

		// bases.add(kieBaseModel1); // dtables.refamt
		// bases.add(kieBaseModel2); // dtables.fop
		// bases.add(kieBaseModel3); // dtables.shortcut
		// bases.add(kieBaseModel4); // dtables.thresholds
		// bases.add(kieBaseModel5); // dtables.init
		// bases.add(kieBaseModel6); // dtables.tier

		/*
		for (int j = 0; j < categories.size(); ++j) {
			for (int i = 0; i < ids.size(); ++i) {
				logger.debug("ksession-process-" + ids.get(i) + "-" + categories.get(j));
				KieSessionModel kieSessionModel = bases.get(j).newKieSessionModel("ksession-process-" + ids.get(i) + "-" + categories.get(j));
				if (i == 0) kieSessionModel.setDefault(true);
			}
		}
		 */

		// kfs = ks.newKieFileSystem();
		// kfs.writeKModuleXML(kieModuleModel.toXML());
		// ks.newKieBuilder(kfs).buildAll();
		// this.releaseId = ks.getRepository().getDefaultReleaseId();
		// this.releaseId = ks.newKieBuilder(kfs).buildAll().getKieModule().getReleaseId();

		// logger.debug("[01;03;31m" + "\n" + (kieModuleModel.toXML()) + "[00;00m");

		logger.debug("[01;03;33m" + "release ID: " + this.releaseId + "[00;00m");

		// this.rules = ids;
	}

	@POST
	@Path("/classify/{rulesetId}")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// @JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> classifyDataset(@PathParam("rulesetId") Integer rulesetId, Dataset dataset) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		logger.printf(DEBUG, "%s%22s%d%s", "[01;03;35m", "=============passed-in ruleset id: ", rulesetId, "[00;00m");
		logger.printf(DEBUG, "%s%22s%d%s", "[01;03;35m", "==========food.cfgcode ", foods.get(0).getCfgCode(), "[00;00m");
		logger.printf(DEBUG, "%s%22s%d%s", "[01;03;35m", "===========food.cfgtire ", foods.get(0).getTier(), "[00;00m");
		// logger.debug("[01;03;35m" + "dataset passed in:\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(dataset) + "[00;00m");

		if (rulesetId == 0) {
			rulesetId = getProductionRulesetId();
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
