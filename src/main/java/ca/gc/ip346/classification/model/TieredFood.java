package ca.gc.ip346.classification.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

// enable the following line to produce xml.
@XmlRootElement  
public class TieredFood {
	private String name;
	private int adjustedTier = 0;
	private int tsatTier;
	private String group;
	private String label;
	private String subGroup;
	private boolean isLowFat = false;
	private boolean isLowSugar = false;
	private boolean isSugarAdded = false;
	private boolean isFatAdded = false;
	private String exclusion1 = "";
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}	
	public String getExclusion1() {
		return exclusion1;
	}
	public void setExclusion1(String exclusion1) {
		this.exclusion1 = exclusion1;
	}		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAdjustedTier() {
		if(adjustedTier == 0){
			// the tier is not adjusted, so it remains the same as the tsatTier.
			return tsatTier;
		}
		return adjustedTier;
	}
	public void setAdjustedTier(int adjustedTier) {
		this.adjustedTier = adjustedTier;
		System.out.println("Adjusted tier to " + adjustedTier);
	}
	public int getTsatTier() {
		return tsatTier;
	}
	public void setTsatTier(int tsatTier) {
		this.tsatTier = tsatTier;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getSubGroup() {
		return subGroup;
	}
	public void setSubGroup(String subGroup) {
		this.subGroup = subGroup;
	}

	public boolean isLowFat() {
		return isLowFat;
	}
	public void setLowFat(boolean isLowFat) {
		this.isLowFat = isLowFat;
	}

	public boolean isSugarAdded() {
		return isSugarAdded;
	}
	public void setSugarAdded(boolean isSugarAdded) {
		this.isSugarAdded = isSugarAdded;
	}
	
	public boolean isLowSugar() {
		return isLowSugar;
	}
	public void setLowSugar(boolean isLowSugar) {
		this.isLowSugar = isLowSugar;
	}
	public boolean isFatAdded() {
		return isFatAdded;
	}
	public void setFatAdded(boolean isFatAdded) {
		this.isFatAdded = isFatAdded;
	}
	
	public void shiftTier(int tier){
		this.adjustedTier = this.tsatTier + tier;
		System.out.println("Shifted tier by " + tier);
	}
}
