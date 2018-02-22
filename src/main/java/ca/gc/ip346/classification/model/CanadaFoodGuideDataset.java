package ca.gc.ip346.classification.model;

import java.util.Date;

public class CanadaFoodGuideDataset {
	private String    type;                               /* smallint         */
	private Integer   code;                               /* integer          */
	private String    name;                               /* text             */
	private Integer   cnfGroupCode;                       /* integer          */
	private Integer   cfgCode;                            /* integer          */
	private Integer   classifiedCfgCode;                  /* integer          */
	private Date      cfgCodeUpdateDate;                  /* date             */
	private Double    energyKcal;                         /* double precision */
	private Double    sodiumAmountPer100g;                /* double precision */
	private String    sodiumImputationReference;          /* text             */
	private Date      sodiumImputationDate;               /* date             */
	private Double    sugarAmountPer100g;                 /* double precision */
	private String    sugarImputationReference;           /* text             */
	private Date      sugarImputationDate;                /* date             */
	private Double    transfatAmountPer100g;              /* double precision */
	private String    transfatImputationReference;        /* text             */
	private Date      transfatImputationDate;             /* date             */
	private Double    satfatAmountPer100g;                /* double precision */
	private String    satfatImputationReference;          /* text             */
	private Date      satfatImputationDate;               /* date             */
	private Double    totalFatAmountPer100g;              /* double precision */
	private String    totalFatImputationReference;        /* text             */
	private Date      totalFatImputationDate;             /* date             */
	private Boolean   containsAddedSodium;                /* smallint         */
	private Date      containsAddedSodiumUpdateDate;      /* date             */
	private Boolean   containsAddedSugar;                 /* smallint         */
	private Date      containsAddedSugarUpdateDate;       /* date             */
	private Boolean   containsFreeSugars;                 /* smallint         */
	private Date      containsFreeSugarsUpdateDate;       /* date             */
	private Boolean   containsAddedFat;                   /* smallint         */
	private Date      containsAddedFatUpdateDate;         /* date             */
	private Boolean   containsAddedTransfat;              /* smallint         */
	private Date      containsAddedTransfatUpdateDate;    /* date             */
	private Boolean   containsCaffeine;                   /* smallint         */
	private Date      containsCaffeineUpdateDate;         /* date             */
	private Boolean   containsSugarSubstitutes;           /* smallint         */
	private Date      containsSugarSubstitutesUpdateDate; /* date             */
	private Double    referenceAmountG;                   /* double precision */
	private String    referenceAmountMeasure;             /* text             */
	private Date      referenceAmountUpdateDate;          /* date             */
	private Double    foodGuideServingG;                  /* double precision */
	private String    foodGuideServingMeasure;            /* text             */
	private Date      foodGuideUpdateDate;                /* date             */
	private Double    tier4ServingG;                      /* double precision */
	private String    tier4ServingMeasure;                /* text             */
	private Date      tier4ServingUpdateDate;             /* date             */
	private Boolean   rolledUp;                           /* smallint         */
	private Date      rolledUpUpdateDate;                 /* date             */
	private Boolean   overrideSmallRaAdjustment;          /* smallint         */
	private Date      overrideSmallRaAdjustmentUpdateDate;/* date             */
	private Integer   replacementCode;                    /* integer          */
	private Date      replacementCodeUpdateDate;          /* date             */
	private Date      commitDate;                         /* date             */
	private String    comments;                           /* text             */
	private Date      commentsUpdateDate;                 /* date             */

	// remember that this food-item has been validated
	private Boolean validated;

	// special input
	private Double sodiumDV;
	private Double sugarDV;
	private Double satFatDV;

	// added by Robin
	private Boolean lowSodium;
	private Boolean highSodium;
	private Boolean lowSugar;
	private Boolean highSugar;
	private Boolean lowFat;
	private Boolean highFat;
	private Boolean lowSatFat;
	private Boolean highSatFat;
	private Boolean lowTransFat;
	private Boolean highTransFat;
	private Double adjustedReferenceAmount;
	private Double fopAdjustedReferenceAmount;
	private Double sodiumPerReferenceAmount;
	private Double sugarPerReferenceAmount;
	private Double fatPerReferenceAmount;
	private Double satFatPerReferenceAmount;
	private Double transFatPerReferenceAmount;
	private Double fopSugarPerReferenceAmount;
	private Double fopSodiumPerReferenceAmount;
	// checks to see if it has finished classification
	private Boolean done;
	private Integer tier;
	private Integer shift;
	private Boolean absolute;

	private Boolean satFatFopWarning;
	private Boolean sugarFopWarning;
	private Boolean sodiumFopWarning;
	private Boolean marketedToKids;
	private Date    marketedToKidsUpdateDate;
	private Integer initialCfgCode;
	private Boolean missingData;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the cnfGroupCode
	 */
	public Integer getCnfGroupCode() {
		return cnfGroupCode;
	}

	/**
	 * @param cnfGroupCode the cnfGroupCode to set
	 */
	public void setCnfGroupCode(Integer cnfGroupCode) {
		this.cnfGroupCode = cnfGroupCode;
	}

	/**
	 * @return the cfgCode
	 */
	public Integer getCfgCode() {
		return cfgCode;
	}

	/**
	 * @param cfgCode the cfgCode to set
	 */
	public void setCfgCode(Integer cfgCode) {
		this.cfgCode = cfgCode;
	}

	/**
	 * @return the classifiedCfgCode
	 */
	public Integer getClassifiedCfgCode() {
		return classifiedCfgCode;
	}

	/**
	 * @param classifiedCfgCode the classifiedCfgCode to set
	 */
	public void setClassifiedCfgCode(Integer classifiedCfgCode) {
		this.classifiedCfgCode = classifiedCfgCode;
	}

	/**
	 * @return the cfgCodeUpdateDate
	 */
	public Date getCfgCodeUpdateDate() {
		return cfgCodeUpdateDate;
	}

	/**
	 * @param cfgCodeUpdateDate the cfgCodeUpdateDate to set
	 */
	public void setCfgCodeUpdateDate(Date cfgCodeUpdateDate) {
		this.cfgCodeUpdateDate = cfgCodeUpdateDate;
	}

	/**
	 * @return the energyKcal
	 */
	public Double getEnergyKcal() {
		return energyKcal;
	}

	/**
	 * @param energyKcal the energyKcal to set
	 */
	public void setEnergyKcal(Double energyKcal) {
		this.energyKcal = energyKcal;
	}

	/**
	 * @return the sodiumAmountPer100g
	 */
	public Double getSodiumAmountPer100g() {
		return sodiumAmountPer100g;
	}

	/**
	 * @param sodiumAmountPer100g the sodiumAmountPer100g to set
	 */
	public void setSodiumAmountPer100g(Double sodiumAmountPer100g) {
		this.sodiumAmountPer100g = sodiumAmountPer100g;
	}

	/**
	 * @return the sodiumImputationReference
	 */
	public String getSodiumImputationReference() {
		return sodiumImputationReference;
	}

	/**
	 * @param sodiumImputationReference the sodiumImputationReference to set
	 */
	public void setSodiumImputationReference(String sodiumImputationReference) {
		this.sodiumImputationReference = sodiumImputationReference;
	}

	/**
	 * @return the sodiumImputationDate
	 */
	public Date getSodiumImputationDate() {
		return sodiumImputationDate;
	}

	/**
	 * @param sodiumImputationDate the sodiumImputationDate to set
	 */
	public void setSodiumImputationDate(Date sodiumImputationDate) {
		this.sodiumImputationDate = sodiumImputationDate;
	}

	/**
	 * @return the sugarAmountPer100g
	 */
	public Double getSugarAmountPer100g() {
		return sugarAmountPer100g;
	}

	/**
	 * @param sugarAmountPer100g the sugarAmountPer100g to set
	 */
	public void setSugarAmountPer100g(Double sugarAmountPer100g) {
		this.sugarAmountPer100g = sugarAmountPer100g;
	}

	/**
	 * @return the sugarImputationReference
	 */
	public String getSugarImputationReference() {
		return sugarImputationReference;
	}

	/**
	 * @param sugarImputationReference the sugarImputationReference to set
	 */
	public void setSugarImputationReference(String sugarImputationReference) {
		this.sugarImputationReference = sugarImputationReference;
	}

	/**
	 * @return the sugarImputationDate
	 */
	public Date getSugarImputationDate() {
		return sugarImputationDate;
	}

	/**
	 * @param sugarImputationDate the sugarImputationDate to set
	 */
	public void setSugarImputationDate(Date sugarImputationDate) {
		this.sugarImputationDate = sugarImputationDate;
	}

	/**
	 * @return the transfatAmountPer100g
	 */
	public Double getTransfatAmountPer100g() {
		return transfatAmountPer100g;
	}

	/**
	 * @param transfatAmountPer100g the transfatAmountPer100g to set
	 */
	public void setTransfatAmountPer100g(Double transfatAmountPer100g) {
		this.transfatAmountPer100g = transfatAmountPer100g;
	}

	/**
	 * @return the transfatImputationReference
	 */
	public String getTransfatImputationReference() {
		return transfatImputationReference;
	}

	/**
	 * @param transfatImputationReference the transfatImputationReference to set
	 */
	public void setTransfatImputationReference(String transfatImputationReference) {
		this.transfatImputationReference = transfatImputationReference;
	}

	/**
	 * @return the transfatImputationDate
	 */
	public Date getTransfatImputationDate() {
		return transfatImputationDate;
	}

	/**
	 * @param transfatImputationDate the transfatImputationDate to set
	 */
	public void setTransfatImputationDate(Date transfatImputationDate) {
		this.transfatImputationDate = transfatImputationDate;
	}

	/**
	 * @return the satfatAmountPer100g
	 */
	public Double getSatfatAmountPer100g() {
		return satfatAmountPer100g;
	}

	/**
	 * @param satfatAmountPer100g the satfatAmountPer100g to set
	 */
	public void setSatfatAmountPer100g(Double satfatAmountPer100g) {
		this.satfatAmountPer100g = satfatAmountPer100g;
	}

	/**
	 * @return the satfatImputationReference
	 */
	public String getSatfatImputationReference() {
		return satfatImputationReference;
	}

	/**
	 * @param satfatImputationReference the satfatImputationReference to set
	 */
	public void setSatfatImputationReference(String satfatImputationReference) {
		this.satfatImputationReference = satfatImputationReference;
	}

	/**
	 * @return the satfatImputationDate
	 */
	public Date getSatfatImputationDate() {
		return satfatImputationDate;
	}

	/**
	 * @param satfatImputationDate the satfatImputationDate to set
	 */
	public void setSatfatImputationDate(Date satfatImputationDate) {
		this.satfatImputationDate = satfatImputationDate;
	}

	/**
	 * @return the totalFatAmountPer100g
	 */
	public Double getTotalFatAmountPer100g() {
		return totalFatAmountPer100g;
	}

	/**
	 * @param totalFatAmountPer100g the totalFatAmountPer100g to set
	 */
	public void setTotalFatAmountPer100g(Double totalFatAmountPer100g) {
		this.totalFatAmountPer100g = totalFatAmountPer100g;
	}

	/**
	 * @return the totalFatImputationReference
	 */
	public String getTotalFatImputationReference() {
		return totalFatImputationReference;
	}

	/**
	 * @param totalFatImputationReference the totalFatImputationReference to set
	 */
	public void setTotalFatImputationReference(String totalFatImputationReference) {
		this.totalFatImputationReference = totalFatImputationReference;
	}

	/**
	 * @return the totalFatImputationDate
	 */
	public Date getTotalFatImputationDate() {
		return totalFatImputationDate;
	}

	/**
	 * @param totalFatImputationDate the totalFatImputationDate to set
	 */
	public void setTotalFatImputationDate(Date totalFatImputationDate) {
		this.totalFatImputationDate = totalFatImputationDate;
	}

	/**
	 * @return the containsAddedSodium
	 */
	public Boolean getContainsAddedSodium() {
		return containsAddedSodium;
	}

	/**
	 * @param containsAddedSodium the containsAddedSodium to set
	 */
	public void setContainsAddedSodium(Boolean containsAddedSodium) {
		this.containsAddedSodium = containsAddedSodium;
	}

	/**
	 * @return the containsAddedSodiumUpdateDate
	 */
	public Date getContainsAddedSodiumUpdateDate() {
		return containsAddedSodiumUpdateDate;
	}

	/**
	 * @param containsAddedSodiumUpdateDate the containsAddedSodiumUpdateDate to set
	 */
	public void setContainsAddedSodiumUpdateDate(Date containsAddedSodiumUpdateDate) {
		this.containsAddedSodiumUpdateDate = containsAddedSodiumUpdateDate;
	}

	/**
	 * @return the containsAddedSugar
	 */
	public Boolean getContainsAddedSugar() {
		return containsAddedSugar;
	}

	/**
	 * @param containsAddedSugar the containsAddedSugar to set
	 */
	public void setContainsAddedSugar(Boolean containsAddedSugar) {
		this.containsAddedSugar = containsAddedSugar;
	}

	/**
	 * @return the containsAddedSugarUpdateDate
	 */
	public Date getContainsAddedSugarUpdateDate() {
		return containsAddedSugarUpdateDate;
	}

	/**
	 * @param containsAddedSugarUpdateDate the containsAddedSugarUpdateDate to set
	 */
	public void setContainsAddedSugarUpdateDate(Date containsAddedSugarUpdateDate) {
		this.containsAddedSugarUpdateDate = containsAddedSugarUpdateDate;
	}

	/**
	 * @return the containsFreeSugars
	 */
	public Boolean getContainsFreeSugars() {
		return containsFreeSugars;
	}

	/**
	 * @param containsFreeSugars the containsFreeSugars to set
	 */
	public void setContainsFreeSugars(Boolean containsFreeSugars) {
		this.containsFreeSugars = containsFreeSugars;
	}

	/**
	 * @return the containsFreeSugarsUpdateDate
	 */
	public Date getContainsFreeSugarsUpdateDate() {
		return containsFreeSugarsUpdateDate;
	}

	/**
	 * @param containsFreeSugarsUpdateDate the containsFreeSugarsUpdateDate to set
	 */
	public void setContainsFreeSugarsUpdateDate(Date containsFreeSugarsUpdateDate) {
		this.containsFreeSugarsUpdateDate = containsFreeSugarsUpdateDate;
	}

	/**
	 * @return the containsAddedFat
	 */
	public Boolean getContainsAddedFat() {
		return containsAddedFat;
	}

	/**
	 * @param containsAddedFat the containsAddedFat to set
	 */
	public void setContainsAddedFat(Boolean containsAddedFat) {
		this.containsAddedFat = containsAddedFat;
	}

	/**
	 * @return the containsAddedFatUpdateDate
	 */
	public Date getContainsAddedFatUpdateDate() {
		return containsAddedFatUpdateDate;
	}

	/**
	 * @param containsAddedFatUpdateDate the containsAddedFatUpdateDate to set
	 */
	public void setContainsAddedFatUpdateDate(Date containsAddedFatUpdateDate) {
		this.containsAddedFatUpdateDate = containsAddedFatUpdateDate;
	}

	/**
	 * @return the containsAddedTransfat
	 */
	public Boolean getContainsAddedTransfat() {
		return containsAddedTransfat;
	}

	/**
	 * @param containsAddedTransfat the containsAddedTransfat to set
	 */
	public void setContainsAddedTransfat(Boolean containsAddedTransfat) {
		this.containsAddedTransfat = containsAddedTransfat;
	}

	/**
	 * @return the containsAddedTransfatUpdateDate
	 */
	public Date getContainsAddedTransfatUpdateDate() {
		return containsAddedTransfatUpdateDate;
	}

	/**
	 * @param containsAddedTransfatUpdateDate the containsAddedTransfatUpdateDate to set
	 */
	public void setContainsAddedTransfatUpdateDate(Date containsAddedTransfatUpdateDate) {
		this.containsAddedTransfatUpdateDate = containsAddedTransfatUpdateDate;
	}

	/**
	 * @return the containsCaffeine
	 */
	public Boolean getContainsCaffeine() {
		return containsCaffeine;
	}

	/**
	 * @param containsCaffeine the containsCaffeine to set
	 */
	public void setContainsCaffeine(Boolean containsCaffeine) {
		this.containsCaffeine = containsCaffeine;
	}

	/**
	 * @return the containsCaffeineUpdateDate
	 */
	public Date getContainsCaffeineUpdateDate() {
		return containsCaffeineUpdateDate;
	}

	/**
	 * @param containsCaffeineUpdateDate the containsCaffeineUpdateDate to set
	 */
	public void setContainsCaffeineUpdateDate(Date containsCaffeineUpdateDate) {
		this.containsCaffeineUpdateDate = containsCaffeineUpdateDate;
	}

	/**
	 * @return the containsSugarSubstitutes
	 */
	public Boolean getContainsSugarSubstitutes() {
		return containsSugarSubstitutes;
	}

	/**
	 * @param containsSugarSubstitutes the containsSugarSubstitutes to set
	 */
	public void setContainsSugarSubstitutes(Boolean containsSugarSubstitutes) {
		this.containsSugarSubstitutes = containsSugarSubstitutes;
	}

	/**
	 * @return the containsSugarSubstitutesUpdateDate
	 */
	public Date getContainsSugarSubstitutesUpdateDate() {
		return containsSugarSubstitutesUpdateDate;
	}

	/**
	 * @param containsSugarSubstitutesUpdateDate the containsSugarSubstitutesUpdateDate to set
	 */
	public void setContainsSugarSubstitutesUpdateDate(Date containsSugarSubstitutesUpdateDate) {
		this.containsSugarSubstitutesUpdateDate = containsSugarSubstitutesUpdateDate;
	}

	/**
	 * @return the referenceAmountG
	 */
	public Double getReferenceAmountG() {
		return referenceAmountG;
	}

	/**
	 * @param referenceAmountG the referenceAmountG to set
	 */
	public void setReferenceAmountG(Double referenceAmountG) {
		this.referenceAmountG = referenceAmountG;
	}

	/**
	 * @return the referenceAmountMeasure
	 */
	public String getReferenceAmountMeasure() {
		return referenceAmountMeasure;
	}

	/**
	 * @param referenceAmountMeasure the referenceAmountMeasure to set
	 */
	public void setReferenceAmountMeasure(String referenceAmountMeasure) {
		this.referenceAmountMeasure = referenceAmountMeasure;
	}

	/**
	 * @return the referenceAmountUpdateDate
	 */
	public Date getReferenceAmountUpdateDate() {
		return referenceAmountUpdateDate;
	}

	/**
	 * @param referenceAmountUpdateDate the referenceAmountUpdateDate to set
	 */
	public void setReferenceAmountUpdateDate(Date referenceAmountUpdateDate) {
		this.referenceAmountUpdateDate = referenceAmountUpdateDate;
	}

	/**
	 * @return the foodGuideServingG
	 */
	public Double getFoodGuideServingG() {
		return foodGuideServingG;
	}

	/**
	 * @param foodGuideServingG the foodGuideServingG to set
	 */
	public void setFoodGuideServingG(Double foodGuideServingG) {
		this.foodGuideServingG = foodGuideServingG;
	}

	/**
	 * @return the foodGuideServingMeasure
	 */
	public String getFoodGuideServingMeasure() {
		return foodGuideServingMeasure;
	}

	/**
	 * @param foodGuideServingMeasure the foodGuideServingMeasure to set
	 */
	public void setFoodGuideServingMeasure(String foodGuideServingMeasure) {
		this.foodGuideServingMeasure = foodGuideServingMeasure;
	}

	/**
	 * @return the foodGuideUpdateDate
	 */
	public Date getFoodGuideUpdateDate() {
		return foodGuideUpdateDate;
	}

	/**
	 * @param foodGuideUpdateDate the foodGuideUpdateDate to set
	 */
	public void setFoodGuideUpdateDate(Date foodGuideUpdateDate) {
		this.foodGuideUpdateDate = foodGuideUpdateDate;
	}

	/**
	 * @return the tier4ServingG
	 */
	public Double getTier4ServingG() {
		return tier4ServingG;
	}

	/**
	 * @param tier4ServingG the tier4ServingG to set
	 */
	public void setTier4ServingG(Double tier4ServingG) {
		this.tier4ServingG = tier4ServingG;
	}

	/**
	 * @return the tier4ServingMeasure
	 */
	public String getTier4ServingMeasure() {
		return tier4ServingMeasure;
	}

	/**
	 * @param tier4ServingMeasure the tier4ServingMeasure to set
	 */
	public void setTier4ServingMeasure(String tier4ServingMeasure) {
		this.tier4ServingMeasure = tier4ServingMeasure;
	}

	/**
	 * @return the tier4ServingUpdateDate
	 */
	public Date getTier4ServingUpdateDate() {
		return tier4ServingUpdateDate;
	}

	/**
	 * @param tier4ServingUpdateDate the tier4ServingUpdateDate to set
	 */
	public void setTier4ServingUpdateDate(Date tier4ServingUpdateDate) {
		this.tier4ServingUpdateDate = tier4ServingUpdateDate;
	}

	/**
	 * @return the rolledUp
	 */
	public Boolean getRolledUp() {
		return rolledUp;
	}

	/**
	 * @param rolledUp the rolledUp to set
	 */
	public void setRolledUp(Boolean rolledUp) {
		this.rolledUp = rolledUp;
	}

	/**
	 * @return the rolledUpUpdateDate
	 */
	public Date getRolledUpUpdateDate() {
		return rolledUpUpdateDate;
	}

	/**
	 * @param rolledUpUpdateDate the rolledUpUpdateDate to set
	 */
	public void setRolledUpUpdateDate(Date rolledUpUpdateDate) {
		this.rolledUpUpdateDate = rolledUpUpdateDate;
	}

	/**
	 * @return the overrideSmallRaAdjustment
	 */
	public Boolean getOverrideSmallRaAdjustment() {
		return overrideSmallRaAdjustment;
	}

	/**
	 * @param overrideSmallRaAdjustment the overrideSmallRaAdjustment to set
	 */
	public void setOverrideSmallRaAdjustment(Boolean overrideSmallRaAdjustment) {
		this.overrideSmallRaAdjustment = overrideSmallRaAdjustment;
	}

	/**
	 * @return the overrideSmallRaAdjustmentUpdateDate
	 */
	public Date getOverrideSmallRaAdjustmentUpdateDate() {
		return overrideSmallRaAdjustmentUpdateDate;
	}

	/**
	 * @param overrideSmallRaAdjustmentUpdateDate the overrideSmallRaAdjustmentUpdateDate to set
	 */
	public void setOverrideSmallRaAdjustmentUpdateDate(Date overrideSmallRaAdjustmentUpdateDate) {
		this.overrideSmallRaAdjustmentUpdateDate = overrideSmallRaAdjustmentUpdateDate;
	}

	/**
	 * @return the replacementCode
	 */
	public Integer getReplacementCode() {
		return replacementCode;
	}

	/**
	 * @param replacementCode the replacementCode to set
	 */
	public void setReplacementCode(Integer replacementCode) {
		this.replacementCode = replacementCode;
	}

	/**
	 * @return the replacementCodeUpdateDate
	 */
	public Date getReplacementCodeUpdateDate() {
		return replacementCodeUpdateDate;
	}

	/**
	 * @param replacementCodeUpdateDate the replacementCodeUpdateDate to set
	 */
	public void setReplacementCodeUpdateDate(Date replacementCodeUpdateDate) {
		this.replacementCodeUpdateDate = replacementCodeUpdateDate;
	}

	/**
	 * @return the commitDate
	 */
	public Date getCommitDate() {
		return commitDate;
	}

	/**
	 * @param commitDate the commitDate to set
	 */
	public void setCommitDate(Date commitDate) {
		this.commitDate = commitDate;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the commentsUpdateDate
	 */
	public Date getCommentsUpdateDate() {
		return commentsUpdateDate;
	}

	/**
	 * @param commentsUpdateDate the commentsUpdateDate to set
	 */
	public void setCommentsUpdateDate(Date commentsUpdateDate) {
		this.commentsUpdateDate = commentsUpdateDate;
	}

	/**
	 * @return the validated
	 */
	public Boolean getValidated() {
		return validated;
	}

	/**
	 * @param validated the validated to set
	 */
	public void setValidated(Boolean validated) {
		this.validated = validated;
	}

	public Double getSodiumDV() {
		return sodiumDV;
	}

	public void setSodiumDV(Double sodiumDV) {
		this.sodiumDV = sodiumDV;
	}

	public Double getSugarDV() {
		return sugarDV;
	}

	public void setSugarDV(Double sugarDV) {
		this.sugarDV = sugarDV;
	}

	public Double getSatFatDV() {
		return satFatDV;
	}

	public void setSatFatDV(Double satFatDV) {
		this.satFatDV = satFatDV;
	}

	public Boolean isLowSodium() {
		return lowSodium;
	}

	public void setLowSodium(Boolean lowSodium) {
		this.lowSodium = lowSodium;
	}

	public Boolean isHighSodium() {
		return highSodium;
	}

	public void setHighSodium(Boolean highSodium) {
		this.highSodium = highSodium;
	}

	public Boolean isLowSugar() {
		return lowSugar;
	}

	public void setLowSugar(Boolean lowSugar) {
		this.lowSugar = lowSugar;
	}

	public Boolean isHighSugar() {
		return highSugar;
	}

	public void setHighSugar(Boolean highSugar) {
		this.highSugar = highSugar;
	}

	public Boolean isLowFat() {
		return lowFat;
	}

	public void setLowFat(Boolean lowFat) {
		this.lowFat = lowFat;
	}

	public Boolean isHighFat() {
		return highFat;
	}

	public void setHighFat(Boolean highFat) {
		this.highFat = highFat;
	}

	/**
	 * @return the lowSatFat
	 */
	public Boolean isLowSatFat() {
		return lowSatFat;
	}

	/**
	 * @param lowSatFat the lowSatFat to set
	 */
	public void setLowSatFat(Boolean lowSatFat) {
		this.lowSatFat = lowSatFat;
	}

	public Boolean isHighSatFat() {
		return highSatFat;
	}

	public void setHighSatFat(Boolean highSatFat) {
		this.highSatFat = highSatFat;
	}

	public Boolean isLowTransFat() {
		return lowTransFat;
	}

	public void setLowTransFat(Boolean lowTransFat) {
		this.lowTransFat = lowTransFat;
	}

	public Boolean isHighTransFat() {
		return highTransFat;
	}

	public void setHighTransFat(Boolean highTransFat) {
		this.highTransFat = highTransFat;
	}

	public Double getAdjustedReferenceAmount() {
		return adjustedReferenceAmount;
	}

	public void setAdjustedReferenceAmount(Double adjustedReferenceAmount) {
		this.adjustedReferenceAmount = adjustedReferenceAmount;
	}

	public Double getFopAdjustedReferenceAmount() {
		return fopAdjustedReferenceAmount;
	}

	public void setFopAdjustedReferenceAmount(Double fopAdjustedReferenceAmount) {
		this.fopAdjustedReferenceAmount = fopAdjustedReferenceAmount;
	}

	public Double getSodiumPerReferenceAmount() {
		return sodiumPerReferenceAmount;
	}

	public void setSodiumPerReferenceAmount(Double sodiumPerReferenceAmount) {
		this.sodiumPerReferenceAmount = sodiumPerReferenceAmount;
	}

	public Double getSugarPerReferenceAmount() {
		return sugarPerReferenceAmount;
	}

	public void setSugarPerReferenceAmount(Double sugarPerReferenceAmount) {
		this.sugarPerReferenceAmount = sugarPerReferenceAmount;
	}

	public Double getFatPerReferenceAmount() {
		return fatPerReferenceAmount;
	}

	public void setFatPerReferenceAmount(Double fatPerReferenceAmount) {
		this.fatPerReferenceAmount = fatPerReferenceAmount;
	}

	public Double getSatFatPerReferenceAmount() {
		return satFatPerReferenceAmount;
	}

	public void setSatFatPerReferenceAmount(Double satFatPerReferenceAmount) {
		this.satFatPerReferenceAmount = satFatPerReferenceAmount;
	}

	public Double getTransFatPerReferenceAmount() {
		return transFatPerReferenceAmount;
	}

	public void setTransFatPerReferenceAmount(Double transFatPerReferenceAmount) {
		this.transFatPerReferenceAmount = transFatPerReferenceAmount;
	}

	public Double getFopSugarPerReferenceAmount() {
		return fopSugarPerReferenceAmount;
	}

	public void setFopSugarPerReferenceAmount(Double fopSugarPerReferenceAmount) {
		this.fopSugarPerReferenceAmount = fopSugarPerReferenceAmount;
	}

	public Double getFopSodiumPerReferenceAmount() {
		return fopSodiumPerReferenceAmount;
	}

	public void setFopSodiumPerReferenceAmount(Double fopSodiumPerReferenceAmount) {
		this.fopSodiumPerReferenceAmount = fopSodiumPerReferenceAmount;
	}

	public Boolean isDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public Integer getTier() {
		return tier;
	}

	public void setTier(Integer tier) {
		this.tier = tier;
	}
	public void setAdjustedTier(Integer tier) {
		this.tier     = tier;
		this.done     = true;
		this.absolute = true;
	}
	public void shiftTier(int shift) {
		if (!done) {
			this.tier  += shift;
			this.shift += shift;
		}
	}

	public Integer getShift() {
		return shift;
	}

	public void setShift(Integer shift) {
		this.shift = shift;
	}

	public Boolean getAbsolute() {
		return absolute;
	}

	public void setAbsolute(Boolean absolute) {
		this.absolute = absolute;
	}

	public Boolean getSatFatFopWarning() {
		return satFatFopWarning;
	}

	public void setSatFatFopWarning(Boolean satFatFopWarning) {
		this.satFatFopWarning = satFatFopWarning;
	}

	public Boolean getSugarFopWarning() {
		return sugarFopWarning;
	}

	public void setSugarFopWarning(Boolean sugarFopWarning) {
		this.sugarFopWarning = sugarFopWarning;
	}

	public Boolean getSodiumFopWarning() {
		return sodiumFopWarning;
	}

	public void setSodiumFopWarning(Boolean sodiumFopWarning) {
		this.sodiumFopWarning = sodiumFopWarning;
	}

	public Boolean getMarketedToKids() {
		return marketedToKids;
	}

	public void setMarketedToKids(Boolean marketedToKids) {
		this.marketedToKids = marketedToKids;
	}

	/**
	 * @return the marketedToKidsUpdateDate
	 */
	public Date getMarketedToKidsUpdateDate() {
		return marketedToKidsUpdateDate;
	}

	/**
	 * @param marketedToKidsUpdateDate the marketedToKidsUpdateDate to set
	 */
	public void setMarketedToKidsUpdateDate(Date marketedToKidsUpdateDate) {
		this.marketedToKidsUpdateDate = marketedToKidsUpdateDate;
	}

	/**
	 * @return the initialCfgCode
	 */
	public Integer getInitialCfgCode() {
		return initialCfgCode;
	}

	/**
	 * @param initialCfgCode the initialCfgCode to set
	 */
	public void setInitialCfgCode(Integer initialCfgCode) {
		this.initialCfgCode = initialCfgCode;
	}

	/**
	 * @return the missingData
	 */
	public Boolean getMissingData() {
		return missingData;
	}

	/**
	 * @param missingData the missingData to set
	 */
	public void setMissingData(Boolean missingData) {
		this.missingData = missingData;
	}
}
