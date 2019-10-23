package com.egovalley.domain;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("all")
public class EgoUser implements Serializable {

    private int idUser;
    private String userCode;
    private String userStatus;
    private String egoUsername;
    private String egoPassword;
    private String egoNickname;
    private String egoGender;
    private String egoBirthday;
    private String egoPhone;
    private String egoEmail;
    private Date createTime;
    private String creator;
    private Date lastUpdateTime;
    private String lastUpdator;
    private String isDelete;

    public EgoUser() {
    }

    public EgoUser(String userCode, String userStatus, String egoUsername, String egoPassword, String egoNickname, String egoGender, String egoBirthday, String egoPhone, String egoEmail, Date createTime, String creator, Date lastUpdateTime, String lastUpdator, String isDelete) {
        this.userCode = userCode;
        this.userStatus = userStatus;
        this.egoUsername = egoUsername;
        this.egoPassword = egoPassword;
        this.egoNickname = egoNickname;
        this.egoGender = egoGender;
        this.egoBirthday = egoBirthday;
        this.egoPhone = egoPhone;
        this.egoEmail = egoEmail;
        this.createTime = createTime;
        this.creator = creator;
        this.lastUpdateTime = lastUpdateTime;
        this.lastUpdator = lastUpdator;
        this.isDelete = isDelete;
    }

    public EgoUser(int idUser, String userCode, String userStatus, String egoUsername, String egoPassword, String egoNickname, String egoGender, String egoBirthday, String egoPhone, String egoEmail, Date createTime, String creator, Date lastUpdateTime, String lastUpdator, String isDelete) {
        this.idUser = idUser;
        this.userCode = userCode;
        this.userStatus = userStatus;
        this.egoUsername = egoUsername;
        this.egoPassword = egoPassword;
        this.egoNickname = egoNickname;
        this.egoGender = egoGender;
        this.egoBirthday = egoBirthday;
        this.egoPhone = egoPhone;
        this.egoEmail = egoEmail;
        this.createTime = createTime;
        this.creator = creator;
        this.lastUpdateTime = lastUpdateTime;
        this.lastUpdator = lastUpdator;
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "EgoUser{" +
                "idUser=" + idUser +
                ", userCode='" + userCode + '\'' +
                ", userStatus='" + userStatus + '\'' +
                ", egoUsername='" + egoUsername + '\'' +
                ", egoPassword='" + egoPassword + '\'' +
                ", egoNickname='" + egoNickname + '\'' +
                ", egoGender='" + egoGender + '\'' +
                ", egoBirthday='" + egoBirthday + '\'' +
                ", egoPhone='" + egoPhone + '\'' +
                ", egoEmail='" + egoEmail + '\'' +
                ", createTime=" + createTime +
                ", creator='" + creator + '\'' +
                ", lastUpdateTime=" + lastUpdateTime +
                ", lastUpdator='" + lastUpdator + '\'' +
                ", isDelete='" + isDelete + '\'' +
                '}';
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getEgoUsername() {
        return egoUsername;
    }

    public void setEgoUsername(String egoUsername) {
        this.egoUsername = egoUsername;
    }

    public String getEgoPassword() {
        return egoPassword;
    }

    public void setEgoPassword(String egoPassword) {
        this.egoPassword = egoPassword;
    }

    public String getEgoNickname() {
        return egoNickname;
    }

    public void setEgoNickname(String egoNickname) {
        this.egoNickname = egoNickname;
    }

    public String getEgoGender() {
        return egoGender;
    }

    public void setEgoGender(String egoGender) {
        this.egoGender = egoGender;
    }

    public String getEgoBirthday() {
        return egoBirthday;
    }

    public void setEgoBirthday(String egoBirthday) {
        this.egoBirthday = egoBirthday;
    }

    public String getEgoPhone() {
        return egoPhone;
    }

    public void setEgoPhone(String egoPhone) {
        this.egoPhone = egoPhone;
    }

    public String getEgoEmail() {
        return egoEmail;
    }

    public void setEgoEmail(String egoEmail) {
        this.egoEmail = egoEmail;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdator() {
        return lastUpdator;
    }

    public void setLastUpdator(String lastUpdator) {
        this.lastUpdator = lastUpdator;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

}
