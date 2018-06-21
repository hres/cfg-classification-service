package ca.gc.ip346.classification.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.Level.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
// import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.google.gson.GsonBuilder;

import ca.gc.ip346.classification.model.CanadaFoodGuideDataset;

public class FlagsEngine {
	private static final Logger logger = LogManager.getLogger(FlagsEngine.class);

	public static FlagsEngine flagsEngine = new FlagsEngine();
	/* The pipeline is used to rule the drools rules one at a time */
	private List<KieSession> kieSessionPipeline;
	private List<CanadaFoodGuideDataset> foodResults;
	
	static Map<String, KieBase> kieBaseCache =  new HashMap<>();

	/* recreates singleton */
	public static void refreshEngine() {
		flagsEngine = new FlagsEngine();
	}

	public List<CanadaFoodGuideDataset> setFlags(List<CanadaFoodGuideDataset> foods) {
		foodResults = new ArrayList<>();
		fireDrools(foods);
		return foodResults;
	}

	public FlagsEngine() {
	}

	/**
	 * @param releaseId the releaseId to use
	 * @param ruleset the ruleset to use
	 * @return this instance
	 */
	public FlagsEngine setReleaseIdAndRuleset(String rulePath, String ruleset) {
		
		kieSessionPipeline      = new ArrayList<KieSession>();
		kieSessionPipeline.add(getKieSession(rulePath, ruleset, ClassificationResource.FoodRuleNames.REFAMT.ruleName()));

		return this;
	}

	/*
	 * Function: fireDrools
	 *  Purpose: fire the drools rules and calculate
	 *       in: foods
	 */
	private void fireDrools(List<CanadaFoodGuideDataset> foods) {
		for (CanadaFoodGuideDataset food : foods) {
			food.setClassifiedCfgCode(food.getCfgCode());
			prepare(food);
			// boolean setRA = false;
			for (int i = 0; i < kieSessionPipeline.size(); i++) {
				if (!food.isDone()) {
// logger.debug("[01;03;31m" + "HERE: \n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(food) + "[00;00m");
					
					kieSessionPipeline.get(i).insert(food);
					kieSessionPipeline.get(i).fireAllRules();
					
					/* only call this after adjustedRA is set */
					calculatePerRA(food);
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "how many rulesets: "                   , kieSessionPipeline.size()           , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getReferenceAmountG(): "          , food.getReferenceAmountG()          , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getOverrideSmallRaAdjustment(): " , food.getOverrideSmallRaAdjustment() , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getAdjustedReferenceAmount(): "   , food.getAdjustedReferenceAmount()   , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getFopAdjustedReferenceAmount(): ", food.getFopAdjustedReferenceAmount(), "[00;00m");
				}
			}
			logger.debug("[01;03;31m" + "firing Drools" + "[00;00m");
			foodResults.add(food);
		}
	}
	
	private KieSession getKieSession(String rulePath, String ruleSetId, String name) {
		
		KieSession kieSession = null;
		String kieSessionName = "ksession-process-" + ruleSetId + "-" + name;
		if (kieBaseCache.get(kieSessionName) == null) {
	
			KieServices ks          = KieServices.Factory.get();
			String inMemoryFileName = rulePath  + name + "/" + ruleSetId + "/" + name + ruleSetId + ".xls";
			KieFileSystem kfs = ks.newKieFileSystem();
			kfs.write(ks.getResources().newFileSystemResource(inMemoryFileName)
					.setResourceType(ResourceType.DTABLE));
			
			KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
			
			if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
				System.out.println(kieBuilder.getResults().toString());
			}
			KieContainer kContainer = ks.newKieContainer(kieBuilder.getKieModule().getReleaseId());
			//KieBaseConfiguration kbconf = ks.newKieBaseConfiguration();
			//KieSession kSession = kContainer.getKieSession(kbconf);
			KieBase kbase = kContainer.getKieBase();
			
			kieSession = kbase.newKieSession();
			System.out.println("Flags Put rules KieBase into Custom Cache=============");
			kieBaseCache.put(kieSessionName, kbase);
		}else {
			System.out.println("Get existing rules KieBase from Init=================Custom Cache");
			kieSession = kieBaseCache.get(kieSessionName).newKieSession();
		}
		return kieSession;
		
	}

	private void prepare(CanadaFoodGuideDataset food) {

		/* Removed due to removing flags table execution */

		// food.setContainsAddedFat      (false);
		// food.setContainsAddedSodium   (false);
		// food.setContainsAddedSugar    (false);
		// food.setContainsAddedTransfat (false);
		// food.setContainsCaffeine      (false);

		/* This is done as the FIRST prepare ONLY */
		food.setAdjustedReferenceAmount(food.getReferenceAmountG());
		food.setFopAdjustedReferenceAmount(food.getReferenceAmountG());
		calculatePerRA(food);
		food.setDone(false);
		food.setAbsolute(false);
		food.setShift(0);
	}

	private void calculatePerRA(CanadaFoodGuideDataset food) {
		food    .setSodiumPerReferenceAmount(food   .getSodiumAmountPer100g() * food    .getAdjustedReferenceAmount() / 100);
		food     .setSugarPerReferenceAmount(food    .getSugarAmountPer100g() * food    .getAdjustedReferenceAmount() / 100);
		food       .setFatPerReferenceAmount(food .getTotalFatAmountPer100g() * food    .getAdjustedReferenceAmount() / 100);
		food    .setSatFatPerReferenceAmount(food   .getSatfatAmountPer100g() * food    .getAdjustedReferenceAmount() / 100);
		food  .setTransFatPerReferenceAmount(food .getTransfatAmountPer100g() * food    .getAdjustedReferenceAmount() / 100);
		food .setFopSodiumPerReferenceAmount(food   .getSodiumAmountPer100g() * food .getFopAdjustedReferenceAmount() / 100);
		food  .setFopSugarPerReferenceAmount(food    .getSugarAmountPer100g() * food .getFopAdjustedReferenceAmount() / 100);
	}
}
