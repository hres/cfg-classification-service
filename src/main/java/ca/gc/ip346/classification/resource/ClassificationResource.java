package ca.gc.ip346.classification.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public List<CanadaFoodGuideDataset> classifyDataset(Dataset dataset) {
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		foods = FlagsEngine.flagsEngine.setFlags(foods);
		foods = InitEngine.initEngine.setInit(foods);
		foods = AdjustmentEngine.adjustmentEngine.adjust(foods);
		List<CanadaFoodGuideDataset> foodResults = foods;
		return foodResults;
	}
	
	@POST
	@Path("/flags")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public List<CanadaFoodGuideDataset> flagsDataset(Dataset dataset) {
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		List<CanadaFoodGuideDataset> foodResults = FlagsEngine.flagsEngine.setFlags(foods);
		return foodResults;
	}
	
	@POST
	@Path("/init")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public List<CanadaFoodGuideDataset> initDataset(Dataset dataset) {
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		List<CanadaFoodGuideDataset> foodResults = InitEngine.initEngine.setInit(foods);
		return foodResults;
	}
	
	@POST
	@Path("/adjustment")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public List<CanadaFoodGuideDataset> adjustmentDataset(Dataset dataset) {
		List<CanadaFoodGuideDataset> foods = dataset.getData();
		List<CanadaFoodGuideDataset> foodResults = AdjustmentEngine.adjustmentEngine.adjust(foods);
		return foodResults;
	}
	
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	//@JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
	public String test() {
		return "Test suceeded";
	}
}
