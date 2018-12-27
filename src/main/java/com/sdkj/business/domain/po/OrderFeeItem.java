package com.sdkj.business.domain.po;

public class OrderFeeItem {
    private Long id;

    private Long orderId;

    private String feeName;

    private Float feeAmount;

    private Integer status;

    private Integer payMethod;

    private String paySerialNum;

    private String payTime;
    
    private Integer feeType;

    private Long driverId;

    private Float driverFee;

    private Long clientRefereeId;

    private Float clientRefereeFee;

    private Long driverRefereeId;

    private Float driverRefereeFee;
    
    private Float platFormFee;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName == null ? null : feeName.trim();
    }

    public Float getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Float feeAmount) {
        this.feeAmount = feeAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Integer payMethod) {
        this.payMethod = payMethod;
    }

    public String getPaySerialNum() {
        return paySerialNum;
    }

    public void setPaySerialNum(String paySerialNum) {
        this.paySerialNum = paySerialNum == null ? null : paySerialNum.trim();
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime == null ? null : payTime.trim();
    }

	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public Float getDriverFee() {
		return driverFee;
	}

	public void setDriverFee(Float driverFee) {
		this.driverFee = driverFee;
	}

	public Long getClientRefereeId() {
		return clientRefereeId;
	}

	public void setClientRefereeId(Long clientRefereeId) {
		this.clientRefereeId = clientRefereeId;
	}

	public Float getClientRefereeFee() {
		return clientRefereeFee;
	}

	public void setClientRefereeFee(Float clientRefereeFee) {
		this.clientRefereeFee = clientRefereeFee;
	}

	public Long getDriverRefereeId() {
		return driverRefereeId;
	}

	public void setDriverRefereeId(Long driverRefereeId) {
		this.driverRefereeId = driverRefereeId;
	}

	public Float getDriverRefereeFee() {
		return driverRefereeFee;
	}

	public void setDriverRefereeFee(Float driverRefereeFee) {
		this.driverRefereeFee = driverRefereeFee;
	}

	public Float getPlatFormFee() {
		return platFormFee;
	}

	public void setPlatFormFee(Float platFormFee) {
		this.platFormFee = platFormFee;
	}
    
}