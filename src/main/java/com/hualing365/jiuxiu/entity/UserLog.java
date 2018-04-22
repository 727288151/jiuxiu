/**
 * 
 */
package com.hualing365.jiuxiu.entity;

/**
 * 用户出入直播间日志实体类（对应表t_user_log）
 * @author im.harley.lee@qq.com
 * @since Mar 10, 2018 5:56:49 PM
 */
public class UserLog {
	
	/**
	 * 主键ID（自增）
	 */
	int id;
	
	/**
	 * 房间号
	 */
	int roomId;

	/**
	 * 用户ID
	 */
	int uid;
	
	/**
	 * 靓号ID
	 */
	int accountId;
	
	/**
	 * 用户昵称
	 */
	String nickName;
	
	/**
	 * 等级
	 */
	int wealthLevel;
	
	/**
	 * 等级描述
	 */
	String wealthLevelDesc;
	
	/**
	 * 进入直播间时间
	 */
	String loginDateTime;
	
	/**
	 * 退出直播间时间
	 */
	String logoutDateTime;
	
	/**
	 * 持续时长
	 */
	String duration;
	
	/**
	 * 是否在线
	 */
	boolean online;
	
	/**
	 * 是否隐身
	 */
	boolean hide;
	
	int os;
	
	/**
	 * 备注
	 */
	String remark;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getWealthLevel() {
		return wealthLevel;
	}

	public void setWealthLevel(int wealthLevel) {
		this.wealthLevel = wealthLevel;
	}

	public String getWealthLevelDesc() {
		return wealthLevelDesc;
	}

	public void setWealthLevelDesc(String wealthLevelDesc) {
		this.wealthLevelDesc = wealthLevelDesc;
	}

	public String getLoginDateTime() {
		return loginDateTime;
	}

	public void setLoginDateTime(String loginDateTime) {
		this.loginDateTime = loginDateTime;
	}

	public String getLogoutDateTime() {
		return logoutDateTime;
	}

	public void setLogoutDateTime(String logoutDateTime) {
		this.logoutDateTime = logoutDateTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public int getOs() {
		return os;
	}

	public void setOs(int os) {
		this.os = os;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
