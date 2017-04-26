package ca.gc.ip346.classification.resource;
import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import ca.gc.ip346.classification.model.CanadaFoodGuideDataset;
public class TemplateEngine {
	
	public static TemplateEngine engine = new TemplateEngine();
	//The pipeline is used to rule the drools rules one at a time
	private List<KieSession> kieSessionPipeline;
	private List<CanadaFoodGuideDataset> foodResults;
	
	//recreates singleton
	public static void refreshEngine(){
		engine = new TemplateEngine();
	}
	
	public TemplateEngine(){
		//taken from Wei Fang's code
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		kieSessionPipeline = new ArrayList<KieSession>();
		//kieSessionPipeline.add(kContainer.newKieSession("ksession-process-test"));
	}
	
	private void prepare(CanadaFoodGuideDataset food){
	}
	
	public List<CanadaFoodGuideDataset> doSomething(List<CanadaFoodGuideDataset> foods){
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
			//set food properties from null to defaults (false?)
			//prepare(food);
			for(int i=0;i<kieSessionPipeline.size();i++){
				kieSessionPipeline.get(i).insert(food);
				kieSessionPipeline.get(i).fireAllRules();
				//do something at the end of every rule table run
			}
			foodResults.add(food);
		}
	}
}
