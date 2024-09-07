package com.gaj.GKook.bean;

public class User {
    private String id;
    private String username;
    private String identifyNum;
    private boolean online;
    private String os;
    private int status;
    private String avatar;
    private String vip_avatar;
    private String banner;
    private String nickname;
    private int[] roles;
    private boolean isVip;
    private boolean vipAmp;
    private boolean bot;
    private boolean mobileVerified;
    private boolean isSys;
    private long joinedAt;
    private long activeTime;

    public User() {
    }

    public User(String id, String username, String identifyNum, boolean online, String os, int status, String avatar, String vip_avatar, String banner, String nickname, int[] roles, boolean isVip, boolean vipAmp, boolean bot, boolean mobileVerified, boolean isSys, long joinedAt, long activeTime) {
        this.id = id;
        this.username = username;
        this.identifyNum = identifyNum;
        this.online = online;
        this.os = os;
        this.status = status;
        this.avatar = avatar;
        this.vip_avatar = vip_avatar;
        this.banner = banner;
        this.nickname = nickname;
        this.roles = roles;
        this.isVip = isVip;
        this.vipAmp = vipAmp;
        this.bot = bot;
        this.mobileVerified = mobileVerified;
        this.isSys = isSys;
        this.joinedAt = joinedAt;
        this.activeTime = activeTime;
    }

    /**
     * 获取
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取
     * @return identifyNum
     */
    public String getIdentifyNum() {
        return identifyNum;
    }

    /**
     * 设置
     * @param identifyNum
     */
    public void setIdentifyNum(String identifyNum) {
        this.identifyNum = identifyNum;
    }

    /**
     * 获取
     * @return online
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * 设置
     * @param online
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * 获取
     * @return os
     */
    public String getOs() {
        return os;
    }

    /**
     * 设置
     * @param os
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * 获取
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 获取
     * @return avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置
     * @param avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取
     * @return vip_avatar
     */
    public String getVip_avatar() {
        return vip_avatar;
    }

    /**
     * 设置
     * @param vip_avatar
     */
    public void setVip_avatar(String vip_avatar) {
        this.vip_avatar = vip_avatar;
    }

    /**
     * 获取
     * @return banner
     */
    public String getBanner() {
        return banner;
    }

    /**
     * 设置
     * @param banner
     */
    public void setBanner(String banner) {
        this.banner = banner;
    }

    /**
     * 获取
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取
     * @return roles
     */
    public int[] getRoles() {
        return roles;
    }

    /**
     * 设置
     * @param roles
     */
    public void setRoles(int[] roles) {
        this.roles = roles;
    }

    /**
     * 获取
     * @return isVip
     */
    public boolean isIsVip() {
        return isVip;
    }

    /**
     * 设置
     * @param isVip
     */
    public void setIsVip(boolean isVip) {
        this.isVip = isVip;
    }

    /**
     * 获取
     * @return vipAmp
     */
    public boolean isVipAmp() {
        return vipAmp;
    }

    /**
     * 设置
     * @param vipAmp
     */
    public void setVipAmp(boolean vipAmp) {
        this.vipAmp = vipAmp;
    }

    /**
     * 获取
     * @return bot
     */
    public boolean isBot() {
        return bot;
    }

    /**
     * 设置
     * @param bot
     */
    public void setBot(boolean bot) {
        this.bot = bot;
    }

    /**
     * 获取
     * @return mobileVerified
     */
    public boolean isMobileVerified() {
        return mobileVerified;
    }

    /**
     * 设置
     * @param mobileVerified
     */
    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    /**
     * 获取
     * @return isSys
     */
    public boolean isIsSys() {
        return isSys;
    }

    /**
     * 设置
     * @param isSys
     */
    public void setIsSys(boolean isSys) {
        this.isSys = isSys;
    }

    /**
     * 获取
     * @return joinedAt
     */
    public long getJoinedAt() {
        return joinedAt;
    }

    /**
     * 设置
     * @param joinedAt
     */
    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }

    /**
     * 获取
     * @return activeTime
     */
    public long getActiveTime() {
        return activeTime;
    }

    /**
     * 设置
     * @param activeTime
     */
    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }

    public String toString() {
        return "User{id = " + id + ", username = " + username + ", identifyNum = " + identifyNum + ", online = " + online + ", os = " + os + ", status = " + status + ", avatar = " + avatar + ", vip_avatar = " + vip_avatar + ", banner = " + banner + ", nickname = " + nickname + ", roles = " + roles + ", isVip = " + isVip + ", vipAmp = " + vipAmp + ", bot = " + bot + ", mobileVerified = " + mobileVerified + ", isSys = " + isSys + ", joinedAt = " + joinedAt + ", activeTime = " + activeTime + "}";
    }
}
