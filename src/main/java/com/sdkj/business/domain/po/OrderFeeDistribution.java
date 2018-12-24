package com.sdkj.business.domain.po;

public class OrderFeeDistribution {
    private Long id;

    private Long orderId;

    private Long driverId;

    private Float driverFee;

    private Long clientRefereeId;

    private Float clientRefereeFee;

    private Long driverRefereeId;

    private Float driverRefereeFee;

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
}