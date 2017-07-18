package ca.gc.ip346.classification.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

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
		/* taken from Wei Fang's code */
		KieServices ks          = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		kieSessionPipeline      = new ArrayList<KieSession>();
		// kieSessionPipeline.add(kContainer.newKieSession("ksession-process-flags"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-refamt"));



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
			boolean setRA = false;
			for (int i = 0; i < kieSessionPipeline.size(); i++) {
				if (!food.isDone()) {
					kieSessionPipeline.get(i).insert(food);
					kieSessionPipeline.get(i).fireAllRules();
					logger.error("[01;03;31m" + kieSessionPipeline.size() + "[00;00m");
					/* only call this after adjustedRA is set */
					calculatePerRA(food);
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
