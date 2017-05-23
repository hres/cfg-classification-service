package ca.gc.ip346.classification.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;

import ca.gc.ip346.classification.model.CanadaFoodGuideDataset;
import ca.gc.ip346.classification.model.Dataset;

@Path("/")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ClassificationResource {

	@POST
	@Path("/classify")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> classifyDataset(Dataset dataset) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CanadaFoodGuideDataset> foods = dataset.getData();

		foods = FlagsEngine      .flagsEngine      .setFlags (foods);
		foods = InitEngine       .initEngine       .setInit  (foods);
		foods = AdjustmentEngine .adjustmentEngine .adjust   (foods);
		foods = prepareCfgCode(foods);

		List<CanadaFoodGuideDataset> foodResults = foods;
		dataset.setStatus("Classified");
		map.put("data",     foodResults);
		map.put("name",     dataset.getName());
		map.put("status",   dataset.getStatus());
		map.put("env",      dataset.getEnv());
		map.put("owner",    dataset.getOwner());
		map.put("comments", dataset.getComments());
		return map;
	}

	@POST
	@Path("/flags")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> flagsDataset(Dataset dataset) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		List<CanadaFoodGuideDataset> foodResults = FlagsEngine.flagsEngine.setFlags(foods);
		map.put("data", foodResults);
		return map;
	}

	@POST
	@Path("/init")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> initDataset(Dataset dataset) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		List<CanadaFoodGuideDataset> foodResults = InitEngine.initEngine.setInit(foods);
		map.put("data", foodResults);
		return map;
	}

	@POST
	@Path("/adjustment")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public Map<String, Object> adjustmentDataset(Dataset dataset) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		List<CanadaFoodGuideDataset> foodResults = AdjustmentEngine.adjustmentEngine.adjust(foods);
		map.put("data", foodResults);
		return map;
	}

	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public String test() {
		return "Test suceeded";
	}

	private List<CanadaFoodGuideDataset> prepareCfgCode(List<CanadaFoodGuideDataset> foods) {
		for (CanadaFoodGuideDataset food : foods) {
			Integer temp = food.getCfgCode();
			food.setCfgCode(food.getClassifiedCfgCode());
			food.setClassifiedCfgCode(temp);
		}
		return foods;
	}
}
