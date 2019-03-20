package com.sdkj.business.domain.po;

public class DriverInfo {
    private Long id;

    private Long userId;

    private String registerCity;
    
    private String idCardNo;

    private Integer idCardType;

    private String drivingLicenseNo;

    private String drivingLicenseFileNo;

    private String drivingLicenseType;

    private String drivingLicenseImage;

    private String idCardImage;

    private String idCardBackImage;

    private String carNo;

    private String carDrivingImage;

    private String createTime;

    private Integer status;
    
    private Integer onDutyStatus;
    
    private Integer driverType;
    
    private Long vehicleTypeId;
    
    private String driverName;
    
    private String carFrontPhoto;
    
    private String carLateralPhoto;
    
    private String carRearPhoto;
    
    
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

    public String getRegisterCity() {
		return registerCity;
	}

	public void setRegisterCity(String registerCity) {
		this.registerCity = registerCity;
	}

	public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo == null ? null : idCardNo.trim();
    }

    public Integer getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(Integer idCardType) {
        this.idCardType = idCardType;
    }

    public String getDrivingLicenseNo() {
        return drivingLicenseNo;
    }

    public void setDrivingLicenseNo(String drivingLicenseNo) {
        this.drivingLicenseNo = drivingLicenseNo == null ? null : drivingLicenseNo.trim();
    }

    public String getDrivingLicenseFileNo() {
        return drivingLicenseFileNo;
    }

    public void setDrivingLicenseFileNo(String drivingLicenseFileNo) {
        this.drivingLicenseFileNo = drivingLicenseFileNo == null ? null : drivingLicenseFileNo.trim();
    }

    public String getDrivingLicenseType() {
        return drivingLicenseType;
    }

    public void setDrivingLicenseType(String drivingLicenseType) {
        this.drivingLicenseType = drivingLicenseType == null ? null : drivingLicenseType.trim();
    }

    public String getDrivingLicenseImage() {
        return drivingLicenseImage;
    }

    public void setDrivingLicenseImage(String drivingLicenseImage) {
        this.drivingLicenseImage = drivingLicenseImage == null ? null : drivingLicenseImage.trim();
    }

    public String getIdCardImage() {
        return idCardImage;
    }

    public void setIdCardImage(String idCardImage) {
        this.idCardImage = idCardImage == null ? null : idCardImage.trim();
    }

    public String getIdCardBackImage() {
        return idCardBackImage;
    }

    public void setIdCardBackImage(String idCardBackImage) {
        this.idCardBackImage = idCardBackImage == null ? null : idCardBackImage.trim();
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo == null ? null : carNo.trim();
    }

    public String getCarDrivingImage() {
        return carDrivingImage;
    }

    public void setCarDrivingImage(String carDrivingImage) {
        this.carDrivingImage = carDrivingImage == null ? null : carDrivingImage.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public Integer getOnDutyStatus() {
		return onDutyStatus;
	}

	public void setOnDutyStatus(Integer onDutyStatus) {
		this.onDutyStatus = onDutyStatus;
	}

	public Long getVehicleTypeId() {
		return vehicleTypeId;
	}

	public void setVehicleTypeId(Long vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
	}

	public Integer getDriverType() {
		return driverType;
	}

	public void setDriverType(Integer driverType) {
		this.driverType = driverType;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getCarFrontPhoto() {
		return carFrontPhoto;
	}

	public void setCarFrontPhoto(String carFrontPhoto) {
		this.carFrontPhoto = carFrontPhoto;
	}

	public String getCarLateralPhoto() {
		return carLateralPhoto;
	}

	public void setCarLateralPhoto(String carLateralPhoto) {
		this.carLateralPhoto = carLateralPhoto;
	}

	public String getCarRearPhoto() {
		return carRearPhoto;
	}

	public void setCarRearPhoto(String carRearPhoto) {
		this.carRearPhoto = carRearPhoto;
	}
	
}