package ca.gc.ip346.classification.model;
import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
public class FoodClassificationEngine {
	//assumed nutrient location after removing index and name
	protected static final int RA=2;
	protected static final int SUGAR=3;
	protected static final int SODIUM=4;
	protected static final int FAT=5;
	protected static final int TSAT=6;
	
	public static FoodClassificationEngine foodClassificationEngine = new FoodClassificationEngine();
	private List<KieSession> kieSessionPipeline;
	private List<List<CanadaFoodGuideDataset>> foodResults;
	
	//recreates singleton
	public static void refreshEngine(){
		foodClassificationEngine = new FoodClassificationEngine();
	}
	
	public FoodClassificationEngine(){
		//taken from Wei Fang's code
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		kieSessionPipeline = new ArrayList<KieSession>();
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-tiers"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-flags"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-thresholds"));
		kieSessionPipeline.add(kContainer.newKieSession("ksession-process-init"));
		/*
		KieSession kSessionTiers = kContainer.newKieSession("ksession-process-tiers");
		KieSession kSessionFlags = kContainer.newKieSession("ksession-process-flags");
		KieSession kSessionThresholds = kContainer.newKieSession("ksession-process-thresholds");
		KieSession kSessionInit = kContainer.newKieSession("ksession-process-init");
		*/
	}
	
	public List<List<CanadaFoodGuideDataset>> classify(ArrayList<CanadaFoodGuideDataset> foods){
		foodResults = new ArrayList<>();
		for (int i=0;i<kieSessionPipeline.size();i++){
			foodResults.add(new ArrayList<>());
		}
		fireDrools(foods);
		return foodResults;
	}
	
	private void prepare(CanadaFoodGuideDataset food){
		
	}
	
	/*
	 * Function: readAndAssignData
	 * Purpose: read the csv file and instantiate TieredFood
	 * Order of cells in excel spreadsheet: Index, Name, Subgroup Code, Given TSAT Tier, sugar, sodium, fat, saturated fat
	*/
	/*
	private ArrayList<CanadaFoodGuideDataset> readAndAssignData(BufferedReader reader) throws Exception{
		//current assumption index,Food Name,Subgroup,Init Tier, RA,sug/g,sod/g,fat/g,tsat/g
		ArrayList<CanadaFoodGuideDataset> foods = new ArrayList<CanadaFoodGuideDataset>();
		TieredFood tmpFood;
		String line;
		String[] quoteTokens;
		String[] fieldTokens;
		line = reader.readLine();
		line = reader.readLine();
		while(line!=null){
			tmpFood = new TieredFood();
			//normal multi-named cases
			if(line.contains("\"")){
				quoteTokens = line.split("\"");
				//if name contains " concat them
				tmpFood.setIndex(Integer.parseInt(quoteTokens[0].split(",")[0]));
				tmpFood.setLabel(quoteTokens[1].toLowerCase());
				fieldTokens = quoteTokens[2].substring(1, quoteTokens[2].length()).split(",");
			}else{
				//if single word names
				quoteTokens = line.split(",");
				tmpFood.setIndex(Integer.parseInt(quoteTokens[0]));
				tmpFood.setLabel(quoteTokens[1].toLowerCase());
				//make fieldTokens quoteTokens but start at 2
				String concatString="";
				for(int i=2;i<quoteTokens.length;i++){
					concatString += quoteTokens[i] + ",";
				}
				fieldTokens = concatString.split(",");
			}
			//tester
			for(String token : fieldTokens){
				if(token.charAt(0)=='\"'){
					token = token.substring(1, token.length());
				}
				if(token.charAt(token.length()-1)=='\"'){
					token = token.substring(0, token.length()-1);
				}
			}
			String subgroup = fieldTokens[0];
			tmpFood.setSubGroup(subgroup.substring(0,3));
			//deprecated code this assumes init tier is given
			//tmpFood.setInitTier(Integer.parseInt(fieldTokens[1]));
			tmpFood.setRA(Double.parseDouble(fieldTokens[RA]));
			if(tmpFood.getRA() <= 30){
				tmpFood.setRA(50);
			}
			//calculate the contents per reference amount
			tmpFood.setSugarPRA(Double.parseDouble(fieldTokens[SUGAR])*tmpFood.getRA());
			tmpFood.setSodiumPRA(Double.parseDouble(fieldTokens[SODIUM])*tmpFood.getRA());
			tmpFood.setFatPRA(Double.parseDouble(fieldTokens[FAT])*tmpFood.getRA());
			tmpFood.setTSATPRA(Double.parseDouble(fieldTokens[TSAT])*tmpFood.getRA());
			foods.add(tmpFood);
			line = reader.readLine();
		}
		reader.close();
		return foods;
	}
	*/

	/*
	 * Function: fireDrools
	 *  Purpose: fire the drools rules and calculate
	 *       in: foods, all KieSessions
	*/
	private void fireDrools(List<CanadaFoodGuideDataset> foods){
		for(CanadaFoodGuideDataset food:foods){
			for(int i=0;i<kieSessionPipeline.size();i++){
				kieSessionPipeline.get(i).insert(food);
				kieSessionPipeline.get(i).fireAllRules();
				foodResults.get(i).add(food);
			}
			/*
			thresholds.insert(food);
			thresholds.fireAllRules();
			flags.insert(food);
			flags.fireAllRules();
			init.insert(food);
			init.fireAllRules();
			tiers.insert(food);
			tiers.fireAllRules();*/
		}
	}
}
