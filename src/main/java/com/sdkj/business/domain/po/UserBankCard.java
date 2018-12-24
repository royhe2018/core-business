package com.sdkj.business.domain.po;

public class UserBankCard {
    private Long id;

    private String bankName;

    private String cardType;

    private String cardNum;

    private String ownerName;

    private String ownerCardNum;

    private String reservePhone;

    private Long userId;

    private String createTime;

    private Integer status;

    private String cardBackImg;
    
    private String last4gigNum;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType == null ? null : cardType.trim();
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum == null ? null : cardNum.trim();
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName == null ? null : ownerName.trim();
    }

    public String getOwnerCardNum() {
        return ownerCardNum;
    }

    public void setOwnerCardNum(String ownerCardNum) {
        this.ownerCardNum = ownerCardNum == null ? null : ownerCardNum.trim();
    }

    public String getReservePhone() {
        return reservePhone;
    }

    public void setReservePhone(String reservePhone) {
        this.reservePhone = reservePhone == null ? null : reservePhone.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

	public String getCardBackImg() {
		return cardBackImg;
	}

	public void setCardBackImg(String cardBackImg) {
		this.cardBackImg = cardBackImg;
	}

	public String getLast4gigNum() {
		return last4gigNum;
	}

	public void setLast4gigNum(String last4gigNum) {
		this.last4gigNum = last4gigNum;
	}
    
}