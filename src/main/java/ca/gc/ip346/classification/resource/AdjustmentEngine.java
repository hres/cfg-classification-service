package ca.gc.ip346.classification.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
// import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

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
		// KieContainer kContainer = ks.getKieClasspathContainer();
		KieContainer kContainer = ks.newKieContainer(releaseId);
		kieSessionPipeline      = new ArrayList<KieSession>();
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-" + ruleset + "-tier"));




		logger.error("[01;03;31m" + ruleset + "[00;00m");

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
					logger.error("[01;03;31m" + kieSessionPipeline.size() + "[00;00m");


				}
			}
			logger.error("[01;03;31m" + "firing Drools" + "[00;00m");



			food.setCfgCode(Integer.parseInt(String.valueOf(food.getCfgCode()).substring(0, 3) + food.getTier()));


			foodResults.add(food);
		}
	}





























}
