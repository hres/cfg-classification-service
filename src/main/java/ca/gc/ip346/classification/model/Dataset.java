package ca.gc.ip346.classification.model;

import java.util.Date;
import java.util.List;

public class Dataset {
	private List<CanadaFoodGuideDataset> data;
	private String name;
	private String env;
	private String owner;
	private String status;
	private String comments;
	private Date modifiedDate;

	public List<CanadaFoodGuideDataset> getData() {
		return data;
	}

	public void setData(List<CanadaFoodGuideDataset> data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}
