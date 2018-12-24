package com.sdkj.business.domain.po;


public class VehicleTypeInfo {
    private Long id;

    private String typeName;

    private Float length;

    private Float width;

    private Float height;

    private Float carryingCapacity;

    private Float volume;

    private String image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName == null ? null : typeName.trim();
    }

    public Float getLength() {
        return length;
    }

    public void setLength(Float length) {
        this.length = length;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getCarryingCapacity() {
        return carryingCapacity;
    }

    public void setCarryingCapacity(Float carryingCapacity) {
        this.carryingCapacity = carryingCapacity;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}