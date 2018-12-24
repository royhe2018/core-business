package com.sdkj.business.domain.po;

public class DistributionFee {
    private Long id;

    private Long vehicleTypeId;

    private Long serviceLevelType;
    
    private String provence;

    private String city;

    private Integer startMileage;

    private Float startFee;

    private Integer feeType;

    private Float feeAmountPerMileage;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getProvence() {
        return provence;
    }

    public void setProvence(String provence) {
        this.provence = provence == null ? null : provence.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public Integer getStartMileage() {
        return startMileage;
    }

    public void setStartMileage(Integer startMileage) {
        this.startMileage = startMileage;
    }

    public Float getStartFee() {
        return startFee;
    }

    public void setStartFee(Float startFee) {
        this.startFee = startFee;
    }

    public Integer getFeeType() {
        return feeType;
    }

    public void setFeeType(Integer feeType) {
        this.feeType = feeType;
    }

	public Long getServiceLevelType() {
		return serviceLevelType;
	}

	public void setServiceLevelType(Long serviceLevelType) {
		this.serviceLevelType = serviceLevelType;
	}

	public Float getFeeAmountPerMileage() {
		return feeAmountPerMileage;
	}

	public void setFeeAmountPerMileage(Float feeAmountPerMileage) {
		this.feeAmountPerMileage = feeAmountPerMileage;
	}
	
}