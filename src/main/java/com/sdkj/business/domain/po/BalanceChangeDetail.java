package com.sdkj.business.domain.po;

public class BalanceChangeDetail {
    private Long id;

    private Long userId;

    private Float balanceBeforeChange;

    private Float balanceAfterChange;

    private Float changeAmount;

    private String changeTime;

    private Integer changeType;

    private Long itemId;
    
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

    public Float getBalanceBeforeChange() {
        return balanceBeforeChange;
    }

    public void setBalanceBeforeChange(Float balanceBeforeChange) {
        this.balanceBeforeChange = balanceBeforeChange;
    }

    public Float getBalanceAfterChange() {
        return balanceAfterChange;
    }

    public void setBalanceAfterChange(Float balanceAfterChange) {
        this.balanceAfterChange = balanceAfterChange;
    }

    public Float getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Float changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime == null ? null : changeTime.trim();
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
    
}