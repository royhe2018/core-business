package com.sdkj.business.domain.po;


public class User {
    private Long id;

    private String account;

    private String passWord;
    
    private String headImg;

    private String nickName;

    private Integer sex;

    private Integer userType;

    private Long refereeId;

    private String registrionId;
    
    private Float balance;
    
    private String mapTerminalId;
    
    private String refereeName;
    
    private Float commentScore;
    
    private String newestTraceId;
    
    private String registerCity;
    
    private String deviceId;
    
    private Integer terminalType;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }
    
    public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Long getRefereeId() {
        return refereeId;
    }

    public void setRefereeId(Long refereeId) {
        this.refereeId = refereeId;
    }

	public String getRegistrionId() {
		return registrionId;
	}

	public void setRegistrionId(String registrionId) {
		this.registrionId = registrionId;
	}

	public Float getBalance() {
		return balance;
	}

	public void setBalance(Float balance) {
		this.balance = balance;
	}

	public String getMapTerminalId() {
		return mapTerminalId;
	}

	public void setMapTerminalId(String mapTerminalId) {
		this.mapTerminalId = mapTerminalId;
	}

	public String getRefereeName() {
		return refereeName;
	}

	public void setRefereeName(String refereeName) {
		this.refereeName = refereeName;
	}

	public Float getCommentScore() {
		return commentScore;
	}

	public void setCommentScore(Float commentScore) {
		this.commentScore = commentScore;
	}

	public String getNewestTraceId() {
		return newestTraceId;
	}

	public void setNewestTraceId(String newestTraceId) {
		this.newestTraceId = newestTraceId;
	}

	public String getRegisterCity() {
		return registerCity;
	}

	public void setRegisterCity(String registerCity) {
		this.registerCity = registerCity;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(Integer terminalType) {
		this.terminalType = terminalType;
	}
}