package com.sdkj.business.domain.po;

public class LeaseTruckOrder {
    private Long id;

    private Long userId;

    private Long serviceStationId;

    private Long vehicleTypeId;

    private String specialRequirementIds;

    private String contactName;

    private String contactPhone;

    private String startTime;

    private String endTime;

    private String realStartTime;

    private String realEndTime;

    private Float totalFee;

    private Integer payStatus;

    private Integer status;

    private String createTime;

    private Long sendDriverId;

    private Long fetchDriverId;

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

    public Long getServiceStationId() {
        return serviceStationId;
    }

    public void setServiceStationId(Long serviceStationId) {
        this.serviceStationId = serviceStationId;
    }

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getSpecialRequirementIds() {
        return specialRequirementIds;
    }

    public void setSpecialRequirementIds(String specialRequirementIds) {
        this.specialRequirementIds = specialRequirementIds == null ? null : specialRequirementIds.trim();
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime == null ? null : startTime.trim();
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime == null ? null : endTime.trim();
    }

    public String getRealStartTime() {
        return realStartTime;
    }

    public void setRealStartTime(String realStartTime) {
        this.realStartTime = realStartTime == null ? null : realStartTime.trim();
    }

    public String getRealEndTime() {
        return realEndTime;
    }

    public void setRealEndTime(String realEndTime) {
        this.realEndTime = realEndTime == null ? null : realEndTime.trim();
    }

    public Float getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Float totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public Long getSendDriverId() {
        return sendDriverId;
    }

    public void setSendDriverId(Long sendDriverId) {
        this.sendDriverId = sendDriverId;
    }

    public Long getFetchDriverId() {
        return fetchDriverId;
    }

    public void setFetchDriverId(Long fetchDriverId) {
        this.fetchDriverId = fetchDriverId;
    }
}