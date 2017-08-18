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

public class FlagsEngine {
	private static final Logger logger = LogManager.getLogger(FlagsEngine.class);

	public static FlagsEngine flagsEngine = new FlagsEngine();
	/* The pipeline is used to rule the drools rules one at a time */
	private List<KieSession> kieSessionPipeline;
	private List<CanadaFoodGuideDataset> foodResults;

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
	 * @param ruleset the ruleset to set
	 * @return this instance
	 */
	public FlagsEngine setReleaseIdAndRuleset(ReleaseId releaseId, String ruleset) {
		KieServices ks          = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		// KieContainer kContainer = ks.newKieContainer(releaseId);
		kieSessionPipeline      = new ArrayList<KieSession>();
		String kSessionName = "ksession-process-" + ruleset + "-refamt";
		logger.error("[01;03;35m" + "kSessionName: " + kSessionName + "[00;00m");
		logger.error("[01;03;35m" + "ReleaseId: " + releaseId + "[00;00m");
		logger.error("[01;03;31m" + "\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(kContainer.getKieSessionNamesInKieBase("dtables.refamt")) + "[00;00m");
		kieSessionPipeline.add(kContainer.newKieSession(kSessionName));






		logger.error("[01;03;31m" + "\n" + new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create().toJson(kContainer.getKieSessionNamesInKieBase("dtables.refamt")) + "[00;00m");
		logger.error("[01;03;31m" + "ksession-process-" + ruleset + "-refamt" + "[00;00m");

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
					kieSessionPipeline.get(i).insert(food);
					kieSessionPipeline.get(i).fireAllRules();
					/* only call this after adjustedRA is set */
					calculatePerRA(food);
					logger.printf(ERROR, "%s%44s%s%s", "[01;03;33m", "how many rulesets: "                   , kieSessionPipeline.size()           , "[00;00m");
					logger.printf(ERROR, "%s%44s%s%s", "[01;03;33m", "food.getReferenceAmountG(): "          , food.getReferenceAmountG()          , "[00;00m");
					logger.printf(ERROR, "%s%44s%s%s", "[01;03;33m", "food.getOverrideSmallRaAdjustment(): " , food.getOverrideSmallRaAdjustment() , "[00;00m");
					logger.printf(ERROR, "%s%44s%s%s", "[01;03;33m", "food.getAdjustedReferenceAmount(): "   , food.getAdjustedReferenceAmount()   , "[00;00m");
					logger.printf(ERROR, "%s%44s%s%s", "[01;03;33m", "food.getFopAdjustedReferenceAmount(): ", food.getFopAdjustedReferenceAmount(), "[00;00m");
				}
			}
			logger.error("[01;03;31m" + "firing Drools" + "[00;00m");






			foodResults.add(food);
		}
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
