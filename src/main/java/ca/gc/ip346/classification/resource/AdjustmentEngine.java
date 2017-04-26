package ca.gc.ip346.classification.resource;
import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import ca.gc.ip346.classification.model.CanadaFoodGuideDataset;
public class AdjustmentEngine {
	
	public static AdjustmentEngine adjustmentEngine = new AdjustmentEngine();
	//The pipeline is used to rule the drools rules one at a time
	private List<KieSession> kieSessionPipeline;
	private List<CanadaFoodGuideDataset> foodResults;
	
	//recreates singleton
	public static void refreshEngine(){
		adjustmentEngine = new AdjustmentEngine();
	}
	
	public AdjustmentEngine(){
		//taken from Wei Fang's code
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		kieSessionPipeline = new ArrayList<KieSession>();
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-tiers"));
	}
	
	public List<CanadaFoodGuideDataset> adjust(List<CanadaFoodGuideDataset> foods){
		foodResults = new ArrayList<>();
		fireDrools(foods);
		return foodResults;
	}

	/*
	 * Function: fireDrools
	 *  Purpose: fire the drools rules and calculate
	 *       in: foods
	*/
	private void fireDrools(List<CanadaFoodGuideDataset> foods){
		for(CanadaFoodGuideDataset food:foods){
			for(int i=0;i<kieSessionPipeline.size();i++){
				if(!food.isDone()){
					kieSessionPipeline.get(i).insert(food);
					kieSessionPipeline.get(i).fireAllRules();
				}
			}
			food.setCfgCode(Integer.parseInt(String.valueOf(food.getCfgCode()).substring(0,3)+food.getTier()));
			foodResults.add(food);
		}
	}
}
