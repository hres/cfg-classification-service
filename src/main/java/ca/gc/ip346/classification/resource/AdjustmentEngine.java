package ca.gc.ip346.classification.resource;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.Level.*;
import static org.kie.api.conf.DeclarativeAgendaOption.ENABLED;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.LoggerFactory;


import com.google.gson.GsonBuilder;
import com.google.protobuf.GeneratedMessage.Builder;

import ca.gc.ip346.classification.model.CanadaFoodGuideDataset;

public class AdjustmentEngine {
	//wma add as test
	public class TrackingAgendaEventListener extends DefaultAgendaEventListener  {

	    private final Logger log = LogManager.getLogger(AdjustmentEngine.class);
	    //private static Logger log = LoggerFactory.getLogger(TrackingAgendaEventListener.class);
	    private List<Match> matchList = new ArrayList<Match>();
	    
	    @Override
	    public void afterMatchFired(AfterMatchFiredEvent event) {
	        Rule rule = event.getMatch().getRule();

	        String ruleName = rule.getName();
	        Map<String, Object> ruleMetaDataMap = rule.getMetaData();

	        matchList.add(event.getMatch());
	        StringBuilder sb = new StringBuilder("Rule fired: " + ruleName);

	        if (ruleMetaDataMap.size() > 0) {
	            sb.append("\n  With [" + ruleMetaDataMap.size() + "] meta-data:");
	            for (String key : ruleMetaDataMap.keySet()) {
	                sb.append("\n    key=" + key + ", value="
	                        + ruleMetaDataMap.get(key));
	            }
	        }

	        log.debug(sb.toString());
	    }

	    public boolean isRuleFired(String ruleName) {
	        for (Match a : matchList) {
	            if (a.getRule().getName().equals(ruleName)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    public void reset() {
	        matchList.clear();
	    }

	    public final List<Match> getMatchList() {
	        return matchList;
	    }

	    public String matchsToString() {
	        if (matchList.size() == 0) {
	            return "No matchs occurred.";
	        } else {
	            StringBuilder sb = new StringBuilder("Matchs: ");
	            for (Match match : matchList) {
	                sb.append("\n  rule: ").append(match.getRule().getName());
	            }
	            return sb.toString();
	        }
	    }

	}
	// wma add ended
	private static final Logger logger = LogManager.getLogger(AdjustmentEngine.class);

	public static AdjustmentEngine adjustmentEngine = new AdjustmentEngine();
	/* The pipeline is used to rule the drools rules one at a time */
	private List<KieSession> kieSessionPipeline;
	private List<CanadaFoodGuideDataset> foodResults;
	
	static Map<String, KieBase> kieBaseCache =  new HashMap<>();;
	
	public KieSession testKieSession = null;

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
	 * @param releaseId the releaseId to use
	 * @param ruleset the ruleset to use
	 * @return this instance
	 */
	public AdjustmentEngine setReleaseIdAndRuleset(String rulePath, String ruleset) {
		kieSessionPipeline      = new ArrayList<KieSession>();
		kieSessionPipeline.add(getKieSession(rulePath, ruleset, ClassificationResource.FoodRuleNames.TIER.ruleName()));
		
		return this;
	}
	
	private KieSession getKieSession(String ruleDir, String ruleSetId, String name) {
		
		KieSession kieSession = null;
		String kieSessionName = "ksession-process-" + ruleSetId + "-" + name;
		if (kieBaseCache.get(kieSessionName) == null) {
	
			KieServices ks          = KieServices.Factory.get();
			String inMemoryFileName = ruleDir  + name + "/" + ruleSetId + "/" + name + ruleSetId + ".xls";
			KieFileSystem kfs = ks.newKieFileSystem();
			kfs.write(ks.getResources().newFileSystemResource(inMemoryFileName)
					.setResourceType(ResourceType.DTABLE));
			
			KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
			
			if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
				System.out.println(kieBuilder.getResults().toString());
			}
			KieContainer kContainer = ks.newKieContainer(kieBuilder.getKieModule().getReleaseId());
			
			KieBase kbase = kContainer.getKieBase();
			
			kieSession = kbase.newKieSession();
			AgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
			kieSession.addEventListener(agendaEventListener);
			
			System.out.println("Adjustment Put rules KieBase into Custom Cache=============");
			kieBaseCache.put(kieSessionName, kbase);
		}else {
			System.out.println("Get existing rules KieBase from Adjust================Custom Cache");
			kieSession = kieBaseCache.get(kieSessionName).newKieSession();
		}
		return kieSession;
		
	}
	
	/*
	 * Function: fireDrools
	 *  Purpose: fire the drools rules and calculate
	 *       in: foods
	 */
	private void fireDrools(List<CanadaFoodGuideDataset> foods) {
		KieSession kieSession = null;
		
		for (CanadaFoodGuideDataset food : foods) {
			for (int i = 0; i < kieSessionPipeline.size(); i++) {
				if (!food.isDone()) {
					kieSession = kieSessionPipeline.get(i);
					kieSession.insert(food);
					
					int ruleFiredCount = kieSession.fireAllRules();
					
					System.out.println("Adjust.food....触发了" + ruleFiredCount + "条规则");
					
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;31m", "how many rulesets: "                   , kieSessionPipeline.size()           , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getReferenceAmountG(): "          , food.getReferenceAmountG()          , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getOverrideSmallRaAdjustment(): " , food.getOverrideSmallRaAdjustment() , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getAdjustedReferenceAmount(): "   , food.getAdjustedReferenceAmount()   , "[00;00m");
					logger.printf(DEBUG, "%s%44s%s%s", "[01;03;33m", "food.getFopAdjustedReferenceAmount(): ", food.getFopAdjustedReferenceAmount(), "[00;00m");
					
				}
			}
			logger.debug("[01;03;31m" + "firing Drools" + "[00;00m");


			logger.debug("[01;03;31m" + "Fianl======= before set CFG code: " + food.getCfgCode() + "[00;00m");
			logger.debug("[01;03;31m" + "Fianl======= CFG Tier: " + food.getTier() + "[00;00m");
			food.setCfgCode(Integer.parseInt(String.valueOf(food.getCfgCode()).substring(0, 3) + food.getTier()));
			logger.debug("[01;03;31m" + "Fianl======= CFG code: " + food.getCfgCode() + "[00;00m");

			foodResults.add(food);
		}
	}
}



























