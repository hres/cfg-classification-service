package ca.gc.ip346.classification.resource;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import ca.gc.ip346.classification.model.CanadaFoodGuideDataset;

public class InitEngine {

	public static InitEngine initEngine = new InitEngine();
	/* The pipeline is used to rule the drools rules one at a time */
	private List<KieSession> kieSessionPipeline;
	private List<CanadaFoodGuideDataset> foodResults;

	/* recreates singleton */
	public static void refreshEngine() {
		initEngine = new InitEngine();
	}

	public InitEngine() {
		/* taken from Wei Fang's code */
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		kieSessionPipeline = new ArrayList<KieSession>();
		// kieSessionPipeline.add(kContainer.newKieSession("ksession-process-default"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-fop"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-shortcut"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-thresholds"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-init"));
	}

	public List<CanadaFoodGuideDataset> setInit(List<CanadaFoodGuideDataset> foods) {
		foodResults = new ArrayList<>();
		fireDrools(foods);
		return foodResults;
	}

	private void prepare(CanadaFoodGuideDataset food) {
		food.setLowFat           (false);
		food.setLowSodium        (false);
		food.setLowSugar         (false);
		food.setHighFat          (false);
		food.setHighSatFat       (false);
		food.setHighSodium       (false);
		food.setHighSugar        (false);
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
				}
			}
			foodResults.add(food);
		}
	}
}
