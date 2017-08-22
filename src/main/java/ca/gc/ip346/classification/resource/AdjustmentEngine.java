package ca.gc.ip346.classification.resource;

import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.Level.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
// import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.google.gson.GsonBuilder;

import ca.gc.ip346.classification.model.CanadaFoodGuideDataset;

public class AdjustmentEngine {
	private static final Logger logger = LogManager.getLogger(AdjustmentEngine.class);

	public static AdjustmentEngine adjustmentEngine = new AdjustmentEngine();
	/* The pipeline is used to rule the drools rules one at a time */
	private List<KieSession> kieSessionPipeline;
	private List<CanadaFoodGuideDataset> foodResults;

	/* recreates singleton */
	public static void refreshEngine() {
		adjustmentEngine = new AdjustmentEngine();
	}

	public List<CanadaFoodGuideDataset> adjust(List<CanadaFoodGuideDataset> foods) {
		foodResults = new ArrayList<>();
		fireDrools(foods);
		return foodResults;
	}

	public AdjustmentEngine() {
	}

	/**
	 * @param ruleset the ruleset to set
	 * @return this instance
	 */
	public AdjustmentEngine setReleaseIdAndRuleset(ReleaseId releaseId, String ruleset) {
		KieServices ks          = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		// KieContainer kContainer = ks.newKieContainer(releaseId);
		kieSessionPipeline      = new ArrayList<KieSession>();
		String kSessionName = "ksession-process-" + ruleset + "-tier";
		logger.debug("[01;03;35m" + "kSessionName: " + kSessionName + "[00;00m");
		logger.debug("[01;03;35m" + "ReleaseId: " + releaseId + "[00;00m");
		logger.debug("[01;03;31m" + "\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(kContainer.getKieSessionNamesInKieBase("dtables.tier")) + "[00;00m");
		kieSessionPipeline.add(kContainer.newKieSession(kSessionName));






		logger.debug("[01;03;31m" + "\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(kContainer.getKieSessionNamesInKieBase("dtables.tier")) + "[00;00m");
		logger.debug("[01;03;31m" + "ksession-process-" + ruleset + "-tier" + "[00;00m");

		return this;
	}

	/*
	 * Function: fireDrools
	 *  Purpose: fire the drools rules and calculate
	 *       in: foods
	 */
	private void fireDrools(List<CanadaFoodGuideDataset> foods) {
		for (CanadaFoodGuideDataset food : foods) {



			for (int i = 0; i < kieSessionPipeline.size(); i++) {
				if (!food.isDone()) {
					kieSessionPipeline.get(i).insert(food);
					kieSessionPipeline.get(i).fireAllRules();


					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;31m", "how many rulesets: "                   , kieSessionPipeline.size()           , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getReferenceAmountG(): "          , food.getReferenceAmountG()          , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getOverrideSmallRaAdjustment(): " , food.getOverrideSmallRaAdjustment() , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getAdjustedReferenceAmount(): "   , food.getAdjustedReferenceAmount()   , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getFopAdjustedReferenceAmount(): ", food.getFopAdjustedReferenceAmount(), "[00;00m");
				}
			}
			logger.debug("[01;03;31m" + "firing Drools" + "[00;00m");



			food.setCfgCode(Integer.parseInt(String.valueOf(food.getCfgCode()).substring(0, 3) + food.getTier()));


			foodResults.add(food);
		}
	}





























}
