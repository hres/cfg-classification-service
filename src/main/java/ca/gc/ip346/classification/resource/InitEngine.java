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

public class InitEngine {
	private static final Logger logger = LogManager.getLogger(InitEngine.class);

	public static InitEngine initEngine = new InitEngine();
	/* The pipeline is used to rule the drools rules one at a time */
	private List<KieSession> kieSessionPipeline;
	private List<CanadaFoodGuideDataset> foodResults;

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
		/* taken from Wei Fang's code */
		KieServices ks          = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		kieSessionPipeline      = new ArrayList<KieSession>();
		// kieSessionPipeline.add(kContainer.newKieSession("ksession-process-default"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-fop"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-shortcut"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-thresholds"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-init"));
	}

	/*
	 * Function: fireDrools
	 *  Purpose: fire the drools rules and calculate
	 *       in: foods
	 */
	private void fireDrools(List<CanadaFoodGuideDataset> foods) {
		for (CanadaFoodGuideDataset food : foods) {

			prepare(food);

			for (int i = 0; i < kieSessionPipeline.size(); i++) {
				if (!food.isDone()) {
					kieSessionPipeline.get(i).insert(food);
					kieSessionPipeline.get(i).fireAllRules();
					logger.error("[01;03;31m" + kieSessionPipeline.size() + "[00;00m");


				}
			}
			logger.error("[01;03;31m" + "firing Drools" + "[00;00m");
			logger.error("[01;03;31m" + "CFG code: " + food.getCfgCode() + " tier: " + food.getTier() + "[00;00m");
			String firstThreeDigits = food.getCfgCode() + "";
			food.setInitialCfgCode(Integer.parseInt(firstThreeDigits.substring(0, 3) + food.getTier()));
			logger.error("[01;03;31m" + "initial CFG code: " + food.getInitialCfgCode() + "[00;00m");
			foodResults.add(food);
		}
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
