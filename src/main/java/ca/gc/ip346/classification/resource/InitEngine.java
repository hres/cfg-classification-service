package ca.gc.ip346.classification.resource;

import static org.apache.logging.log4j.Level.DEBUG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class InitEngine {
	private static final Logger logger = LogManager.getLogger(InitEngine.class);

	public static InitEngine initEngine = new InitEngine();
	/* The pipeline is used to rule the drools rules one at a time */
	private List<KieSession> kieSessionPipeline;
	private List<CanadaFoodGuideDataset> foodResults;
	static Map<String, KieBase> kieBaseCache =  new HashMap<>();

	/* recreates singleton */
	public static void refreshEngine() {
		initEngine = new InitEngine();
	}

	public List<CanadaFoodGuideDataset> setInit(List<CanadaFoodGuideDataset> foods) {
		foodResults = new ArrayList<>();
		fireDrools(foods);
		return foodResults;
	}

	public InitEngine() {
	}

	/**
	 * @param releaseId the releaseId to use
	 * @param ruleset the ruleset to use
	 * @return this instance
	 */
	public InitEngine setReleaseIdAndRuleset(String rulePath, String ruleset) {
		kieSessionPipeline      = new ArrayList<KieSession>();
	
		kieSessionPipeline.add(getKieSession(rulePath, ruleset, ClassificationResource.FoodRuleNames.FOP.ruleName()));
		kieSessionPipeline.add(getKieSession(rulePath, ruleset, ClassificationResource.FoodRuleNames.SHORTCUT.ruleName()));
		kieSessionPipeline.add(getKieSession(rulePath, ruleset, ClassificationResource.FoodRuleNames.THRESHOLDS.ruleName()));
		kieSessionPipeline.add(getKieSession(rulePath, ruleset, ClassificationResource.FoodRuleNames.INIT.ruleName()));

		return this;
	}

	/*
	 * Function: fireDrools
	 *  Purpose: fire the drools rules and calculate
	 *       in: foods
	 */
	private void fireDrools(List<CanadaFoodGuideDataset> foods) {
		KieSession kieSession = null;
		for (CanadaFoodGuideDataset food : foods) {

			prepare(food);

			for (int i = 0; i < kieSessionPipeline.size(); i++) {
				if (!food.isDone()) {
					kieSessionPipeline.get(i).insert(food);
					
					kieSession = kieSessionPipeline.get(i);
					
					int ruleFiredCount = kieSession.fireAllRules();
					
					System.out.println("Initila.........触发了" + ruleFiredCount + "条规则");

					logger.debug("[01;03;31m" + kieSessionPipeline.size() + "[00;00m");
				}
			}
			
			logger.debug("[01;03;31m" + "Final==========CFG code: " + food.getCfgCode() + " ====tier: " + food.getTier() + "[00;00m");
			String firstThreeDigits = food.getCfgCode() + "";
			food.setInitialCfgCode(Integer.parseInt(firstThreeDigits.substring(0, 3) + food.getTier()));
			logger.debug("[01;03;31m" + "Fianl=======initial CFG code: " + food.getInitialCfgCode() + "[00;00m");

			foodResults.add(food);
		}
	}
	
	private KieSession getKieSession(String rulePath, String ruleSetId, String name) {
		
		KieSession kieSession = null;
		String kieSessionName = "ksession-process-" + ruleSetId + "-" + name;
		if (kieBaseCache.get(kieSessionName) == null) {
	
			KieServices ks          = KieServices.Factory.get();
			String inMemoryFileName = rulePath + name + "/" + ruleSetId + "/" + name + ruleSetId + ".xls";
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
			
			//get rule
			logger.debug("Get Rule : " + kbase.getKiePackages().size());
	        for ( KiePackage kp : kbase.getKiePackages() ) {
	            for (Rule rule : kp.getRules()) {
	            	logger.debug("Get Rule From InitEngine App==================" + kp + " rule name: " + rule.getName());
	            }
	        }
			kieSession = kbase.newKieSession();
			System.out.println("Init Put rules KieBase into Custom Cache============");
			kieBaseCache.put(kieSessionName, kbase);
		}else {
			System.out.println("Get existing rules KieBase from Init===================Custom Cache");
			kieSession = kieBaseCache.get(kieSessionName).newKieSession();
		}
		return kieSession;
		
	}

	private void prepare(CanadaFoodGuideDataset food) {

		food.setLowSodium        (false);
		food.setHighSodium       (false);

		food.setLowSugar         (false);
		food.setHighSugar        (false);

		food.setLowFat           (false);
		food.setHighFat          (false);

		food.setLowSatFat        (false);
		food.setHighSatFat       (false);

		food.setLowTransFat      (false);
		food.setHighTransFat     (false);

		food.setSodiumFopWarning (false);
		food.setSugarFopWarning  (false);
		food.setSatFatFopWarning (false);

		food.setTier             (4);
		food.setSugarDV          (15.0);
		food.setSodiumDV         (15.0);
		food.setSatFatDV         (15.0);
	}
}
