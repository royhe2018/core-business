package com.sdkj.business.domain.po;


public class OrderInfo {
    private Long id;

    private Long userId;
    
    private Integer useTimeType;
    
    private String useTruckTime;

    private String contactName;

    private String contactPhone;

    private Long vehicleTypeId;

    private Long serviceVehicleLevelId;

    private Long distributionFeeId;

    private String specialRequirementIds;

    private Float startFee;

    private Float extraFee;

    private Float insuranceFee;

    private Float attachFee;

    private Float totalFee;

    private Long driverId;

    private String createTime;

    private String finishTime;

    private Integer status;

    private String remark;
    
    private String takedTime;
    
    private String closeCodePic;
    
    private String thingListPic;
    
    private Integer dataVersion;
    
    private Integer payStatus;
    
    private Integer cancleStatus;
    
    private String signNamePic;
    
    private String signReceiveTime;
    
    private Long receiveUserId;
    
    private String cityName;
    
    private Float totalDistance;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    
    public Integer getUseTimeType() {
		return useTimeType;
	}

	public void setUseTimeType(Integer useTimeType) {
		this.useTimeType = useTimeType;
	}

	public String getUseTruckTime() {
        return useTruckTime;
    }

    public void setUseTruckTime(String useTruckTime) {
        this.useTruckTime = useTruckTime == null ? null : useTruckTime.trim();
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName == null ? null : contactName.trim();
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone == null ? null : contactPhone.trim();
    }

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public Long getServiceVehicleLevelId() {
        return serviceVehicleLevelId;
    }

    public void setServiceVehicleLevelId(Long serviceVehicleLevelId) {
        this.serviceVehicleLevelId = serviceVehicleLevelId;
    }

    public Long getDistributionFeeId() {
        return distributionFeeId;
    }

    public void setDistributionFeeId(Long distributionFeeId) {
        this.distributionFeeId = distributionFeeId;
    }

    public String getSpecialRequirementIds() {
        return specialRequirementIds;
    }

    public void setSpecialRequirementIds(String specialRequirementIds) {
        this.specialRequirementIds = specialRequirementIds == null ? null : specialRequirementIds.trim();
    }

    public Float getStartFee() {
        return startFee;
    }

    public void setStartFee(Float startFee) {
        this.startFee = startFee;
    }

    public Float getExtraFee() {
        return extraFee;
    }

    public void setExtraFee(Float extraFee) {
        this.extraFee = extraFee;
    }

    public Float getInsuranceFee() {
        return insuranceFee;
    }

    public void setInsuranceFee(Float insuranceFee) {
        this.insuranceFee = insuranceFee;
    }

    public Float getAttachFee() {
        return attachFee;
    }

    public void setAttachFee(Float attachFee) {
        this.attachFee = attachFee;
    }

    public Float getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Float totalFee) {
        this.totalFee = totalFee;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime == null ? null : finishTime.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTakedTime() {
		return takedTime;
	}

	public void setTakedTime(String takedTime) {
		this.takedTime = takedTime;
	}

	public String getCloseCodePic() {
		return closeCodePic;
	}

	public void setCloseCodePic(String closeCodePic) {
		this.closeCodePic = closeCodePic;
	}

	public String getThingListPic() {
		return thingListPic;
	}

	public void setThingListPic(String thingListPic) {
		this.thingListPic = thingListPic;
	}

	public Integer getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(Integer dataVersion) {
		this.dataVersion = dataVersion;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public Integer getCancleStatus() {
		return cancleStatus;
	}

	public void setCancleStatus(Integer cancleStatus) {
		this.cancleStatus = cancleStatus;
	}

	public String getSignNamePic() {
		return signNamePic;
	}

	public void setSignNamePic(String signNamePic) {
		this.signNamePic = signNamePic;
	}

	public String getSignReceiveTime() {
		return signReceiveTime;
	}

	public void setSignReceiveTime(String signReceiveTime) {
		this.signReceiveTime = signReceiveTime;
	}

	public Long getReceiveUserId() {
		return receiveUserId;
	}

	public void setReceiveUserId(Long receiveUserId) {
		this.receiveUserId = receiveUserId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Float getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(Float totalDistance) {
		this.totalDistance = totalDistance;
	}
}